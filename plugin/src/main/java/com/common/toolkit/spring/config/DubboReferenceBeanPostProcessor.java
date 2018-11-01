package com.common.toolkit.spring.config;

import com.yame.rpc.context.ServiceContainer;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * 对使用{@link ServiceContainer}获取服务的方式，适配dubbo{@link DubboReference}
 *
 * @author ewen
 */
public class DubboReferenceBeanPostProcessor extends
    InstantiationAwareBeanPostProcessorAdapter implements
    BeanFactoryAware {

  private final Log logger = LogFactory.getLog(getClass());

  private final Map<String, InjectionMetadata> injectionMetadataCache =
      new ConcurrentHashMap<>(64);

  private final Set<Class<? extends Annotation>> referenceAnnotationTypes =
      new LinkedHashSet<>();

  private ConfigurableListableBeanFactory beanFactory;

  private ServiceContainer serviceContainer;

  public DubboReferenceBeanPostProcessor() {
    this.referenceAnnotationTypes.add(DubboReference.class);
  }

  @Override
  public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds,
      Object bean, String beanName) throws BeansException {
    InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass());
    try {
      metadata.inject(bean, beanName, pvs);
    } catch (Throwable ex) {
      throw new BeanCreationException(beanName, "Injection of reference dependencies failed", ex);
    }
    return pvs;
  }

  private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz) {
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
      synchronized (this.injectionMetadataCache) {
        metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
          metadata = buildDubboReferenceMetadata(clazz);
          this.injectionMetadataCache.put(cacheKey, metadata);
        }
      }
    }
    return metadata;
  }

  private Annotation findDubboReferenceAnnotation(AccessibleObject ao) {
    for (Class<? extends Annotation> type : this.referenceAnnotationTypes) {
      Annotation annotation = AnnotationUtils.getAnnotation(ao, type);
      if (annotation != null) {
        return annotation;
      }
    }
    return null;
  }

  private InjectionMetadata buildDubboReferenceMetadata(Class<?> clazz) {
    LinkedList<InjectedElement> elements = new LinkedList<>();
    Class<?> targetClass = clazz;
    do {
      LinkedList<InjectedElement> currElements = new LinkedList<>();
      for (Field field : targetClass.getDeclaredFields()) {
        Annotation annotation = findDubboReferenceAnnotation(field);
        if (annotation != null) {
          if (Modifier.isStatic(field.getModifiers())) {
            if (logger.isWarnEnabled()) {
              logger.warn("DubboReference annotation is not supported on static fields: " + field);
            }
            continue;
          }
          currElements.add(new DubboReferenceFieldElement(field, annotation));
        }
      }
      elements.addAll(0, currElements);
      targetClass = targetClass.getSuperclass();
    }
    while (targetClass != null && targetClass != Object.class);
    return new InjectionMetadata(clazz, elements);
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
      throw new IllegalArgumentException(
          "DubboDubboReferenceBeanPostProcessor requires a ConfigurableListableBeanFactory");
    }
    this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    try {
      this.serviceContainer = beanFactory
          .getBean(lowerFirstChar(ServiceContainer.class.getSimpleName()), ServiceContainer.class);
    } catch (BeansException e) {
      try {
        this.serviceContainer = beanFactory
            .getBean(ServiceContainer.class);
      } catch (BeansException e2) {
        ServiceContainer serviceContainer = new ServiceContainer();
        try {
          serviceContainer.init();
        } catch (Exception e3) {
          throw new BeanInitializationException("ServiceContainer init failure！", e3);
        }
        this.beanFactory
            .registerSingleton(ServiceContainer.class.getSimpleName(), serviceContainer);
        this.serviceContainer = serviceContainer;
      }
    }
  }

  private String lowerFirstChar(String input) {
    if (StringUtils.isEmpty(input)) {
      return input;
    }
    char ch = input.charAt(0);
    if ((ch >= 'A' && ch <= 'Z')) {
      ch = (char) (ch + 32);
      input = ch + input.substring(1);
    }
    return input;
  }

  private class DubboReferenceFieldElement extends InjectedElement {

    private DubboReference reference;

    public DubboReferenceFieldElement(Field field, Annotation annotation) {
      super(field, null);
      this.reference = (DubboReference) annotation;
    }

    @Override
    protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
      Field field = (Field) this.member;
      try {
        Object value = getService(this.reference, field.getType());
        if (value != null) {
          ReflectionUtils.makeAccessible(field);
          field.set(bean, value);
        }
      } catch (Throwable ex) {
        throw new BeanCreationException("Could not autowire field: " + field, ex);
      }
    }

    private Object getService(DubboReference reference, Class<?> type) {
      if (StringUtils.isEmpty(reference.group()) && StringUtils.isEmpty(reference.version())) {
        return serviceContainer.getService(type);
      }
      if (!StringUtils.isEmpty(reference.group()) && !StringUtils.isEmpty(reference.version())) {
        return serviceContainer.getService(reference.group(), type, reference.version());
      }
      if (StringUtils.isEmpty(reference.group())) {
        return serviceContainer.getService(type, reference.version());
      }
      if (StringUtils.isEmpty(reference.version())) {
        return serviceContainer.getService(reference.group(), type);
      }
      return null;
    }
  }
}
