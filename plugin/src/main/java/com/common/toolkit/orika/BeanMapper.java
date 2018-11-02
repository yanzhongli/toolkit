package com.common.toolkit.orika;

import java.util.List;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

/**
 * 简单封装orika, 实现深度的BeanOfClasssA&lt;-&gt;BeanOfClassB复制 不要是用Apache Common
 * BeanUtils进行类复制，每次就行反射查询对象的属性列表, 非常缓慢.
 */
public class BeanMapper {

  private static MapperFacade mapper;

  static {
    MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    mapper = mapperFactory.getMapperFacade();
  }

  /**
   * 简单的复制出新类型对象. 通过source.getClass() 获得源Class
   *
   * @param source source
   * @param destinationClass destinationClass
   * @param <S> S
   * @param <D> D
   * @return D
   */
  public static <S, D> D map(S source, Class<D> destinationClass) {
    return mapper.map(source, destinationClass);
  }

  /**
   * 极致性能的复制出新类型对象. 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
   *
   * @param source source
   * @param sourceType sourceType
   * @param destinationType destinationType
   * @param <S> S
   * @param <D> D
   * @return D
   */
  public static <S, D> D map(S source, Type<S> sourceType, Type<D> destinationType) {
    return mapper.map(source, sourceType, destinationType);
  }

  /**
   * 简单的复制出新对象列表到ArrayList
   *
   * 不建议使用mapper.mapAsList(Iterable&lt;S&gt;,Class&lt;D&gt;)接口, sourceClass需要反射，实在有点慢
   */
  /**
   * 简单的复制出新对象列表到ArrayList
   *
   * 不建议使用mapper.mapAsList(Iterable&lt;S&gt;,Class&lt;D&gt;)接口, sourceClass需要反射，实在有点慢
   *
   * @param sourceList sourceList
   * @param sourceClass sourceClass
   * @param destinationClass destinationClass
   * @param <S> S
   * @param <D> D
   * @return List&lt;D&gt;
   */
  public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<S> sourceClass,
      Class<D> destinationClass) {
    return mapper.mapAsList(sourceList, TypeFactory.valueOf(sourceClass),
        TypeFactory.valueOf(destinationClass));
  }

  /**
   * 极致性能的复制出新类型对象到ArrayList.
   *
   * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
   *
   * @param sourceList sourceList
   * @param sourceType sourceType
   * @param destinationType destinationType
   * @param <S> S
   * @param <D> D
   * @return List&lt;D&gt;
   */
  public static <S, D> List<D> mapList(Iterable<S> sourceList, Type<S> sourceType,
      Type<D> destinationType) {
    return mapper.mapAsList(sourceList, sourceType, destinationType);
  }

  /**
   * 简单复制出新对象列表到数组
   *
   * 通过source.getComponentType() 获得源Class
   *
   * @param destination destination
   * @param source source
   * @param destinationClass destinationClass
   * @param <S> S
   * @param <D> D
   * @return array of D
   */
  public static <S, D> D[] mapArray(final D[] destination, final S[] source,
      final Class<D> destinationClass) {
    return mapper.mapAsArray(destination, source, destinationClass);
  }

  /**
   * 极致性能的复制出新类型对象到数组
   *
   * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
   *
   * @param destination destination
   * @param source source
   * @param sourceType sourceType
   * @param destinationType destinationType
   * @param <S> S
   * @param <D> D
   * @return array of D
   */
  public static <S, D> D[] mapArray(D[] destination, S[] source, Type<S> sourceType,
      Type<D> destinationType) {
    return mapper.mapAsArray(destination, source, sourceType, destinationType);
  }

  /**
   * 预先获取orika转换所需要的Type，避免每次转换.
   *
   * @param rawType rawType
   * @param <E> Type
   * @return Type&lt;E&gt;
   */
  public static <E> Type<E> getType(final Class<E> rawType) {
    return TypeFactory.valueOf(rawType);
  }
}
