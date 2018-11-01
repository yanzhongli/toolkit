package com.common.toolkit.test.config.apollo;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ewen
 */
public final class Constants {

  public static final String name = new String("daixw");

  public static final Map<String, String> map = new HashMap<>();

  public static final List<String> list = Lists.newArrayList();

  public static final Integer age = new Integer("20");

  public static final Sex sex = Sex.MAN;

  public enum Sex {
    MAN, MALE;
  }

}
