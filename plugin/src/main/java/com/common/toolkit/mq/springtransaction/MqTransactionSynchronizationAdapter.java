package com.common.toolkit.mq.springtransaction;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 核心事务同步适配器, 当方法上面定义了{@link org.springframework.transaction.annotation.Transactional}注解，
 * 那么当每次状态发生时就会调用本同步适配器.
 */
public class MqTransactionSynchronizationAdapter extends
    TransactionSynchronizationAdapter {

  private MqSessionFactory mySessionFactory;

  public MqTransactionSynchronizationAdapter(MqSessionFactory mySessionFactory) {
    this.mySessionFactory = mySessionFactory;
  }


  @Override
  public void beforeCommit(boolean readOnly) {
    if (!readOnly) {
      MqSession mqSession = (MqSession) TransactionSynchronizationManager
          .getResource(mySessionFactory);
      mqSession.beginTransaction();
    }
  }

  @Override
  public void afterCompletion(int status) {
    MqSession mySession = (MqSession) TransactionSynchronizationManager
        .getResource(mySessionFactory);
    if (STATUS_COMMITTED == status) {
      mySession.commit();
    } else if (STATUS_ROLLED_BACK == status) {
      mySession.rollback();
    }
  }


}  