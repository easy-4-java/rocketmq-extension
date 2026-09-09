/*
 * Copyright (c) 2018 (https://github.com/loong10k).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.rocketmq.client.extension.event;

import java.io.UnsupportedEncodingException;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * Default POJO implementation of {@link RocketmqEvent}.
 *
 * <p>Carries the raw RocketMQ {@link MessageExt} plus extracted {@code topic},
 * {@code tag}, {@code body}, and a route expression of the form
 * {@code /Topic/Tags/Keys}. Has no dependency on Spring and is safe to use in
 * any Java runtime.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 * @see RocketmqEvent
 */
public class DefaultRocketmqEvent implements RocketmqEvent {

	private MessageExt messageExt;
	private String topic;
	private String tag;
	private byte[] body;
	private String routeExpression;

	public DefaultRocketmqEvent() {
	}

	public DefaultRocketmqEvent(MessageExt msgExt) {
		this.topic = msgExt.getTopic();
		this.tag = msgExt.getTags();
		this.body = msgExt.getBody();
		this.messageExt = msgExt;
		this.routeExpression = this.buildRouteExpression(msgExt);
	}

	private String buildRouteExpression(MessageExt msgExt) {
		return new StringBuilder("/").append(msgExt.getTopic()).append("/").append(msgExt.getTags()).append("/")
				.append(msgExt.getKeys()).toString();
	}

	@Override
	public String getMsgBody() {
		return getMsgBody("UTF-8");
	}

	@Override
	public String getMsgBody(String code) {
		if (this.body == null) {
			return null;
		}
		try {
			return new String(this.body, code);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public MessageExt getMessageExt() {
		return messageExt;
	}

	@Override
	public void setMessageExt(MessageExt messageExt) {
		this.messageExt = messageExt;
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public byte[] getBody() {
		return body;
	}

	@Override
	public void setBody(byte[] body) {
		this.body = body;
	}

	@Override
	public String getRouteExpression() {
		return routeExpression;
	}

	@Override
	public void setRouteExpression(String routeExpression) {
		this.routeExpression = routeExpression;
	}
}
