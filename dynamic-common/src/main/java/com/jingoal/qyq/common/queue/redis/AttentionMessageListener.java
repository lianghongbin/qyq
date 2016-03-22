package com.jingoal.qyq.common.queue.redis;

import com.jingoal.qyq.common.Message;

/**
 * Created by lianghb on 16/3/18.
 * Description:
 */
public class AttentionMessageListener implements RedisMessageListener<String> {

    public String key() {
        return AttentionMessage.REDIS_ATTENTION_QUEUE_DESTINATION;
    }

    public void onMessage(Message<String> message) {
        //处理关注关系
    }
}
