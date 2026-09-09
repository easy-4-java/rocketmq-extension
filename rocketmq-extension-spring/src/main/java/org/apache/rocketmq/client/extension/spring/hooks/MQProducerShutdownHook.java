package org.apache.rocketmq.client.extension.spring.hooks;

import org.apache.rocketmq.client.producer.MQProducer;

/**
 * JVM shutdown hook that gracefully shuts down a RocketMQ {@link MQProducer}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.rocketmq.client.producer.MQProducer
 */
public class MQProducerShutdownHook extends Thread{
	
	private MQProducer producer;
	
	public MQProducerShutdownHook(MQProducer producer) {
		this.producer = producer;
	}
	
	@Override
	public void run() {
		producer.shutdown();
	}
	
}
