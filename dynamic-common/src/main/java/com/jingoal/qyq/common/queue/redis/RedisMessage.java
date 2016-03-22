package com.jingoal.qyq.common.queue.redis;

import com.jingoal.qyq.common.Cacheable;
import com.jingoal.qyq.common.Message;

import java.io.Serializable;
import java.rmi.MarshalException;

/**
 * Created by lianghb on 16/3/18.
 * Description:
 */
public abstract class RedisMessage<V extends Serializable> extends Message<V> implements Cacheable {

    public RedisMessage(V v) {
        super(v);
    }
}
