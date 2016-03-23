package com.jingoal.qyq.common.queue.kafka;

import com.jingoal.qyq.common.queue.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/22.
 * Description: 通用的消息监听器
 */
public class GeneralMessageListener<K extends KafkaMessage<String>> implements MessageListener<K> {

    private static final Logger logger = LoggerFactory.getLogger(GeneralMessageListener.class);

    public void onMessage(K message) {
        logger.info("收到一个消息:{}", message.getPayload());
        if (!message.key().equalsIgnoreCase("my-topic")) {  //如果不是my-topic这个主题队列,不做处理
            return;
        }

        //处理关注的队列
        logger.info("处理 my-topic 队列");
    }
}
