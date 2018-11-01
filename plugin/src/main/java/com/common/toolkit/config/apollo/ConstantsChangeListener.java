package com.common.toolkit.config.apollo;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * 有一种场景,开关或配置等写在常量文件中,需要能够由配置中心下发
 *
 * @author ewen
 */
public class ConstantsChangeListener {

  private static final Logger logger = LoggerFactory.getLogger(ConstantsLoad.class);

  private String constantsNamespace;

  private ConstantsProperties constantsProperties;

  public ConstantsChangeListener(ConstantsProperties constantsProperties) {
    this(constantsProperties, "Constants.properties");
  }

  public ConstantsChangeListener(ConstantsProperties constantsProperties,
      String constantsNamespace) {
    this.constantsProperties = constantsProperties;
    this.constantsNamespace = constantsNamespace;
  }

  @ApolloConfigChangeListener
  public void onChange(ConfigChangeEvent changeEvent) {
    if (!constantsNamespace.equals(changeEvent.getNamespace())) {
      return;
    }
    changeEvent.changedKeys().forEach(key -> updateConstants(changeEvent, key));
  }

  public void updateConstants(ConfigChangeEvent changeEvent, String key) {
    ConfigChange configChange = changeEvent.getChange(key);
    String newValue = configChange.getNewValue();
    updateConstants(new KeyValue<>(key, newValue));
  }

  public void updateConstants(KeyValue<String> keyValue) {
    if (StringUtils.isEmpty(constantsProperties.getBasePackage()) || StringUtils
        .isEmpty(constantsProperties.getClassPattern())) {
      return;
    }
    try {
      ConstantsLoad
          .writeConstant(null, keyValue, constantsProperties.getBasePackage(),
              constantsProperties.getClassPattern() + ".class");
    } catch (Exception e) {
      logger.error("更新常量配置出错！", e);
    }
  }

}
