package com.common.toolkit.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonUtil {

  private static Jackson2Converter jsonConverter;

  static {
    jsonConverter = Jackson2Converter.buildNormalConverter();
    jsonConverter.getMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
  }

  public static Jackson2Converter getJsonConverter() {
    return jsonConverter;
  }

  public static <T> T getObjFromStr(String value, Class<T> type) throws IOException {
    if (type == String.class) {
      return type.cast(value);
    } else if (type == Integer.class) {
      return type.cast(Integer.parseInt(value));
    } else if (type == Long.class) {
      return type.cast(Long.parseLong(value));
    } else if (type == Double.class) {
      return type.cast(Double.parseDouble(value));
    } else if (type == Float.class) {
      return type.cast(Float.parseFloat(value));
    } else if (type == Short.class) {
      return type.cast(Short.parseShort(value));
    } else if (type == Boolean.class) {
      return type.cast(Boolean.parseBoolean(value));
    } else {
      return getJsonConverter().toBean(value, type);
    }
  }

  @SuppressWarnings("unchecked")
  public static List toList(String json, Class<?> objClz)
      throws JsonProcessingException, IOException {
    if (json == null) {
      return null;
    }
    if (objClz != null) {
      ObjectMapper mapper = getJsonConverter().getMapper();
      JsonNode rootNode = mapper.readTree(json);
      List list = new LinkedList();
      if (rootNode.size() > 0) {
        for (int i = 0; i < rootNode.size(); i++) {
          list.add(mapper.readValue(rootNode.get(i).traverse(), objClz));
        }
      }
      return list;
    } else {
      return getJsonConverter().toBean(json, List.class);
    }
  }

  public static Object toArray(String json, Class<?> objClz)
      throws JsonProcessingException, IOException {
    if (json == null) {
      return null;
    }
    if (objClz != null) {
      ObjectMapper mapper = getJsonConverter().getMapper();
      JsonNode rootNode = mapper.readTree(json);
      Object arr = Array.newInstance(objClz, rootNode.size());
      if (rootNode.size() > 0) {
        for (int i = 0; i < rootNode.size(); i++) {
          Array.set(arr, i, mapper.readValue(rootNode.get(i).traverse(), objClz));
        }
      }
      return arr;
    } else {
      return getJsonConverter().toBean(json, List.class).toArray();
    }
  }

  @SuppressWarnings("unchecked")
  public static Map toMap(String json, Class<?> keyClz, Class<?> valueClz)
      throws JsonProcessingException, IOException {
    if (json == null) {
      return null;
    }

    Map map = getJsonConverter().toBean(json, Map.class);
    Map resMap = new HashMap();
    for (Object key : map.keySet()) {
      resMap.put(getObjFromStr(key.toString(), keyClz),
          getObjFromStr(String.valueOf(map.get(key)), valueClz));
    }
    return resMap;
  }
}
