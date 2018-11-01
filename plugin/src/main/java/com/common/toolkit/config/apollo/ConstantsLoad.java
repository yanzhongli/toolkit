package com.common.toolkit.config.apollo;

import com.common.toolkit.util.ClazzUtil;
import com.common.toolkit.util.FinalObjectUtil;
import com.common.toolkit.util.JsonUtil;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jooq.lambda.Unchecked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

/**
 * 常量属性值更新。可写入的属性类型为：基本类型包装类、list、Map、枚举类、数组。 不支持宏变量。
 *
 * @author ewen
 */
public class ConstantsLoad {

  private static final Logger log = LoggerFactory.getLogger(ConstantsLoad.class);

  public static void writeConstant(ClassLoader classLoader, KeyValue<String> keyValue,
      String basePkg, String classPattern) {
    ClassLoader cl =
        classLoader == null ? ClassUtils.getDefaultClassLoader() : classLoader;
    String[] classPatterns = classPattern.split(",");
    List<String> constClassNameList = Lists.newLinkedList();
    Arrays.stream(classPatterns).forEach(
        Unchecked.consumer(clzPattern -> {
          List<String> constClzNameList = ClazzUtil.findClassName(basePkg, clzPattern);
          if (!CollectionUtils.isEmpty(constClzNameList)) {
            constClassNameList.addAll(constClzNameList);
          }
        })
    );
    constClassNameList.forEach(
        Unchecked.consumer(constClassName -> {
          Class consClass = cl.loadClass(constClassName);
          Arrays.stream(consClass.getFields())
              .filter(field -> Objects.equals(field.getName(), keyValue.getKey())).forEach(
              Unchecked.consumer(field -> updateConstants(field, keyValue.getValue()))
          );
        })
    );
  }

  private static void updateConstants(Field field, String value) throws Exception {
    String propValue = value;
    if (propValue != null) {
      Class clazz = field.getType();
      Object oldValue = field.get(null);
      //是覆盖还是添加?
      if (clazz.isAssignableFrom(List.class)) {
        List list = JsonUtil.toList(propValue, ClazzUtil.getTypeOfList(field));
        List oldList = (List) oldValue;
        oldList.clear();
        oldList.addAll(list);
      } else if (Map.class.isAssignableFrom(field.getType())) {
        Map valueMap = JsonUtil.toMap(propValue,
            ClazzUtil.getTypeOfKeyInMap(field),
            ClazzUtil.getTypeOfValueInMap(field));
        Map oldValueMap = (Map) oldValue;
        oldValueMap.clear();
        oldValueMap.putAll(valueMap);
      } else if (field.getType().isEnum()) {
        Object enumConstant = ClazzUtil.getObjFromEnum(field.getType(),
            propValue);
        if (enumConstant != null) {
          FinalObjectUtil.setValue(field, enumConstant);
        }
      } else if (field.getType().isArray()) {
        Object arrObj = JsonUtil.toArray(propValue,
            ClazzUtil.getTypeOfObjInArray(field));
        FinalObjectUtil.setValue(field, arrObj);
      } else {
        Object newValue = ClazzUtil.cast(propValue,
            oldValue.getClass());
        if (oldValue == null || !oldValue.equals(newValue)) {
          FinalObjectUtil.setValue(field, newValue);
        }
      }
    }
  }

}
