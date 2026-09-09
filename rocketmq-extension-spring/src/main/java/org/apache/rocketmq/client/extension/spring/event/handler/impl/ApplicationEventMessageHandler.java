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
 * {@link ApplicationEventPublisher}.
 *
 * <p>The published event is a {@link SpringRocketmqEvent}, which is a Spring
 * {@code ApplicationEvent} adapter that wraps a core {@link RocketmqEvent}
 * (a {@link DefaultRocketmqEvent} POJO). Listeners in Spring code can either
 * receive the wrapper or unwrap it via {@link SpringRocketmqEvent#getDelegate()}.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see MessageHandler
 * @see RocketmqEvent
 * @see DefaultRocketmqEvent
 * @see SpringRocketmqEvent
 */
public class ApplicationEventMessageHandler implements MessageHandler, ApplicationEventPublisherAware {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationEventMessageHandler.class);
	private ApplicationEventPublisher eventPublisher;

	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			// 发布消息到达的事件，以便分发到每个tag的监听方法
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
