package org.apache.rocketmq.client.extension.spring.hooks;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.exception.RequestTimeoutException;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.RequestCallback;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

/**
 * Tests for {@link MQProducerShutdownHook}.
 */
public class MQProducerShutdownHookTest {

    /**
     * No-op base covering every abstract method of {@code MQProducer extends MQAdmin}
     * as of RocketMQ 5.5.1.
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

    /** Records shutdown invocation. */
    private static class StubProducer extends StubProducerBase {

        final AtomicBoolean shutdownCalled = new AtomicBoolean(false);

        @Override public void shutdown() {
            shutdownCalled.set(true);
        }
    }

    @Test
    public void shouldExtendThread() {
        StubProducer producer = new StubProducer();
        MQProducerShutdownHook hook = new MQProducerShutdownHook(producer);
        assertTrue(hook instanceof Thread);
    }

    @Test
    public void shouldCallShutdownOnRun() {
        StubProducer producer = new StubProducer();
        MQProducerShutdownHook hook = new MQProducerShutdownHook(producer);
        hook.run();
        assertTrue(producer.shutdownCalled.get());
    }
}
