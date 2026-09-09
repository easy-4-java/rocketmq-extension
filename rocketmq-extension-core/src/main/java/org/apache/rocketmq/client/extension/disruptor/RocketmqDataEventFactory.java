package org.apache.rocketmq.client.extension.disruptor;

import com.lmax.disruptor.EventFactory;
import org.apache.rocketmq.client.extension.event.RocketmqDisruptorEvent;

/**
 * Disruptor {@link EventFactory} that creates {@link RocketmqDisruptorEvent} instances.
 *
 * <p>Used to pre-populate the Disruptor ring buffer with event objects.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see com.lmax.disruptor.EventFactory
 * @see RocketmqDisruptorEvent
 */
public class RocketmqDataEventFactory implements EventFactory<RocketmqDisruptorEvent> {

	@Override
	public RocketmqDisruptorEvent newInstance() {
		return new RocketmqDisruptorEvent(this);
	}
	
}
