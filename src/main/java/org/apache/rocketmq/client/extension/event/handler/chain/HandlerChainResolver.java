package org.apache.rocketmq.client.extension.event.handler.chain;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;

/**
 * Resolves the appropriate {@link HandlerChain} for a given event.
 *
 * @param <T> the concrete event type
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see HandlerChain
 */
public interface HandlerChainResolver<T extends RocketmqEvent> {

	HandlerChain<T> getChain(T event , HandlerChain<T> originalChain);
	
}
