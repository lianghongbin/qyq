package com.jingoal.qyq.common.queue.kafka;

import com.jingoal.qyq.common.Keyable;
import com.jingoal.qyq.common.Message;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/22.
 * Description:
 */
public abstract class KafkaMessage<V extends Serializable> extends Message<V> implements Keyable {

    public KafkaMessage(V v) {
        super(v);
    }
}
