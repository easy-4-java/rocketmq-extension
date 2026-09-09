package org.apache.rocketmq.client.extension.spring;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import org.apache.rocketmq.client.exception.RequestTimeoutException;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.RequestCallback;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
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

    /**
     * No-op base covering every abstract method of {@code MQProducer extends MQAdmin}
     * as of RocketMQ 5.5.1, so subclasses only override what they care about.
     */
    private abstract static class StubProducerBase implements MQProducer {

        @Override public void start() throws MQClientException {}

        @Override public void shutdown() {}

        @Override public List<MessageQueue> fetchPublishMessageQueues(String topic) throws MQClientException {
            return Collections.emptyList();
        }

        @Override public SendResult send(Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public SendResult send(Message msg, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public void send(Message msg, SendCallback callback) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {}

        @Override public void send(Message msg, SendCallback callback, long timeout) throws MQClientException, RemotingException, InterruptedException {}

        @Override public void sendOneway(Message msg) throws MQClientException, RemotingException, InterruptedException {}

        @Override public SendResult send(Message msg, MessageQueue mq) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public SendResult send(Message msg, MessageQueue mq, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public void send(Message msg, MessageQueue mq, SendCallback callback) throws MQClientException, RemotingException, InterruptedException {}

        @Override public void send(Message msg, MessageQueue mq, SendCallback callback, long timeout) throws MQClientException, RemotingException, InterruptedException {}

        @Override public void sendOneway(Message msg, MessageQueue mq) throws MQClientException, RemotingException, InterruptedException {}

        @Override public SendResult send(Message msg, MessageQueueSelector selector, Object arg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public SendResult send(Message msg, MessageQueueSelector selector, Object arg, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public void send(Message msg, MessageQueueSelector selector, Object arg, SendCallback callback) throws MQClientException, RemotingException, InterruptedException {}

        @Override public void send(Message msg, MessageQueueSelector selector, Object arg, SendCallback callback, long timeout) throws MQClientException, RemotingException, InterruptedException {}

        @Override public void sendOneway(Message msg, MessageQueueSelector selector, Object arg) throws MQClientException, RemotingException, InterruptedException {}

        @Override public TransactionSendResult sendMessageInTransaction(Message msg, Object arg) throws MQClientException {
            return null;
        }

        @Override public SendResult send(Collection<Message> msgs) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public SendResult send(Collection<Message> msgs, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public SendResult send(Collection<Message> msgs, MessageQueue mq) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public SendResult send(Collection<Message> msgs, MessageQueue mq, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return new SendResult();
        }

        @Override public void send(Collection<Message> msgs, SendCallback callback) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {}

        @Override public void send(Collection<Message> msgs, SendCallback callback, long timeout) throws MQClientException, RemotingException, InterruptedException {}

        @Override public void send(Collection<Message> msgs, MessageQueue mq, SendCallback callback) throws MQClientException, RemotingException, InterruptedException {}

        @Override public void send(Collection<Message> msgs, MessageQueue mq, SendCallback callback, long timeout) throws MQClientException, RemotingException, InterruptedException {}

        @Override public String recallMessage(String topic, String msgId) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return null;
        }

        @Override public Message request(Message msg, long timeout) throws RequestTimeoutException, MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return null;
        }

        @Override public void request(Message msg, RequestCallback callback, long timeout) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {}

        @Override public Message request(Message msg, MessageQueueSelector selector, Object arg, long timeout) throws RequestTimeoutException, MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return null;
        }

        @Override public void request(Message msg, MessageQueueSelector selector, Object arg, RequestCallback callback, long timeout) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {}

        @Override public Message request(Message msg, MessageQueue mq, long timeout) throws RequestTimeoutException, MQClientException, RemotingException, MQBrokerException, InterruptedException {
            return null;
        }

        @Override public void request(Message msg, MessageQueue mq, RequestCallback callback, long timeout) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {}

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

    /** Records whether any send-family method was invoked. */
    private static class StubProducer extends StubProducerBase {

        final AtomicBoolean sendCalled = new AtomicBoolean(false);

        @Override public SendResult send(Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public SendResult send(Message msg, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public void send(Message msg, SendCallback callback) {
            sendCalled.set(true);
        }

        @Override public void send(Message msg, SendCallback callback, long timeout) {
            sendCalled.set(true);
        }

        @Override public void sendOneway(Message msg) {
            sendCalled.set(true);
        }

        @Override public SendResult send(Message msg, MessageQueue mq) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public SendResult send(Message msg, MessageQueue mq, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public void send(Message msg, MessageQueue mq, SendCallback callback) {
            sendCalled.set(true);
        }

        @Override public void send(Message msg, MessageQueue mq, SendCallback callback, long timeout) {
            sendCalled.set(true);
        }

        @Override public void sendOneway(Message msg, MessageQueue mq) {
            sendCalled.set(true);
        }

        @Override public SendResult send(Message msg, MessageQueueSelector selector, Object arg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public SendResult send(Message msg, MessageQueueSelector selector, Object arg, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public void send(Message msg, MessageQueueSelector selector, Object arg, SendCallback callback) {
            sendCalled.set(true);
        }

        @Override public void send(Message msg, MessageQueueSelector selector, Object arg, SendCallback callback, long timeout) {
            sendCalled.set(true);
        }

        @Override public void sendOneway(Message msg, MessageQueueSelector selector, Object arg) {
            sendCalled.set(true);
        }

        @Override public TransactionSendResult sendMessageInTransaction(Message msg, Object arg) throws MQClientException {
            sendCalled.set(true);
            return null;
        }

        @Override public SendResult send(Collection<Message> msgs) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public SendResult send(Collection<Message> msgs, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public SendResult send(Collection<Message> msgs, MessageQueue mq) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public SendResult send(Collection<Message> msgs, MessageQueue mq, long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
            sendCalled.set(true);
            return new SendResult();
        }

        @Override public List<MessageQueue> fetchPublishMessageQueues(String topic) throws MQClientException {
            List<MessageQueue> queues = new ArrayList<MessageQueue>();
            queues.add(new MessageQueue(topic, "broker-a", 0));
            return queues;
        }
    }

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

    /** Records listener registration. */
    private static class StubConsumer extends StubConsumerBase {

        final AtomicBoolean registerCalled = new AtomicBoolean(false);

        @Override public void registerMessageListener(MessageListenerConcurrently listener) {
            registerCalled.set(true);
        }

        @Override public void registerMessageListener(MessageListenerOrderly listener) {
            registerCalled.set(true);
        }
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
        template.send(msg, noopCallback());
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendMessageAsyncWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        template.send(msg, noopCallback(), 3000L);
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
        template.send(msg, mq, noopCallback());
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendToSpecificQueueAsyncWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueue mq = new MessageQueue("T", "b", 0);
        template.send(msg, mq, noopCallback(), 3000L);
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
        template.send(msg, selector, "arg", noopCallback());
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendToSelectorAsyncWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Message msg = new Message("T", "body".getBytes());
        MessageQueueSelector selector = new SelectMessageQueueByHash();
        template.send(msg, selector, "arg", noopCallback(), 3000L);
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
        template.sendMessageInTransaction(msg, "arg");
        assertTrue(producer.sendCalled.get());
    }

    @Test
    public void shouldSendBatch() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Collection<Message> msgs = new ArrayList<Message>();
        msgs.add(new Message("T", "body".getBytes()));
        SendResult result = template.send(msgs);
        assertNotNull(result);
    }

    @Test
    public void shouldSendBatchWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Collection<Message> msgs = new ArrayList<Message>();
        msgs.add(new Message("T", "body".getBytes()));
        SendResult result = template.send(msgs, 3000L);
        assertNotNull(result);
    }

    @Test
    public void shouldSendBatchToQueue() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Collection<Message> msgs = new ArrayList<Message>();
        msgs.add(new Message("T", "body".getBytes()));
        MessageQueue mq = new MessageQueue("T", "b", 0);
        SendResult result = template.send(msgs, mq);
        assertNotNull(result);
    }

    @Test
    public void shouldSendBatchToQueueWithTimeout() throws Exception {
        StubProducer producer = new StubProducer();
        RocketmqTemplate template = new RocketmqTemplate(producer);
        Collection<Message> msgs = new ArrayList<Message>();
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

    private static SendCallback noopCallback() {
        return new SendCallback() {
            @Override public void onSuccess(SendResult sendResult) {}

            @Override public void onException(Throwable e) {}
        };
    }
}
