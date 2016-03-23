package com.jingoal.qyq.common.queue.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lianghb on 16/3/22.
 * Description:
 */
public class Bootstrap {

    private KafkaConsumerGroup consumerGroup;
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public void start() {
        logger.info("初始化消费者环境......");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-kafka.xml");
        consumerGroup = context.getBean("consumerGroup", KafkaConsumerGroup.class);

        logger.info("初始化消费者环境完成!");

        logger.info("消费者开始监听队列......");
        consumerGroup.start();
    }

    public void stop() {
        consumerGroup.shutdown();
    }

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.start();
    }
}
