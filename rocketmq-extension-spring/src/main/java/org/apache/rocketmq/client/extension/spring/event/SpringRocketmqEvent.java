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
package org.apache.rocketmq.client.extension.spring.event;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationEvent;

/**
 * Spring adapter that exposes a {@link RocketmqEvent} as a Spring
 * {@link ApplicationEvent} so it can be passed to
 * {@code ApplicationEventPublisher#publishEvent(Object)}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 * @see RocketmqEvent
 */
public class SpringRocketmqEvent extends ApplicationEvent implements RocketmqEvent {

	private static final long serialVersionUID = 1L;

	private final RocketmqEvent delegate;

	public SpringRocketmqEvent(RocketmqEvent delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	public RocketmqEvent getDelegate() {
		return delegate;
	}

	@Override
	public MessageExt getMessageExt() {
		return delegate.getMessageExt();
	}

	@Override
	public void setMessageExt(MessageExt messageExt) {
		delegate.setMessageExt(messageExt);
	}

	@Override
	public String getTopic() {
		return delegate.getTopic();
	}

	@Override
	public void setTopic(String topic) {
		delegate.setTopic(topic);
	}

	@Override
	public String getTag() {
		return delegate.getTag();
	}

	@Override
	public void setTag(String tag) {
		delegate.setTag(tag);
	}

	@Override
	public byte[] getBody() {
		return delegate.getBody();
	}

	@Override
	public void setBody(byte[] body) {
		delegate.setBody(body);
	}

	@Override
	public String getMsgBody() {
		return delegate.getMsgBody();
	}

	@Override
	public String getMsgBody(String code) {
		return delegate.getMsgBody(code);
	}

	@Override
	public String getRouteExpression() {
		return delegate.getRouteExpression();
	}

	@Override
	public void setRouteExpression(String routeExpression) {
		delegate.setRouteExpression(routeExpression);
	}
}
