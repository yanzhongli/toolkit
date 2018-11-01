package com.common.toolkit.httpservice;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.dubbo.spring.boot.DubboProperties;
import com.common.toolkit.spring.SpringContextHolder;
import com.common.toolkit.util.ClassUtils;
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 扫描dubbo sdk并映射服务编码.
 *
 * @author ewen
 */
@Getter
@Setter
public class DubboServiceContainer extends ClassPathBeanDefinitionScanner implements
    ServiceContainer {

  public final static Logger logger = LoggerFactory.getLogger(DubboServiceContainer.class);

  private final Map<String, ServiceEntity> serviceMapper = new HashMap<>();

  private DubboProperties dubboProperties;

  private final DubboClient dubboClient = new DubboClient(dubboProperties);

  private String scanPackage;

  public void buildServiceCodeMapper(String className) {
    try {
      Class<?> clazz = org.springframework.util.ClassUtils.getDefaultClassLoader()
          .loadClass(className);
      Method[] methods = clazz.getMethods();
      for (Method method : methods) {
        ServiceEntity srv = parseService(method);
        if (serviceMapper.containsKey(srv.getServiceCode())) {
          ServiceEntity serviceEntity = serviceMapper.get(srv.getServiceCode());
          throw new RuntimeException(String.format("服务编码重复，接口%s与接口%s存在相同的方法名称%s",
              className, serviceEntity.getServiceType().getName(), method.getName()));
        }
        serviceMapper.put(srv.getServiceCode(), srv);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("服务编码映射失败", e);
    }
  }

  public ServiceEntity parseService(Method method) {
    ServiceEntity srv = new ServiceEntity();
    srv.setServiceType(method.getDeclaringClass());
    srv.setServiceCode(
        ServiceContainer
            .getServiceCode(null, null, (s, m) -> method.getDeclaringClass() + method.getName()));
    MethodEntity methodEntity = new MethodEntity();
    methodEntity.setMethodName(method.getName());
    methodEntity.setParameterTypes(ClassUtils.convertClass2String(method.getParameterTypes()));
    srv.setMethodEntity(methodEntity);
    return srv;
  }

  public DubboServiceContainer(BeanDefinitionRegistry registry) {
    super(registry, false);
    addIncludeFilter((metadataReader, metadataReaderFactory) -> {
      AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
      if (metadata.isInterface()) {
        buildServiceCodeMapper(metadata.getClassName());
        return true;
      }
      return false;
    });
    if (StringUtils.isBlank(this.scanPackage)) {
      this.scanPackage = "com.yame.rpc.api";
    }
    String[] scanPks = org.springframework.util.StringUtils
        .tokenizeToStringArray(this.scanPackage,
            ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
    this.doScan(scanPks);
  }

  public DubboServiceContainer() {
    this((BeanDefinitionRegistry) SpringContextHolder.getApplicationContext()
        .getAutowireCapableBeanFactory());
  }

  @Override
  public void register(String serviceCode, ServiceEntity serviceEntity) {
    ServiceEntity se = serviceMapper.putIfAbsent(serviceCode, serviceEntity);
    if (se != null) {
      logger.warn(String.format("服务编码%s与接口%s重复！", serviceCode, se.getServiceType()));
    }
  }

  @Override
  public ServiceEntity getService(String serviceCode) {
    return serviceMapper.get(serviceCode);
  }

  @Override
  public void destroy() {
    serviceMapper.clear();
    DubboClient.refMap.values().forEach(referenceBean -> referenceBean.destroy());
    DubboClient.refMap.clear();
  }

  @Override
  public ServiceInvoker<DubboClient> getInvoker() {
    return new ServiceInvoker<>(dubboClient);
  }

  static class DubboClient implements Client {

    private DubboProperties dubboProperties;

    static Map<String, ReferenceBean> refMap = Maps.newConcurrentMap();

    public DubboClient(DubboProperties dubboProperties) {
      this.dubboProperties = dubboProperties;
    }

    @Override
    public Object invoke(ServiceEntity serviceEntity, Object arg) {
      String serviceCode = serviceEntity.getServiceCode();
      ReferenceBean referenceBean = refMap.putIfAbsent(serviceCode, new ReferenceBean());
      if (referenceBean == null) {
        referenceBean = refMap.get(serviceCode);
        String className = serviceCode.substring(0, serviceCode.lastIndexOf("."));
        initConsumerBean(referenceBean, className);
      }
      GenericService genericService = (GenericService) referenceBean.get();
      MethodEntity methodEntity = serviceEntity.getMethodEntity();
      Object result = genericService
          .$invoke(methodEntity.getMethodName(), methodEntity.getParameterTypes(),
              (Object[]) arg);
      return result;
    }

    private ReferenceBean initConsumerBean(ReferenceBean referenceBean, String className) {
      referenceBean.setInterface(className);
      referenceBean.setApplication(
          getAppConfig(SpringContextHolder.getApplicationContext().getEnvironment()));
      referenceBean.setModule(getModuleConfig());
      referenceBean.setRegistries(getRegistryConfig());
      referenceBean.setMonitor(getMonitorConfig());
      referenceBean.setConsumer(getConsumerConfig());
      referenceBean.setGeneric(true);
      referenceBean.setApplicationContext(SpringContextHolder.getApplicationContext());
      return referenceBean;
    }

    private ApplicationConfig getAppConfig(Environment environment) {
      ApplicationConfig applicationConfig = dubboProperties.getApplication();
      if (applicationConfig == null) {
        applicationConfig = new ApplicationConfig();
        applicationConfig.setName(environment.getProperty("spring.application.name"));
      }
      return applicationConfig;
    }

    private ModuleConfig getModuleConfig() {
      return dubboProperties.getModule();
    }

    private List<RegistryConfig> getRegistryConfig() {
      List<RegistryConfig> registryList = null;
      RegistryConfig registry = dubboProperties.getRegistry();
      if (registry != null) {
        registryList = new ArrayList<>();
        registryList.add(registry);
      }
      return registryList;
    }

    private MonitorConfig getMonitorConfig() {
      return dubboProperties.getMonitor();
    }

    private ConsumerConfig getConsumerConfig() {
      return dubboProperties.getConsumer();
    }
  }
}
