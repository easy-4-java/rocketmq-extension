package org.apache.rocketmq.client.extension.event.handler;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;

/**
 * Abstract handler that implements {@link Nameable} by storing a handler name.
 *
 * @param <T> the concrete event type
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see EventHandler
 * @see Nameable
 */
public abstract class AbstractNameableMessageHandler<T extends RocketmqEvent> implements EventHandler<T>, Nameable {

	/**
	 * 过滤器名称
	 */
	protected String name;

	protected String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
