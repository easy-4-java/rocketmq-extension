package org.apache.rocketmq.client.extension.disruptor;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;

import com.lmax.disruptor.EventTranslatorOneArg;
import org.apache.rocketmq.client.extension.event.RocketmqDisruptorEvent;

/**
 * Disruptor {@link EventTranslatorOneArg} that populates a {@link RocketmqDisruptorEvent}
 * from a consumed {@link MessageExt}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see com.lmax.disruptor.EventTranslatorOneArg
 * @see RocketmqDisruptorEvent
 */
public class RocketmqDataEventTranslator implements EventTranslatorOneArg<RocketmqDisruptorEvent, MessageExt> {
	
	private ConsumeConcurrentlyContext context;

	/**
	 * Creates a translator with the given consume context.
	 *
	 * @param context the {@link ConsumeConcurrentlyContext} associated with the consumption
	 * @throws Exception if initialization fails
	 */
	public RocketmqDataEventTranslator(ConsumeConcurrentlyContext context) throws Exception {
		this.context = context;
	}
	
	/**
	 * Translates a {@link MessageExt} into a {@link RocketmqDisruptorEvent} by copying
	 * the topic, tag, and body fields.
	 *
	 * @param event    the target Disruptor event to populate
	 * @param sequence the ring buffer sequence number
	 * @param msgExt   the source message consumed from RocketMQ
	 */
	@Override
	public void translateTo(RocketmqDisruptorEvent event, long sequence, MessageExt msgExt) {
		
		event.setMessageExt(msgExt);
		event.setTopic(msgExt.getTopic());
		event.setTag(msgExt.getTags());
		event.setBody(msgExt.getBody());
		
	}

	public ConsumeConcurrentlyContext getContext() {
		return context;
	}

	public void setContext(ConsumeConcurrentlyContext context) {
		this.context = context;
	}
	
}