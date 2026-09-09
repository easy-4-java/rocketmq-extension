package org.apache.rocketmq.client.extension.event;

import java.io.UnsupportedEncodingException;

import org.apache.rocketmq.common.message.MessageExt;

import com.lmax.disruptor.event.DisruptorEvent;

import org.apache.rocketmq.client.extension.util.StringUtils;

/**
 * A Disruptor-based event that wraps a consumed RocketMQ {@link MessageExt}.
 *
 * <p>Carries the raw message along with topic, tag, and body fields for fast
 * downstream processing. Builds a route expression from the message metadata
 * if none is explicitly set.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see com.lmax.disruptor.event.DisruptorEvent
 * @see org.apache.rocketmq.common.message.MessageExt
 */
@SuppressWarnings("serial")
public class RocketmqDisruptorEvent extends DisruptorEvent {

	private MessageExt messageExt;
	private String topic;
	private String tag;
	private byte[] body;

	public RocketmqDisruptorEvent(Object source) {
		super(source);
	}
	
	public String getRouteExpression() {
		if (this.routeExpression != null) {
			return this.routeExpression;
		}
		if (messageExt != null) {
			return this.buildRouteExpression(messageExt);
		}
		return null;
	}

	private String routeExpression;

	public void setRouteExpression(String routeExpression) {
		this.routeExpression = routeExpression;
	}
	
	private String buildRouteExpression(MessageExt msgExt) {
		return new StringBuilder("/").append(msgExt.getTopic()).append("/").append(msgExt.getTags()).append("/")
				.append(msgExt.getKeys()).toString();
	}

	public String getMsgBody() {
		try {
			return new String(this.body, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public String getMsgBody(String code) {
		try {
			return new String(this.body, code);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public MessageExt getMessageExt() {
		return messageExt;
	}

	public void setMessageExt(MessageExt messageExt) {
		this.messageExt = messageExt;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}
	
}
