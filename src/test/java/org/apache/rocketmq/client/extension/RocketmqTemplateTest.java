package org.apache.rocketmq.client.extension;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

/**
 * Tests for {@link RocketmqTemplate}.
 */
public class RocketmqTemplateTest {

    private static class StubProducer implements MQProducer {
        final AtomicBoolean sendCalled = new AtomicBoolean(false);

        @Override public void start() {}
        @Override public void shutdown() {}
        @Override public List<MessageQueue> fetchPublishMessageQueues(String t) {
            List<MessageQueue> list = new ArrayList<>();
            list.add(new MessageQueue(t, "broker-a", 0));
            return list;
        }
        @Override public SendResult send(Message m) { sendCalled.set(true); return new SendResult(); }
        @Override public SendResult send(Message m, long t) { sendCalled.set(true); return new SendResult(); }
        @Override public void send(Message m, SendCallback cb) { sendCalled.set(true); }
        @Override public void send(Message m, SendCallback cb, long t) { sendCalled.set(true); }
        @Override public void sendOneway(Message m) { sendCalled.set(true); }
        @Override public SendResult send(Message m, MessageQueue mq) { sendCalled.set(true); return new SendResult(); }
        @Override public SendResult send(Message m, MessageQueue mq, long t) { sendCalled.set(true); return new SendResult(); }
        @Override public void send(Message m, MessageQueue mq, SendCallback cb) { sendCalled.set(true); }
        @Override public void send(Message m, MessageQueue mq, SendCallback cb, long t) { sendCalled.set(true); }
        @Override public void sendOneway(Message m, MessageQueue mq) { sendCalled.set(true); }
        @Override public SendResult send(Message m, MessageQueueSelector s, Object a) { sendCalled.set(true); return new SendResult(); }
        @Override public SendResult send(Message m, MessageQueueSelector s, Object a, long t) { sendCalled.set(true); return new SendResult(); }
        @Override public void send(Message m, MessageQueueSelector s, Object a, SendCallback cb) { sendCalled.set(true); }
        @Override public void send(Message m, MessageQueueSelector s, Object a, SendCallback cb, long t) { sendCalled.set(true); }
        @Override public void sendOneway(Message m, MessageQueueSelector s, Object a) { sendCalled.set(true); }
        @Override public TransactionSendResult sendMessageInTransaction(Message m, LocalTransactionExecuter e, Object a) { sendCalled.set(true); return null; }
        @Override public TransactionSendResult sendMessageInTransaction(Message m, Object a) { sendCalled.set(true); return null; }
        @Override public SendResult send(Collection<Message> msgs) { sendCalled.set(true); return new SendResult(); }
        @Override public SendResult send(Collection<Message> msgs, long t) { sendCalled.set(true); return new SendResult(); }
        @Override public SendResult send(Collection<Message> msgs, MessageQueue mq) { sendCalled.set(true); return new SendResult(); }
        @Override public SendResult send(Collection<Message> msgs, MessageQueue mq, long t) { sendCalled.set(true); return new SendResult(); }
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

    private static class StubConsumer implements MQPushConsumer {
        final AtomicBoolean registerCalled = new AtomicBoolean(false);

        @Override public void start() {}
        @Override public void shutdown() {}
        @Override public void registerMessageListener(MessageListener l) {}
        @Override public void registerMessageListener(MessageListenerConcurrently l) { registerCalled.set(true); }
        @Override public void registerMessageListener(MessageListenerOrderly l) { registerCalled.set(true); }
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
        @Override public QueryResult queryMessage(String t, String k, int m, long s, long e) { return null; }
        @Override public MessageExt viewMessage(String id, String topic) { return null; }
    }

    @Test
    public void shouldCreateWithDefaultConstructor() {
        RocketmqTemplate template = new RocketmqTemplate();
        assertNull(template.getProducer());
        assertNull(template.getConsumer());
    }

    @Test
    public void shouldCreateWithProducer() {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        assertSame(producer, template.getProducer());
    }

    @Test
    public void shouldSetAndGetProducer() {
        RocketmqTemplate template = new RocketmqTemplate();
        StubProducer producer = new StubProducer();
        template.setProducer(producer);
        assertSame(producer, template.getProducer());
    }

    @Test
    public void shouldSetAndGetConsumer() {
        RocketmqTemplate template = new RocketmqTemplate();
        StubConsumer consumer = new StubConsumer();
        template.setConsumer(consumer);
        assertSame(consumer, template.getConsumer());
    }

    @Test
    public void shouldFetchPublishMessageQueues() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        List<MessageQueue> queues = template.fetchPublishMessageQueues("TestTopic");
        assertNotNull(queues);
        assertEquals(1, queues.size());
    }

