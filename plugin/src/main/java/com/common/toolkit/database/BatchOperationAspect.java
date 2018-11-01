package com.common.toolkit.database;

import com.google.common.collect.Lists;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 数据库批处理操作分区执行。 适合场景：mysql设置了最长语句字节数，可在配置分区执行。
 */
@Aspect
public class BatchOperationAspect {

  public @interface BatchOpt {

    /**
     * 批次大小
     */
    int batchSize();
  }

  /**
   * 带有{@link BatchOpt}的注解方法
   */
  @Pointcut("@annotation(BatchOpt)&& @annotation(batch) ")
  public void batchMethod(BatchOpt batch) {
  }

  @Pointcut("execution(public * com.yame.*.*(..)) && batchMethod(batch)")
  public void intReturnMethod(BatchOpt batch) {
  }

  /**
   * 带有{@link List}参数的方法
   */
  @Pointcut("args(java.util.List)&& args(list)")
  public void listArgument(List list) {
  }

  @Around("intReturnMethod(batch)&&listArgument(list)")
  public Object batchExecuteReturnInt(ProceedingJoinPoint joinPoint, BatchOpt batch, List list)
      throws Throwable {
    int batchSize = batch.batchSize();
    int rltTotal = 0;
    List<List> partitions = Lists.partition(list, batchSize);
    for (List partition : partitions) {
      int rlt = (int) joinPoint.proceed(new Object[]{partition});
      rltTotal += rlt;
    }
    return rltTotal;
  }

}
