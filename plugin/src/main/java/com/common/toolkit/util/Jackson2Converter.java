package com.common.toolkit.util;

import com.common.toolkit.config.apollo.GenericClass;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import org.springframework.util.StringUtils;

public class Jackson2Converter {

  protected ObjectMapper mapper;

  protected Jackson2Converter() {
  }

  protected Jackson2Converter(JsonInclude.Include include) {
    this.mapper = new ObjectMapper();
    // 设置输出时包含属性的风格
    this.mapper.setSerializationInclusion(include);
    //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  protected Jackson2Converter(JsonInclude.Include include, DateFormat format) {
    this.mapper = new ObjectMapper();
    //设置输出时包含属性的风格
    this.mapper.setSerializationInclusion(include);
    //默认的日期转换格式
    this.mapper.setDateFormat(new SimpleDateFormat(format.toString()));
    //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * 创建JSON转换器（转换所有属性）
   *
   * @return JSON转换器
   */
  public static Jackson2Converter buildNormalConverter() {
    return new Jackson2Converter(JsonInclude.Include.ALWAYS);
  }

  /**
   * 创建JSON转换器（转换所有属性）
   *
   * @param format 日期格式
   * @return JSON转换器
   */
  public static Jackson2Converter buildNormalConverter(DateFormat format) {
    return new Jackson2Converter(JsonInclude.Include.ALWAYS, format);
  }

  /**
   * 创建JSON转换器（仅转换非空属性）
   *
   * @return JSON转换器
   */
  public static Jackson2Converter buildNonNullConverter() {
    return new Jackson2Converter(JsonInclude.Include.NON_NULL);
  }

  /**
   * 创建JSON转换器（仅转换非空属性）
   *
   * @param format 日期格式
   * @return JSON转换器
   */
  public static Jackson2Converter buildNonNullConverter(DateFormat format) {
    return new Jackson2Converter(JsonInclude.Include.NON_NULL, format);
  }

  /**
   * 创建JSON转换器（仅转换值被改变的属性）
   *
   * @return JSON转换器
   */
  public static Jackson2Converter buildNonDefaultConverter() {
    return new Jackson2Converter(JsonInclude.Include.NON_DEFAULT);
  }

  /**
   * 创建JSON转换器（仅转换值被改变的属性）
   *
   * @param format 日期格式
   * @return JSON转换器
   */
  public static Jackson2Converter buildNonDefaultConverter(DateFormat format) {
    return new Jackson2Converter(JsonInclude.Include.NON_DEFAULT, format);
  }

  /**
   * 将JSON字符串转化为对象（字符串为null或"null"字符串, 返回null；字符串为"[]", 返回空集合）
   *
   * @param json JSON字符串
   * @param clazz 需要转换的类
   * @return 转换后的类型
   */
  public <T> T toBean(String json, Class<T> clazz) throws IOException {
    if (!StringUtils.hasText(json)) {
      return null;
    }
    return this.mapper.readValue(json, clazz);
  }

  /**
   * 转换成bean，这里clazz2表示泛型类型
   */
  public <T> T toBean(String json, Class<T> clazz1, Class<?> clazz2) throws IOException {
    JavaType javaType = getMapper().getTypeFactory()
        .constructParametrizedType(clazz1, clazz1, clazz2);
    return toBean(json, javaType);
  }

  public <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valClass)
      throws IOException {
    JavaType javaType = getMapper().getTypeFactory()
        .constructMapType(Map.class, keyClass, valClass);
    return toBean(json, javaType);
  }

  /**
   * 转换成bean，这里javaType表示泛型类型
   */
  public <T> T toBean(String json, JavaType javaType) throws IOException {
    return getMapper().readValue(json, javaType);
  }

  /**
   * 转换成bean，这里Type表示泛型类型
   */
  public <T> T toBean(String json, Class<T> clazz, Type type) throws IOException {
    if (!StringUtils.hasText(json)) {
      return null;
    }
    if (type == null) {
      return toBean(json, clazz);
    } else {
      GenericClass genericClass = new GenericClass();
      ClassUtils.getGenericClass(type, genericClass);
      JavaType javaType = null;
      if (genericClass.getRawClass() != null) {
        javaType = getJavaType(this.mapper, genericClass);
      }
      if (javaType != null) {
        return this.mapper.readValue(json, javaType);
      }
    }
    return this.mapper.readValue(json, clazz);
  }


