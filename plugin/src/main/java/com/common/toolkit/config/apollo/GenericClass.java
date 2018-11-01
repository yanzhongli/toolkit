package com.common.toolkit.config.apollo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenericClass {

  private Class<?> rawClass;

  private GenericClass[] genericClasses;

}
