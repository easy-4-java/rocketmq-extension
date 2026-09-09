package org.apache.rocketmq.client.extension.event.handler;

import java.util.List;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;


/**
 * A named, ordered list of {@link EventHandler}s that can produce a proxied {@link HandlerChain}.
 *
 * @param <T> the concrete event type
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see EventHandler
 * @see HandlerChain
 */
public interface NamedHandlerList<T extends RocketmqEvent> extends List<EventHandler<T>> {
	 
	/**
     * Returns the configuration-unique name assigned to this {@code Handler} list.
     */
    String getName();

    /**
     * Returns a new {@code HandlerChain<T>} instance that will first execute this list's {@code Handler}s (in list order)
     * and end with the execution of the given {@code handlerChain} instance.
     */
    HandlerChain<T> proxy(HandlerChain<T> handlerChain);
    
}
