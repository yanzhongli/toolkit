package com.common.toolkit.orika;

import com.common.toolkit.logger.InitializationException;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * 这是一个经常出现的场景：bean与bean之间的映射。 orika优点：速度快(基于字节码)，能多层级映射，使用方便
 *
 * @author ewen
 */
public class OrikaBeanMapper extends ConfigurableMapper {

  private ApplicationContext applicationContext;
  private MapperFactory mapperFactory;
  private boolean init;

  public OrikaBeanMapper() {
    this(false);
  }

  public OrikaBeanMapper(boolean init) {
    super(init);
    this.init = init;
  }

  @Autowired
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    init();
  }

  @Override
  protected void configure(MapperFactory factory) {
    super.configure(factory);
    if (!init) {
      throw new InitializationException("MapperFactory 未初始化！");
    }
    this.mapperFactory = factory;
    if (this.applicationContext != null) {
      this.applicationContext.getBeansOfType(Mapper.class).values().stream()
          .forEach(this::addMapper);
      this.applicationContext.getBeansOfType(Converter.class).values().stream()
          .forEach(this::addConverter);
    }
  }

  private void addMapper(Mapper<?, ?> mapper) {
    this.mapperFactory.registerMapper(mapper);
  }

  private void addConverter(Converter<?, ?> converter) {
    this.mapperFactory.getConverterFactory().registerConverter(converter);
  }


}
