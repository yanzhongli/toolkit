package com.common.toolkit.mq.springtransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模拟一个session类
 */
public class MqSession<T extends MqClient> {

  private static Logger logger = LoggerFactory.getLogger(MqSession.class);

  private Long sessionId;

  T client;

  public MqSession(T client) {
    this.client = client;
  }

  public void send(Message msg) throws Exception {
    client.send(msg);
  }


  public void beginTransaction() {
    logger.debug("sessionId:" + sessionId + ":beginTransaction");
  }

  public void commit() {
    client.commit();
  }

  public void rollback() {
    client.rollback();
  }

  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  @Override
  public String toString() {
    return "MqSession [sessionId=" + sessionId + "]";
  }


}  