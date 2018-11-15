package com.common.toolkit.test.auto;

import com.google.auto.service.AutoService;

/**
 * 通过javac的hook{@link javax.annotation.processing.Processor}自动导出{@link java.util.ServiceLoader}自动装配所需要的文件
 *
 * @author ewen
 */
@AutoService(ExportSpi.class)
public class ExportSpi {

  public void export() {
    //
  }
}
