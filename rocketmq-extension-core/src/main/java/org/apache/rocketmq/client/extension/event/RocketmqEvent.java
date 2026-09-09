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
 * Has no dependency on Spring. Lives in the {@code core} module.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public interface RocketmqEvent {

	MessageExt getMessageExt();

	void setMessageExt(MessageExt messageExt);

	String getTopic();

	void setTopic(String topic);

	String getTag();

	void setTag(String tag);

	byte[] getBody();

	void setBody(byte[] body);

	String getMsgBody();

	String getMsgBody(String code);

	String getRouteExpression();

	void setRouteExpression(String routeExpression);
}
