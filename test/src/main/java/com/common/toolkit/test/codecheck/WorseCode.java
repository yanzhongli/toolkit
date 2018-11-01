package com.common.toolkit.test.codecheck;

/**
 * @author ewen
 */
public class WorseCode {

  public Person findUser(String name) {
    if (name==null) {
      return new Person(null);
    }
    return null; // Code to avoid
  }

  private static class Person {

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    private String name;

    public Person(String name) {

      this.name = name;
    }
  }

  public static void main(String[] args) {
    new WorseCode().findUser(null);
  }
}
