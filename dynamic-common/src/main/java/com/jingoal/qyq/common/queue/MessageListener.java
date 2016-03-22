package com.jingoal.qyq.common.queue;

import com.jingoal.qyq.common.Message;
import com.jingoal.qyq.common.exception.MessageException;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/16.
 * Description: 通用的消息监听接口
 */
public interface MessageListener<T extends Serializable> {

    void onMessage(T message);
}
