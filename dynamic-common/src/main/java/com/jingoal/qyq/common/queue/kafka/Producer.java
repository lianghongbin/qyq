package com.jingoal.qyq.common.queue.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {
	public static Logger LOG = LoggerFactory.getLogger(Producer.class);

	private KafkaProducer<byte[], byte[]> producer = null;

	public static void main(String[] args) {
		Producer producer = new Producer();
		for (int i = 0; i < 1000; i++) {
			try {
				producer.pushMsg("liangtest", ("test string " + i).getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Producer() {
		final Properties props = new Properties();
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("producer.properties");
			props.load(is);
		} catch (IOException e) {
			LOG.error("", e);
			System.exit(-1);
		}

		producer = new KafkaProducer<byte[], byte[]>(props);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOG.info("enter hook");
				close();
			}
		});
	}

	public void pushMsg(final String topic, byte[] payload) {
		ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topic, payload);
		producer.send(record, new Callback() {
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception != null) {
					exception.printStackTrace();
				}
			}
		});
	}

	void pushMsg(final String topic, byte[] partitionKey, byte[] payload) {
		ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topic, partitionKey, payload);
		producer.send(record, new Callback() {
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception != null) {
					exception.printStackTrace();
				}
			}
		});
	}

	public void close() {
		LOG.debug("begin close producer.");
		try {
			producer.close();
		} catch (Exception e) {
			LOG.error("", e);
		}
		LOG.debug("end close producer.");
	}
}
