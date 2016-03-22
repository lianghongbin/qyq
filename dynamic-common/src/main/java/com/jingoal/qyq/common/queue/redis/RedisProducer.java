package com.jingoal.qyq.common.queue.redis;

import com.jingoal.qyq.common.queue.Producer;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/21.
 * Description:
 */
public interface RedisProducer<V extends Serializable> extends Producer<RedisMessage<V>> {
}
