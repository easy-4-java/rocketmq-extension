package org.apache.rocketmq.client.extension.spring.event.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import org.apache.rocketmq.client.extension.event.DefaultRocketmqEvent;
import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.MessageHandler;
import org.apache.rocketmq.client.extension.spring.event.SpringRocketmqEvent;

/**
 * {@link MessageHandler} that converts a consumed RocketMQ message into a
 * {@link RocketmqEvent} and publishes it via Spring's
 * {@link ApplicationEventPublisher} as a {@link SpringRocketmqEvent}.
 */
public class ApplicationEventMessageHandler implements MessageHandler, ApplicationEventPublisherAware {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationEventMessageHandler.class);
	private ApplicationEventPublisher eventPublisher;

	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			getEventPublisher().publishEvent(new SpringRocketmqEvent(new DefaultRocketmqEvent(msgExt)));
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	public ApplicationEventPublisher getEventPublisher() {
		return eventPublisher;
	}

}
