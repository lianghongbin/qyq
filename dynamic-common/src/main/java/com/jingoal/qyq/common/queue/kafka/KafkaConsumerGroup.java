package com.jingoal.qyq.common.queue.kafka;

/**
 * Created by lianghb on 16/3/22.
 * Description:
 */

import com.jingoal.qyq.common.queue.MessageListener;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaConsumerGroup {
    private final ConsumerConnector consumer;
    private final String topic;
    private ExecutorService executor;
    private List<MessageListener<KafkaMessage<String>>> listeners;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerGroup.class);

    public KafkaConsumerGroup(String topic, ConsumerConfig consumerConfig) {
        logger.info("开始初始化 KafkaConsumerGroup ......");
        this.consumer = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);
        this.topic = topic;
        logger.info("KafkaConsumerGroup 初始化完成!");
    }

    public void setListeners(List<MessageListener<KafkaMessage<String>>> listeners) {
        this.listeners = listeners;
    }

    public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor == null) {
            return;
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                logger.info("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            logger.warn("Interrupted during shutdown, exiting uncleanly");
        }
    }

    public void start() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, listeners.size());
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
        executor = Executors.newFixedThreadPool(listeners.size());

        logger.info(Thread.currentThread().getName() + " consumerMap size: " + consumerMap.size());

        for (String topic : consumerMap.keySet()) {
            List<KafkaStream<byte[], byte[]>> streamList = consumerMap.get(topic);

            logger.info(Thread.currentThread().getName() + " KafkaStream size: " + streamList.size() + ", topic: " + topic);
            int i = 0;
            for (final KafkaStream<byte[], byte[]> stream : streamList) {
                ConsumerTask ct = new ConsumerTask(topic, stream, Thread.currentThread().getName(), i);
                ct.start();
                i++;
            }
        }
    }

    public class ConsumerTask extends Thread {
        private String topic;
        private KafkaStream<byte[], byte[]> stream;
        private String consumerName;
        private int i;
        private volatile boolean isRunning = true;

        public ConsumerTask(String topic, KafkaStream<byte[], byte[]> stream, String consumerName, int i) {
            this.topic = topic;
            this.stream = stream;
            this.consumerName = consumerName;
            this.i = i;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(consumerName + "-ConsumerTask-" + i);
            ConsumerIterator<byte[], byte[]> it = stream.iterator();

            while (isRunning) {
                try {
                    if (it.hasNext()) {
                        MessageAndMetadata<byte[], byte[]> messageAndMeatadata = it.next();
                        long offset = messageAndMeatadata.offset();
                        byte[] bs = messageAndMeatadata.message();
                        try {
                            MessageListener<KafkaMessage<String>> listener = listeners.get(i);
                            KafkaMessage<String> message = new KafkaMessage<String>(new String(bs)) {
                                @Override
                                public String key() {
                                    return topic;
                                }
                            };

                            listener.onMessage(message);
                            logger.info(
                                    Thread.currentThread().getName() + "-offset:" + offset + ", msg: " + new String(bs, "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (ConsumerTimeoutException e) {
                    logger.error("consumer.timeout", e);
                }
            }
        }

        public void shutdown() {
            this.isRunning = false;
        }
    }
}