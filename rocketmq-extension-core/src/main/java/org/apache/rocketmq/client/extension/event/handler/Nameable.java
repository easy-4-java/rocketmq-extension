package org.apache.rocketmq.client.extension.event.handler;

/**
 * Contract for components that can be identified by a unique name.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public interface Nameable {

	void setName(String name);
	
}