    @Test
    public void shouldSendWithTopicTagsKeysBody() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        SendResult result = template.send("Topic", "Tag", "Key", "body".getBytes());
        assertNotNull(result);
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendWithStringBody() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        SendResult result = template.send("Topic", "Tag", "Key", "body");
        assertNotNull(result);
    }

    @Test
    public void shouldSendMessage() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        SendResult result = template.send(msg);
        assertNotNull(result);
    }

    @Test
    public void shouldSendMessageWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        SendResult result = template.send(msg, 3000L);
        assertNotNull(result);
    }

    @Test
    public void shouldSendMessageAsync() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        template.send(msg, new SendCallback() {
            @Override public void onSuccess(SendResult sendResult) {}
            @Override public void onException(Throwable e) {}
        });
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendMessageAsyncWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        template.send(msg, new SendCallback() {
            @Override public void onSuccess(SendResult sendResult) {}
            @Override public void onException(Throwable e) {}
        }, 3000L);
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendOneway() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        template.sendOneway(msg);
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendToSpecificQueue() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueue mq = new MessageQueue("T", "b", 0);
        SendResult result = template.send(msg, mq);
        assertNotNull(result);
    }

    @Test
    public void shouldSendToSpecificQueueWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueue mq = new MessageQueue("T", "b", 0);
        SendResult result = template.send(msg, mq, 3000L);
        assertNotNull(result);
    }

    @Test
    public void shouldSendToSpecificQueueAsync() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueue mq = new MessageQueue("T", "b", 0);
        template.send(msg, mq, new SendCallback() {
            @Override public void onSuccess(SendResult sendResult) {}
            @Override public void onException(Throwable e) {}
        });
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendToSpecificQueueAsyncWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueue mq = new MessageQueue("T", "b", 0);
        template.send(msg, mq, new SendCallback() {
            @Override public void onSuccess(SendResult sendResult) {}
            @Override public void onException(Throwable e) {}
        }, 3000L);
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendOnewayToSpecificQueue() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueue mq = new MessageQueue("T", "b", 0);
        template.sendOneway(msg, mq);
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendToSelector() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueueSelector selector = new SelectMessageQueueByHash();
        SendResult result = template.send(msg, selector, "arg");
        assertNotNull(result);
    }

    @Test
    public void shouldSendToSelectorWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueueSelector selector = new SelectMessageQueueByHash();
        SendResult result = template.send(msg, selector, "arg", 3000L);
        assertNotNull(result);
    }

    @Test
    public void shouldSendToSelectorAsync() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueueSelector selector = new SelectMessageQueueByHash();
        template.send(msg, selector, "arg", new SendCallback() {
            @Override public void onSuccess(SendResult sendResult) {}
            @Override public void onException(Throwable e) {}
        });
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendToSelectorAsyncWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueueSelector selector = new SelectMessageQueueByHash();
        template.send(msg, selector, "arg", new SendCallback() {
            @Override public void onSuccess(SendResult sendResult) {}
            @Override public void onException(Throwable e) {}
        }, 3000L);
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendOnewayToSelector() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueueSelector selector = new SelectMessageQueueByHash();
        template.sendOneway(msg, selector, "arg");
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendTransactionMessage() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        template.sendMessageInTransaction(msg, null, "arg");
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendBatch() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Collection<Message> msgs = new ArrayList<>();
        msgs.add(new Message("T", "body".getBytes()));
        SendResult result = template.send(msgs);
        assertNotNull(result);
    }

    @Test
    public void shouldSendBatchWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Collection<Message> msgs = new ArrayList<>();
        msgs.add(new Message("T", "body".getBytes()));
        SendResult result = template.send(msgs, 3000L);
        assertNotNull(result);
    }

    @Test
    public void shouldSendBatchToQueue() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Collection<Message> msgs = new ArrayList<>();
        msgs.add(new Message("T", "body".getBytes()));
        MessageQueue mq = new MessageQueue("T", "b", 0);
        SendResult result = template.send(msgs, mq);
        assertNotNull(result);
    }

    @Test
    public void shouldSendBatchToQueueWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Collection<Message> msgs = new ArrayList<>();
        msgs.add(new Message("T", "body".getBytes()));
        MessageQueue mq = new MessageQueue("T", "b", 0);
        SendResult result = template.send(msgs, mq, 3000L);
        assertNotNull(result);
    }

    @Test
    public void shouldRegisterConcurrentMessageListener() {
        StubConsumer consumer = new StubConsumer();
        RocketmqTemplate template = new RocketmqTemplate();
        template.setConsumer(consumer);
        template.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> null);
        assertTrue(consumer.registerCalled.get());
    }

    @Test
    public void shouldRegisterOrderlyMessageListener() {
        StubConsumer consumer = new StubConsumer();
        RocketmqTemplate template = new RocketmqTemplate();
        template.setConsumer(consumer);
        template.registerMessageListener((MessageListenerOrderly) (msgs, context) -> null);
        assertTrue(consumer.registerCalled.get());
    }

    @Test
    public void shouldHaveHashSelector() {
        RocketmqTemplate template = new RocketmqTemplate();
        assertNotNull(template.HASH_SELECTOR);
    }

    @Test
    public void shouldHaveRandomSelector() {
        RocketmqTemplate template = new RocketmqTemplate();
        assertNotNull(template.RANDOOM_SELECTOR);
    }

    @Test
    public void shouldHaveMachineRoomSelector() {
        RocketmqTemplate template = new RocketmqTemplate();
        assertNotNull(template.Machine_RANDOOM_SELECTOR);
    }
}
