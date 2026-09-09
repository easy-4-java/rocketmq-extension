package org.apache.rocketmq.client.extension.event.handler.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.AbstractRouteableMessageHandler;
import org.apache.rocketmq.client.extension.event.handler.MessageHandler;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChainResolver;
import org.apache.rocketmq.client.extension.event.handler.chain.ProxiedHandlerChain;

/**
 * {@link MessageHandler} that converts a consumed message into a {@link RocketmqEvent}
 * and processes it through a resolved {@link HandlerChain}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see AbstractRouteableMessageHandler
 * @see MessageHandler
 * @see HandlerChainResolver
 */
public class RocketmqEventMessageHandler extends AbstractRouteableMessageHandler<RocketmqEvent> implements MessageHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RocketmqEventMessageHandler.class);
	
	public RocketmqEventMessageHandler(HandlerChainResolver<RocketmqEvent> filterChainResolver) {
		super(filterChainResolver);
	}
	
	@Override
	public boolean handleMessage(MessageExt msgExt, ConsumeConcurrentlyContext context) throws Exception {
		try {
			//构造原始链对象
			HandlerChain<RocketmqEvent>	originalChain = new ProxiedHandlerChain();
			//执行事件处理链
			this.doHandler(new RocketmqEvent(msgExt), originalChain);
			return true;
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return false;
		}
	}
	
}