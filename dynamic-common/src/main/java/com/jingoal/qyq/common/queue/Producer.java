package com.jingoal.qyq.common.queue;

import com.jingoal.qyq.common.Message;
import com.jingoal.qyq.common.exception.MessageException;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/16.
 * Description: 通用消息发送接口
 */
public interface Producer<T extends Serializable> {

    void send(T message) throws MessageException;
}
