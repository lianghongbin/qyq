package com.jingoal.qyq.common.queue.kafka;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer extends Thread {
	public static void main(String args[]) {
		Consumer consumer = new Consumer();
		consumer.init();
		consumer.start();
		synchronized (consumer) {
			try {
				consumer.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static Logger LOG = LoggerFactory.getLogger(Consumer.class);
	private ConsumerConnector consumer;
	private Map<String, Integer> topicCountMap = new HashMap<String, Integer>();

	@Override
	public void run() {
		String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		Thread.currentThread().setName(jvmName + "-" + "Consumer-");

		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);

		LOG.info(Thread.currentThread().getName() + " consumerMap size: " + consumerMap.size());

		for (String topic : consumerMap.keySet()) {
			List<KafkaStream<byte[], byte[]>> streamList = consumerMap.get(topic);

			LOG.info(Thread.currentThread().getName() + " KafkaStream size: " + streamList.size() + ", topic: " + topic);
			int i = 0;
			for (final KafkaStream<byte[], byte[]> stream : streamList) {
				ConsumerTask ct = new ConsumerTask(topic, stream, Thread.currentThread().getName(), i);
				ct.start();
				i++;
			}
		}
	}

	public void init() {
		Properties props = new Properties();
		try {
			props.load(this.getClass().getClassLoader().getResourceAsStream("consumer.properties"));
		} catch (IOException e) {
			LOG.error("", e);
			System.exit(-1);
		}
		this.topicCountMap.put("liangtest", 8);
		this.consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
	}

	public class ConsumerTask extends Thread {
		String topic;
		KafkaStream<byte[], byte[]> stream;
		String consumerName;
		int i;

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
			while (true) {
				try {
					if (it.hasNext()) {
						MessageAndMetadata<byte[], byte[]> messageAndMeatadata = it.next();
						long offset = messageAndMeatadata.offset();
						byte[] bs = messageAndMeatadata.message();
						try {
							LOG.info(
									Thread.currentThread().getName() + "-offset:" + offset + ", msg: " + new String(bs, "utf-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				} catch (ConsumerTimeoutException e) {
					LOG.error("consumer.timeout", e);
				}
			}
		}
	}

}
