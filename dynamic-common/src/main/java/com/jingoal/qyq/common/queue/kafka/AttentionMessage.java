package com.jingoal.qyq.common.queue.kafka;

/**
 * Created by lianghb on 16/3/22.
 * Description:
 */
public class AttentionMessage extends KafkaMessage<String> {

    public static final String KAFKA_ATTENTION_QUEUE_DESTINATION = "q-attention";

    public AttentionMessage(String message) {
        super(message);
    }

    /**
     * 生成缓存Key
     *
     * @return string
     */
    public String key() {
        return this.KAFKA_ATTENTION_QUEUE_DESTINATION;
    }
}
