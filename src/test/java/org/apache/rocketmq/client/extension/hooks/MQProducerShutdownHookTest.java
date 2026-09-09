package org.apache.rocketmq.client.extension.hooks;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

/**
 * Tests for {@link MQProducerShutdownHook}.
 */
public class MQProducerShutdownHookTest {

    @Test
    public void shouldExtendThread() {
        MQProducerShutdownHook hook = new MQProducerShutdownHook(new StubProducer());
        assertTrue(hook instanceof Thread);
    }

    @Test
    public void shouldCallShutdownOnRun() {
        StubProducer producer = new StubProducer();
        MQProducerShutdownHook hook = new MQProducerShutdownHook(producer);
        hook.run();
        assertTrue(producer.shutdownCalled.get());
    }

    private static class StubProducer implements MQProducer {
        final AtomicBoolean shutdownCalled = new AtomicBoolean(false);

        @Override public void start() {}
        @Override public void shutdown() { shutdownCalled.set(true); }
        @Override public List<MessageQueue> fetchPublishMessageQueues(String t) { return null; }
        @Override public SendResult send(Message m) { return null; }
        @Override public SendResult send(Message m, long t) { return null; }
        @Override public void send(Message m, SendCallback cb) {}
        @Override public void send(Message m, SendCallback cb, long t) {}
        @Override public void sendOneway(Message m) {}
        @Override public SendResult send(Message m, MessageQueue mq) { return null; }
        @Override public SendResult send(Message m, MessageQueue mq, long t) { return null; }
        @Override public void send(Message m, MessageQueue mq, SendCallback cb) {}
        @Override public void send(Message m, MessageQueue mq, SendCallback cb, long t) {}
        @Override public void sendOneway(Message m, MessageQueue mq) {}
        @Override public SendResult send(Message m, MessageQueueSelector s, Object a) { return null; }
        @Override public SendResult send(Message m, MessageQueueSelector s, Object a, long t) { return null; }
        @Override public void send(Message m, MessageQueueSelector s, Object a, SendCallback cb) {}
        @Override public void send(Message m, MessageQueueSelector s, Object a, SendCallback cb, long t) {}
        @Override public void sendOneway(Message m, MessageQueueSelector s, Object a) {}
        @Override public TransactionSendResult sendMessageInTransaction(Message m, LocalTransactionExecuter e, Object a) { return null; }
        @Override public TransactionSendResult sendMessageInTransaction(Message m, Object a) { return null; }
        @Override public SendResult send(Collection<Message> msgs) { return null; }
        @Override public SendResult send(Collection<Message> msgs, long t) { return null; }
        @Override public SendResult send(Collection<Message> msgs, MessageQueue mq) { return null; }
        @Override public SendResult send(Collection<Message> msgs, MessageQueue mq, long t) { return null; }
        @Override public void createTopic(String k, String n, int q) {}
        @Override public void createTopic(String k, String n, int q, int a) {}
        @Override public long searchOffset(MessageQueue mq, long t) { return 0; }
        @Override public long maxOffset(MessageQueue mq) { return 0; }
        @Override public long minOffset(MessageQueue mq) { return 0; }
        @Override public long earliestMsgStoreTime(MessageQueue mq) { return 0; }
        @Override public MessageExt viewMessage(String id) { return null; }
        @Override public QueryResult queryMessage(String t, String k, int m, long s, long e) { return null; }
        @Override public MessageExt viewMessage(String id, String topic) { return null; }
    }
}
