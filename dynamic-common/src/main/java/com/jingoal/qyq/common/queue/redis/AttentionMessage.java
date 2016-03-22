package com.jingoal.qyq.common.queue.redis;

/**
 * Created by lianghb on 16/3/18.
 * Description: 关注关系的Redis消息定义
 */
public class AttentionMessage extends RedisMessage<String> {

    public static final String REDIS_ATTENTION_QUEUE_DESTINATION = "k-attention";

    /**
     * 消息内容为关注/取消关注的关系
     * @param attention A+B:A关注了B, A-B:A取消关注了B
     */
    public AttentionMessage(String attention) {
        super(attention);   //A+B  或  A-B
    }

    /**
     * 消息缓存或持久化的时长,默认一月
     * @return 时长为秒
     */
    public int expire() {
        return 25920000;   //一月
    }

    /**
     * 在Redis中的Key, 也是队列名称
     * @return 队列名称
     */
    public String key() {
        return REDIS_ATTENTION_QUEUE_DESTINATION;
    }
}
