package com.jingoal.qyq.common.queue.kafka;

/**
 * Created by lianghb on 16/3/22.
 * Description:
 */

import com.jingoal.qyq.common.queue.MessageListener;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private int index = 0;
    private List<MessageListener<KafkaMessage<String>>> listeners;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerGroup.class);

    public KafkaConsumerGroup(String topic, ConsumerConfig consumerConfig) {
        this.consumer = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);
        this.topic = topic;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
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
        index = 0;
        // now create an object to consume the messages
        for (final KafkaStream<byte[], byte[]> stream : streams) {
            executor.submit(new Runnable() {
                public void run() {
                    ConsumerIterator<byte[], byte[]> it = stream.iterator();
                    MessageListener<KafkaMessage<String>> listener = listeners.get(index++);
                    while (it.hasNext()) {
                        KafkaMessage<String> message = new KafkaMessage<String>(new String(it.next().message())) {
                            public String key() {
                                return topic;
                            }
                        } ;
                        listener.onMessage(message);
                    }
                }
            });
        }
    }
}