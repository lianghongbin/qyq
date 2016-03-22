package com.jingoal.qyq.common.queue.redis;

import com.jingoal.qyq.common.Keyable;
import com.jingoal.qyq.common.Message;
import com.jingoal.qyq.common.queue.MessageListener;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/18.
 * Description:
 */
public interface RedisMessageListener<T extends Serializable> extends Keyable, MessageListener<Message<T>> {
}
