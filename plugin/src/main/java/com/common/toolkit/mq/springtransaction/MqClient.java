package com.common.toolkit.mq.springtransaction;

/**
 * mq 客户端，需要mq支持事务消息
 */
public interface MqClient {

  void send(Message message);

  void commit();

  void rollback();
}
