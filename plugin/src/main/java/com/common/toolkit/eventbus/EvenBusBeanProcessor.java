package com.common.toolkit.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

/**
 * 注册evenBus note: jdk代理无法驱动事件,建议强制使用cglib,proxy-target-class = true
 *
 * @author ewen
 */
public class EvenBusBeanProcessor implements BeanPostProcessor {

  private static final Logger log = LoggerFactory.getLogger(EvenBusBeanProcessor.class);

  public static final EventBus eventBus = new EventBus();

  private final Map<String, InjectionMetadata> injectionMetadataCache =
      new ConcurrentHashMap<>(64);

  private final Set<Class<? extends Annotation>> referenceAnnotationTypes =
      new LinkedHashSet<>();

  public EvenBusBeanProcessor() {
    this.referenceAnnotationTypes.add(Subscribe.class);
  }

  public void register(Object notBean) {
    eventBus.register(notBean);
  }

  public void registerBean(Object bean) {
    if (AopUtils.isJdkDynamicProxy(bean)) {
      log.warn("bean[{}]为jdk代理类，无法注册事件！", bean);
    } else {
      eventBus.register(bean);
    }
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    try {
      InjectionMetadata metadata = findSubscribeMetadata(beanName, bean.getClass());
      metadata.inject(bean, beanName, null);
    } catch (Throwable ex) {
      throw new BeanCreationException(beanName, "Injection of @Subscribe failed", ex);
    }
    return null;
  }

  private InjectionMetadata findSubscribeMetadata(String beanName, Class<?> clazz) {
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
      synchronized (this.injectionMetadataCache) {
        metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
          metadata = buildSubscribeMetadata(clazz);
          this.injectionMetadataCache.put(cacheKey, metadata);
        }
      }
    }
    return metadata;
  }

  private InjectionMetadata buildSubscribeMetadata(Class<?> clazz) {
    LinkedList<InjectedElement> elements = new LinkedList<>();
    Class<?> targetClass = clazz;
    do {
      LinkedList<InjectedElement> currElements = new LinkedList<>();
      for (Method method : targetClass.getDeclaredMethods()) {
        Annotation annotation = findSubscribeAnnotation(method);
        if (annotation != null) {
          currElements.add(new SubscribeFieldElement(method));
        }
      }
      elements.addAll(0, currElements);
      targetClass = targetClass.getSuperclass();
    }
    while (targetClass != null && targetClass != Object.class);
    return new InjectionMetadata(clazz, elements);
  }

  private Annotation findSubscribeAnnotation(AccessibleObject ao) {
    for (Class<? extends Annotation> type : this.referenceAnnotationTypes) {
      Annotation annotation = AnnotationUtils.getAnnotation(ao, type);
      if (annotation != null) {
        return annotation;
      }
    }
    return null;
  }

  private class SubscribeFieldElement extends InjectedElement {

    public SubscribeFieldElement(Method method) {
      super(method, null);
    }

    @Override
    protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
      Method method = (Method) this.member;
      try {
        eventBus.register(bean);
      } catch (Throwable ex) {
        throw new BeanCreationException("Could not register: " + method, ex);
      }
    }
  }
}
