package org.apache.rocketmq.client.extension.event.handler;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;

/**
 * Contract for handling a {@link RocketmqEvent} within a handler chain.
 *
 * @param <T> the concrete event type
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see HandlerChain
 */
public interface EventHandler<T extends RocketmqEvent> {

	public void doHandler(T event, HandlerChain<T> handlerChain) throws Exception;
	
}
