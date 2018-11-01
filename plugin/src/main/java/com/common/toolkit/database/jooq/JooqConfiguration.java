package com.common.toolkit.database.jooq;

/**
 * 小清新 sql工具
 *
 * 使用步骤：
 * 1.引入spring-boot-starter-jooq 自动配置.
 * 2.配置{@link org.springframework.boot.autoconfigure.jooq.JooqProperties}
 * 3.添加maven插件，具体见pom.xml,执行mvn clean compile
 * 4.bean注入{@link org.jooq.DSLContext}，根据表生成的类使用其API。
 *
 * 具体见测试用例
 *
 * @author ewen
 */
public class JooqConfiguration {

}
