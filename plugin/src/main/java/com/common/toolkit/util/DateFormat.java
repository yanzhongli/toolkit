package com.common.toolkit.util;

/**
 * 日期格式
 */
public enum DateFormat {
  /**
   * 标准格式（yyyy-MM-dd）
   */
  DATE("yyyy-MM-dd"),
  /**
   * 标准格式（yyyy-MM-dd HH:mm:ss）
   */
  DATETIME("yyyy-MM-dd HH:mm:ss"),
  /**
   * 斜杠格式（yyyy/MM/dd）
   */
  SLASHDATE("yyyy/MM/dd"),
  /**
   * 斜杠格式（yyyy/MM/dd HH:mm:ss）
   */
  SLASHDATETIME("yyyy/MM/dd HH:mm:ss"),
  /**
   * 中文格式（yyyy年MM月dd日）
   */
  CHINESEDATE("yyyy年MM月dd日"),
  /**
   * 中文格式（yyyy年MM月dd日 HH时mm分ss秒）
   */
  CHINESEDATETIME("yyyy年MM月dd日 HH时mm分ss秒"),
  /**
   * 串行格式（yyyyMMdd）
   */
  SERIALNUMBERDATE("yyyyMMdd"),
  /**
   * 串行格式（yyyyMMddHHmmss）
   */
  SERIALNUMBERDATETIME("yyyyMMddHHmmss");

  private String format;

  private DateFormat(String format) {
    this.format = format;
  }

  @Override
  public String toString() {
    return this.format;
  }
}