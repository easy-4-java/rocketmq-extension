package org.apache.rocketmq.client.extension.event.handler;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;

/**
 * Contract for associating a path pattern with an {@link EventHandler}.
 *
 * @param <T> the concrete event type
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see EventHandler
 */
public interface PathProcessor<T extends RocketmqEvent> {
	
	EventHandler<T> processPath(String path);

}
