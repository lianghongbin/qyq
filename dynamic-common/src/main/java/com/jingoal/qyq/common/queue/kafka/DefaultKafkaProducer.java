package com.jingoal.qyq.common.queue.kafka;

import kafka.producer.ProducerConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by lianghb on 16/3/21.
 * Description:
 */
public class DefaultKafkaProducer<V extends Serializable> implements KafkaProducer<V> {

    private ProducerConfig config;
    private org.apache.kafka.clients.producer.Producer<String, V> producer;
    private static final Logger logger = LoggerFactory.getLogger(DefaultKafkaProducer.class);

    public DefaultKafkaProducer() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("enter hook");
                close();
            }
        });
    }

    public void setProducer(org.apache.kafka.clients.producer.Producer<String, V> producer) {
        this.producer = producer;
    }

    public void close() {
        logger.debug("begin close producer.");
        try {
            producer.close();
        } catch (Exception e) {
            logger.error("", e);
        }

        logger.debug("end close producer.");
    }

    public void send(String partitionKey, KafkaMessage<V> message) {
        final ProducerRecord<String, V> producerRecord = new ProducerRecord<String, V>(message.key(), partitionKey , message.getPayload());
        producer.send(producerRecord);
    }

    public void send(KafkaMessage<V> message) {
        final ProducerRecord<String, V> record = new ProducerRecord<String, V>(message.key(), message.getPayload());
        producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    logger.error("message send error:{}!", exception);
                }
            }
        });
    }
}
