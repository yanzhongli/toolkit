package com.common.toolkit.test.config.apollo;

import com.common.toolkit.config.apollo.ConstantsChangeListener;
import com.common.toolkit.config.apollo.ConstantsProperties;
import com.common.toolkit.config.apollo.KeyValue;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author ewen
 */
public class ConstantsLoadTest extends TestCase {

  ConstantsChangeListener changeListener;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ConstantsProperties constantsProperties = new ConstantsProperties();
    constantsProperties.setClassPattern("Constants");
    constantsProperties.setBasePackage("com.common.toolkit");
    changeListener = new ConstantsChangeListener(constantsProperties);
  }

  @Test
  public void testLoad() {

    changeListener.updateConstants(new KeyValue<>("name", "ewen"));
    assertEquals(Constants.name, "ewen");

    changeListener.updateConstants(new KeyValue<>("map", "{'key':'value'}"));
    assertTrue(Constants.map.containsKey("key"));

    changeListener.updateConstants(new KeyValue<>("list", "['key']"));
    assertTrue(Constants.list.contains("key"));

    changeListener.updateConstants(new KeyValue<>("sex", "MALE"));
    assertEquals(Constants.sex.name(), "MALE");
  }

}
