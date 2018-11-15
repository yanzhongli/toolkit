package com.common.toolkit.test.auto;

import com.google.auto.value.AutoValue;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * 自动导出抽象类的实现类，包括equals和hashcode方法
 *
 * @author ewen
 */
@AutoValue
abstract class Animal {

  static Animal create(String name, int numberOfLegs) {
    // See "How do I...?" below for nested classes.
    return new AutoValue_Animal(name, numberOfLegs);
  }

  abstract String name();

  abstract int numberOfLegs();
}

public class TestAutoValue extends TestCase {

  @Test
  public void testAnimal() {
    Animal dog = Animal.create("dog", 4);
    assertEquals("dog", dog.name());
    assertEquals(4, dog.numberOfLegs());

    // You probably don't need to write assertions like these; just illustrating.
    assertTrue(Animal.create("dog", 4).equals(dog));
    assertFalse(Animal.create("cat", 4).equals(dog));
    assertFalse(Animal.create("dog", 2).equals(dog));

    assertEquals("Animal{name=dog, numberOfLegs=4}", dog.toString());
  }
}
