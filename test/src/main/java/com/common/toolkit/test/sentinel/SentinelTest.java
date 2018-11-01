package com.common.toolkit.test.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.junit.Test;

/**
 * @author ewen
 */
public class SentinelTest {

  @Test
  public void testSentinel() {
    Entry entry = null;
// 务必保证finally会被执行
    try {
      // 资源名可使用任意有业务语义的字符串
      entry = SphU.entry("自定义资源名");
      /**
       * 被保护的业务逻辑
       */
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } catch (BlockException e1) {
      // 资源访问阻止，被限流或被降级
      // 进行相应的处理操作
    } finally {
      if (entry != null) {
        entry.exit();
      }
    }
  }

}
