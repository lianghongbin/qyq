package com.jingoal.qyq.common.queue.kafka;

import com.jingoal.qyq.common.Keyable;
import com.jingoal.qyq.common.queue.MessageListener;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/22.
 * Description: Kafka的消息监听接口
 */
public interface KafkaMessageListener<T extends KafkaMessage> extends MessageListener<T> {

}
