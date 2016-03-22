package com.jingoal.qyq.common.queue.kafka;

import com.jingoal.qyq.common.queue.Producer;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/22.
 * Description:
 */
public interface KafkaProducer<V extends Serializable> extends Producer<KafkaMessage<V>> {

    void send(String partitionKey, KafkaMessage<V> v);
}
