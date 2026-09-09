package org.apache.rocketmq.client.extension.event.handler.chain;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;

/**
 * Contract for a chain of {@link org.apache.rocketmq.client.extension.event.handler.EventHandler}s that process an event sequentially.
 *
 * @param <T> the concrete event type
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.rocketmq.client.extension.event.handler.EventHandler
 */
public interface HandlerChain<T extends RocketmqEvent>{

	void doHandler(T event) throws Exception;
	
}
