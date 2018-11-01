package com.common.toolkit.orika;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link MapperFactory} spring factory
 *
 * @author ewen
 */
public class MapperFactoryFactory implements FactoryBean<MapperFactory> {

  @Override
  public MapperFactory getObject() throws Exception {
    return new DefaultMapperFactory.Builder().build();
  }

  @Override
  public Class<?> getObjectType() {
    return MapperFactory.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
