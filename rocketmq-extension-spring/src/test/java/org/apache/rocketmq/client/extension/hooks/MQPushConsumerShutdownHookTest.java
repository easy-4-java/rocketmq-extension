package org.apache.rocketmq.client.extension.spring.hooks;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

/**
 * Tests for {@link MQPushConsumerShutdownHook}.
 */
public class MQPushConsumerShutdownHookTest {

    /**
     * No-op base covering every abstract method of
     * {@code MQPushConsumer extends MQConsumer extends MQAdmin} as of RocketMQ 5.5.1.
     */
    private abstract static class StubConsumerBase implements MQPushConsumer {

        @Override public void start() throws MQClientException {}

        @Override public void shutdown() {}

        @Override public void registerMessageListener(MessageListener listener) {}

        @Override public void registerMessageListener(MessageListenerConcurrently listener) {}

        @Override public void registerMessageListener(MessageListenerOrderly listener) {}

        @Override public void subscribe(String topic, String subExpression) throws MQClientException {}

        @Override public void subscribe(String topic, String fullClassName, String jsonClassContent) throws MQClientException {}

        @Override public void subscribe(String topic, MessageSelector selector) throws MQClientException {}

        @Override public void unsubscribe(String topic) {}

        @Override public void updateCorePoolSize(int corePoolSize) {}

        @Override public void suspend() {}

        @Override public void resume() {}

        // ---- MQConsumer methods ----

        @Override public void sendMessageBack(MessageExt msg, int delayLevel) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {}

        @Override public void sendMessageBack(MessageExt msg, int delayLevel, String brokerName) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {}

        @Override public Set<MessageQueue> fetchSubscribeMessageQueues(String topic) throws MQClientException {
            return Collections.emptySet();
        }

        // ---- MQAdmin methods ----

        @Override public void createTopic(String key, String newTopic, int queueNum, Map<String, String> attributes) throws MQClientException {}

        @Override public void createTopic(String key, String newTopic, int queueNum, int sysFlag, Map<String, String> attributes) throws MQClientException {}

        @Override public long searchOffset(MessageQueue mq, long timeout) throws MQClientException {
            return 0;
        }

        @Override public long maxOffset(MessageQueue mq) throws MQClientException {
            return 0;
        }

        @Override public long minOffset(MessageQueue mq) throws MQClientException {
            return 0;
        }

        @Override public long earliestMsgStoreTime(MessageQueue mq) throws MQClientException {
            return 0;
        }

        @Override public QueryResult queryMessage(String topic, String key, int maxNum, long begin, long end) throws MQClientException, InterruptedException {
            return null;
        }

        @Override public MessageExt viewMessage(String topic, String msgId) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
            return null;
        }
    }

    /** Records shutdown invocation. */
    private static class StubConsumer extends StubConsumerBase {

        final AtomicBoolean shutdownCalled = new AtomicBoolean(false);

        @Override public void shutdown() {
            shutdownCalled.set(true);
        }
    }

    @Test
    public void shouldExtendThread() {
        StubConsumer consumer = new StubConsumer();
        MQPushConsumerShutdownHook hook = new MQPushConsumerShutdownHook(consumer);
        assertTrue(hook instanceof Thread);
    }

    @Test
    public void shouldCallShutdownOnRun() {
        StubConsumer consumer = new StubConsumer();
        MQPushConsumerShutdownHook hook = new MQPushConsumerShutdownHook(consumer);
        hook.run();
        assertTrue(consumer.shutdownCalled.get());
    }
}
