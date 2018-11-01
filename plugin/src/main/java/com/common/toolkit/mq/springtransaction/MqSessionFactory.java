package com.common.toolkit.mq.springtransaction;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * mq session 工厂
 *
 * @author ewen
 */
public class MqSessionFactory {

  private MqClient client;

  public MqSessionFactory(MqClient client) {
    this.client = client;
  }

  public MqSession getSession() {
    if (TransactionSynchronizationManager.hasResource(this)) {
      return getCurrentSession();
    } else {
      return openSession();
    }
  }

  private MqSession openSession() {
    MqSession mySession = new MqSession(client);
    mySession.setSessionId(System.currentTimeMillis());

    TransactionSynchronization transactionSynchronization = new MqTransactionSynchronizationAdapter(
        this);
    TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);

    TransactionSynchronizationManager.bindResource(this, mySession);
    return mySession;
  }

  private MqSession getCurrentSession() {
    MqSession mySession = (MqSession) TransactionSynchronizationManager.getResource(this);
    return mySession;
  }

}
