package com.jingoal.qyq.common.queue.kafka;

import com.jingoal.qyq.common.exception.MessageException;
import com.jingoal.qyq.common.queue.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lianghb on 16/3/23.
 * Description:
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {

        logger.info("初始化消息发送环境......");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-kafka.xml");
        DefaultKafkaProducer<String> producer = context.getBean("kafkaProducer", DefaultKafkaProducer.class);

        logger.info("初始化消息发送环境完成!");

        for (int i = 0; i < 10; i++) {
            AttentionMessage attentionMessage = new AttentionMessage("lhb-test-" + i);

            producer.send(attentionMessage);
            logger.info("消息发送成功: {}", attentionMessage.getPayload());
        }

        producer.close();
    }
}
