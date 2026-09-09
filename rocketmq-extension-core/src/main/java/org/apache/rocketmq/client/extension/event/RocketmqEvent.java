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

import org.apache.rocketmq.common.message.MessageExt;

/**
 * Core contract for a RocketMQ message envelope consumed by the extension.
 *
 * <p>This interface lives in the {@code core} module and has no dependency on
 * Spring. It is the single data carrier exchanged between the chain of
 * {@link org.apache.rocketmq.client.extension.event.handler.EventHandler}s and
 * any downstream publishing mechanism (Spring's
 * {@code org.springframework.context.ApplicationEventPublisher}, a Disruptor
 * ring buffer, or direct invocation).</p>
 *
 * <p>Implementations:
 * <ul>
 *   <li>{@link DefaultRocketmqEvent} — plain POJO, the default in the core module.</li>
 *   <li>{@code org.apache.rocketmq.client.extension.spring.event.SpringRocketmqEvent} —
 *       Spring adapter in the spring module that wraps a {@code RocketmqEvent}
 *       and extends {@code org.springframework.context.ApplicationEvent} so it can be
 *       passed to {@code ApplicationEventPublisher#publishEvent}.</li>
 * </ul>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.rocketmq.common.message.MessageExt
 */
public interface RocketmqEvent {

	/**
	 * The underlying RocketMQ message envelope.
	 */
	MessageExt getMessageExt();

	void setMessageExt(MessageExt messageExt);

	String getTopic();

	void setTopic(String topic);

	String getTag();

	void setTag(String tag);

	byte[] getBody();

	void setBody(byte[] body);

	/**
	 * Body decoded as UTF-8, or {@code null} if decoding fails.
	 */
	String getMsgBody();

	/**
	 * Body decoded with the supplied charset name, or {@code null} if decoding fails.
	 */
	String getMsgBody(String code);

	/**
	 * Path expression of the form {@code /Topic/Tags/Keys} used by the
	 * responsibility-chain router.
	 */
	String getRouteExpression();

	void setRouteExpression(String routeExpression);
}
