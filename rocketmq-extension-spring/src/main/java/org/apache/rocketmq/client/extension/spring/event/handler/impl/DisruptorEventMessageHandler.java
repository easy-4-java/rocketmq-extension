package org.apache.rocketmq.client.extension.spring.event.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.lmax.disruptor.dsl.Disruptor;
import org.apache.rocketmq.client.extension.disruptor.RocketmqDataEventTranslator;
import org.apache.rocketmq.client.extension.event.RocketmqDisruptorEvent;
import org.apache.rocketmq.client.extension.event.handler.MessageHandler;

/**
 * {@link MessageHandler} that publishes consumed messages into a LMAX Disruptor
 * ring buffer for high-throughput asynchronous processing.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see MessageHandler
 * @see com.lmax.disruptor.dsl.Disruptor
 * @see RocketmqDataEventTranslator
 */
public class DisruptorEventMessageHandler implements MessageHandler, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(DisruptorEventMessageHandler.class);
	
	private Disruptor<RocketmqDisruptorEvent> disruptor;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			// 生产消息
			getDisruptor().publishEvent(new RocketmqDataEventTranslator(context), msgExt);
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}
	}

	public Disruptor<RocketmqDisruptorEvent> getDisruptor() {
		return disruptor;
	}

	public void setDisruptor(Disruptor<RocketmqDisruptorEvent> disruptor) {
		this.disruptor = disruptor;
	}

}