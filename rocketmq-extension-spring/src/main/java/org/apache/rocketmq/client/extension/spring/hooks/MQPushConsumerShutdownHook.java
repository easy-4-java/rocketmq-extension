package org.apache.rocketmq.client.extension.spring.hooks;

import org.apache.rocketmq.client.consumer.MQPushConsumer;

/**
 * JVM shutdown hook that gracefully shuts down a RocketMQ {@link MQPushConsumer}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.rocketmq.client.consumer.MQPushConsumer
 */
public class MQPushConsumerShutdownHook extends Thread{
	
	private MQPushConsumer consumer;
	
	public MQPushConsumerShutdownHook(MQPushConsumer consumer) {
		this.consumer = consumer;
	}
	
	@Override
	public void run() {
		consumer.shutdown();
	}
	
}
