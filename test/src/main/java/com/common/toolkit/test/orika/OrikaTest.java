package com.common.toolkit.test.orika;

import com.common.toolkit.orika.OrikaBeanMapper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeBuilder;
import org.junit.Test;

/**
 * @author ewen
 */
public class OrikaTest extends TestCase {

  /**
   * test1:相同属性名,集合映射
   */
  @Test
  public void test1() {
    MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    mapperFactory.classMap(Person.class, PersonDto.class) // <2>
        .field("sex", "sexy")
//        .field("addresses{}", "addresses{[0]}")
        .byDefault() //<1>
        .register();

    Person person = new Person();
    person.setName("daixw");
    person.setAge(20);
    person.setBirthDate(new Date());
    person.setSex("yes");
    person.setAddresses(Lists.asList(new Address(), new Address[]{new Address()}));
//一顿赋值
    System.out.println(person);

    PersonDto personDto = mapperFactory.getMapperFacade().map(person, PersonDto.class);
    System.out.println(personDto);
  }

  /**
   *
   */
  @Test
  public void test2() {
    OrikaBeanMapper orikaBeanMapper = new OrikaBeanMapper() {
      @Override
      protected void configure(MapperFactory factory) {
        super.configure(factory);
        factory.classMap(Person.class, PersonDto.class) // <2>
            .field("sex", "sexy")
//        .field("addresses{}", "addresses{[0]}")
            .byDefault() //<1>
            .register();
      }
    };

    Person person = new Person();
    person.setName(null);
    person.setAge(20);
    person.setBirthDate(new Date());
    person.setSex("yes");
    person.setAddresses(Lists.asList(new Address(), new Address[]{new Address()}));
    PersonDto personDto = orikaBeanMapper.map(person, PersonDto.class);
    System.out.println(person + "\n" + personDto);
  }

  /**
   * 集合映射集合
   */
  @Test
  public void test3() {
    OrikaBeanMapper orikaBeanMapper = new OrikaBeanMapper();
    List<Person> persons = new ArrayList<>();
    List<PersonDto> personDtos = orikaBeanMapper.mapAsList(persons, PersonDto.class);
    System.out.println(personDtos);
  }

  /**
   * 默认支持递归映射
   */
  @Test
  public void test4() {

  }

  /**
   * 泛型映射
   */
  @Test
  public void test5() {
    MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    Response<String> response = new Response<>();
    response.setData("test generic");
    ResponseDto<String> responseDto = mapperFactory.getMapperFacade()
        .map(response, ResponseDto.class);// *
    System.out.println("test generic".equals(responseDto.getData()));

    Response<Person> response2 = new Response<>();
    Person person = new Person();
    person.setName("test generic");
    response2.setData(person);

    //泛型映射需要传入类型
    Type<Response<Person>> fromType = new TypeBuilder<Response<Person>>() {
    }.build();
    Type<Response<PersonDto>> toType = new TypeBuilder<Response<PersonDto>>() {
    }.build();
    Response<PersonDto> responseDto2 = mapperFactory.getMapperFacade()
        .map(response2, fromType, toType);
    System.out.println(responseDto2.getData() instanceof PersonDto);
    System.out.println(responseDto2);
  }

  /**
   * 拷贝
   */
  @Test
  public void test6() {
    MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    Person person = new Person();
    person.setAddresses(Lists.asList(new Address(), new Address[]{
        new Address()
    }));
    PersonDto personDto = mapperFactory.getMapperFacade().map(person, PersonDto.class);
    //深拷贝
    System.out.println(personDto.getAddresses().hashCode() == person.getAddresses().hashCode());
  }

  public static class Response<T> {

    private T data;

    public T getData() {
      return data;
    }

    public void setData(T data) {
      this.data = data;
    }
  }

  public static class ResponseDto<T> {

    private T data;

    public T getData() {
      return data;
    }

    public void setData(T data) {
      this.data = data;
    }
  }

  public static class Person {

    private String sex;
    private String name;
    private int age;
    private Date birthDate;
    List<Address> addresses; // <1>

    public String getSex() {
      return sex;
    }

    public void setSex(String sex) {
      this.sex = sex;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }

    public Date getBirthDate() {
      return birthDate;
    }

    public void setBirthDate(Date birthDate) {
      this.birthDate = birthDate;
    }

    public List<Address> getAddresses() {
      return addresses;
    }

    public void setAddresses(List<Address> addresses) {
      this.addresses = addresses;
    }
  }

  public static class PersonDto {

    private String sexy;
    private String name;
    private int age;
    private Date birthDate;
    List<AddressDto> addresses; // <1>

    public String getSexy() {
      return sexy;
    }

    public void setSexy(String sexy) {
      this.sexy = sexy;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }

    public Date getBirthDate() {
      return birthDate;
    }

    public void setBirthDate(Date birthDate) {
      this.birthDate = birthDate;
    }

    public List<AddressDto> getAddresses() {
      return addresses;
    }

    public void setAddresses(List<AddressDto> addresses) {
      this.addresses = addresses;
    }
  }

  public static class Address {

    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class AddressDto {

    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

}