  /**
   * 将对象转换成JSON字符串（如果对象为Null, 返回"null"）
   *
   * @param object 需要转换的对象
   * @return JSON字符串
   */
  public String toJson(Object object) throws IOException {
    return this.mapper.writeValueAsString(object);
  }

  /**
   * 获得指定节点下的值
   *
   * @param node 节点字符串
   * @param path 路径
   * @return 对应的值
   */
  public String getValue4Path(String node, String path) throws IOException {
    return this.mapper.readTree(node).path(path).asText();
  }

  /**
   * 获得指定节点下的值
   *
   * @param node 节点对象
   * @param path 路径
   * @return 对应的值
   */
  public String getJsonPathValue(JsonNode node, String path) {
    return node.path(path).asText();
  }

  /**
   * 获取指定节点文本内容
   *
   * @param node 节点对象
   * @param nodeName 节点名称
   * @return 节点内容
   */
  public String getNodeText(JsonNode node, String nodeName) {
    String text = "";
    if (node != null) {
      JsonNode tmp = node.path(nodeName);
      if (tmp != null) {
        text = tmp.asText();
      }
    }
    if (text == null) {
      text = "";
    }
    return text;
  }

  /**
   * 根据路径获取值
   *
   * @param node 节点对象
   * @param path 格式: key1/key2/key3
   * @return 对应节点的值
   */
  public String getNoteValue(JsonNode node, String path) {
    if (!StringUtils.isEmpty(path)) {
      if (path.startsWith("/")) {
        path = path.substring(1, path.length());
      }
      String[] nodeNames = path.split("/");
      for (int i = 0; i < nodeNames.length; i++) {
        node = node.findPath(nodeNames[i]);
        if (null == node) {
          return null;
        }
        if (i == nodeNames.length - 1) {
          return node.asText();
        }
      }
    }
    return null;
  }

  /**
   * 排除某些字段不输出.如:<br/> TestBean里有id,name两个属性(实际上是以get方法为准)只想输出name属性<br/>
   * <code>
   *
   * @JsonFilter("testFilter")</br> class TestBean{</br> }</br> JsonConverter.addFilter("testFilter",new
   * String[]{"id"});
   * </code>
   */
  public void addFilter(String filterName, String[] propertys) {
    if (mapper == null) {
      throw new RuntimeException("请先实例化ObjectMapper对象");
    }
    SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider();
    simpleFilterProvider
        .addFilter(filterName, SimpleBeanPropertyFilter.serializeAllExcept(propertys));
    mapper.setFilters(simpleFilterProvider);
  }

  /**
   * 取出Mapper做进一步的设置或使用其他序列化API
   */
  public ObjectMapper getMapper() {
    return this.mapper;
  }

  public static JavaType getJavaType(ObjectMapper mapper, GenericClass genericClass) {
    JavaType javaType;
    Class<?> clazz = genericClass.getRawClass();
    GenericClass[] genericClasses = genericClass.getGenericClasses();
    JavaType[] jts;
    if (genericClasses != null) {
      jts = new JavaType[genericClasses.length];
      for (int i = 0; i < genericClasses.length; i++) {
        jts[i] = getJavaType(mapper, genericClasses[i]);
      }
    } else {
      jts = new JavaType[]{mapper.getTypeFactory().constructType(Object.class),
          mapper.getTypeFactory().constructType(Object.class)};
    }
    if (clazz.isArray()) {
      //javaType = ArrayType.construct(SimpleType.constructUnsafe(clazz.getComponentType()),null,null);
      javaType = mapper.getTypeFactory().constructArrayType(clazz.getComponentType());
    } else if (Collection.class.isAssignableFrom(clazz)) {
      //javaType = CollectionType.construct(java.util.ArrayList.class,jts[0]);
      javaType = mapper.getTypeFactory().constructCollectionType(Collection.class, jts[0]);
    } else if (Map.class.isAssignableFrom(clazz)) {
      javaType = mapper.getTypeFactory().constructMapType(Map.class, jts[0], jts[1]);
    } else {
      javaType = mapper.getTypeFactory().constructType(clazz);
    }
    return javaType;
  }
}
