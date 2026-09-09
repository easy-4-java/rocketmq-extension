package org.apache.rocketmq.client.extension.spring.hooks;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

/**
 * Tests for {@link MQPushConsumerShutdownHook}.
 */
public class MQPushConsumerShutdownHookTest {

    @Test
    public void shouldExtendThread() {
        MQPushConsumerShutdownHook hook = new MQPushConsumerShutdownHook(new StubConsumer());
        assertTrue(hook instanceof Thread);
    }

    @Test
    public void shouldCallShutdownOnRun() {
        StubConsumer consumer = new StubConsumer();
        MQPushConsumerShutdownHook hook = new MQPushConsumerShutdownHook(consumer);
        hook.run();
        assertTrue(consumer.shutdownCalled.get());
    }

    private static class StubConsumer implements MQPushConsumer {
        final AtomicBoolean shutdownCalled = new AtomicBoolean(false);

        @Override public void start() {}
        @Override public void shutdown() { shutdownCalled.set(true); }
        @Override public void registerMessageListener(MessageListener l) {}
        @Override public void registerMessageListener(MessageListenerConcurrently l) {}
        @Override public void registerMessageListener(MessageListenerOrderly l) {}
        @Override public void subscribe(String t, String sub) {}
        @Override public void subscribe(String t, String sub, String exp) {}
        @Override public void subscribe(String t, MessageSelector s) {}
        @Override public void unsubscribe(String t) {}
        @Override public void updateCorePoolSize(int s) {}
        @Override public void suspend() {}
        @Override public void resume() {}
        @Override public Set<MessageQueue> fetchSubscribeMessageQueues(String t) { return null; }
        @Override public void sendMessageBack(MessageExt m, int d) {}
        @Override public void sendMessageBack(MessageExt m, int d, String brokerName) {}
        @Override public void createTopic(String k, String n, int q) {}
        @Override public void createTopic(String k, String n, int q, int a) {}
        @Override public long searchOffset(MessageQueue mq, long t) { return 0; }
        @Override public long maxOffset(MessageQueue mq) { return 0; }
        @Override public long minOffset(MessageQueue mq) { return 0; }
        @Override public long earliestMsgStoreTime(MessageQueue mq) { return 0; }
        @Override public MessageExt viewMessage(String id) { return null; }
        @Override public org.apache.rocketmq.client.QueryResult queryMessage(String t, String k, int m, long s, long e) { return null; }
        @Override public MessageExt viewMessage(String id, String topic) { return null; }
    }
}
