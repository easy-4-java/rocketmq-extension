package org.apache.rocketmq.client.extension.spring;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByMachineRoom;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByRandom;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * High-level template for sending and receiving messages via Apache RocketMQ.
 *
 * <p>Wraps {@link MQProducer} and {@link MQPushConsumer} to provide a simplified
 * API for synchronous, asynchronous, one-way, batch, and transactional message
 * sending, as well as consumer listener registration.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.rocketmq.client.producer.MQProducer
 * @see org.apache.rocketmq.client.consumer.MQPushConsumer
 */
public class RocketmqTemplate {

	public final MessageQueueSelector HASH_SELECTOR = new SelectMessageQueueByHash();
	public final MessageQueueSelector RANDOOM_SELECTOR = new SelectMessageQueueByRandom();
	public final MessageQueueSelector Machine_RANDOOM_SELECTOR = new SelectMessageQueueByMachineRoom();
	
	@Autowired
	protected MQProducer producer;
	@Autowired(required = false)
	protected MQPushConsumer consumer;

	/** Creates a new {@code RocketmqTemplate} with no producer or consumer assigned. */
	public RocketmqTemplate() {
	}

	/**
	 * Creates a new {@code RocketmqTemplate} backed by the given producer.
	 *
	 * @param producer the {@link MQProducer} to delegate message sending to
	 */
	public RocketmqTemplate(MQProducer producer) {
		this.producer = producer;
	}

	/**
	 * Fetches the publish message queues for the given topic.
	 *
	 * @param topic the topic name
	 * @return list of {@link MessageQueue} for the topic
	 * @throws MQClientException if the RocketMQ client encounters an error
	 */
	public List<MessageQueue> fetchPublishMessageQueues(final String topic) throws MQClientException {
		return producer.fetchPublishMessageQueues(topic);
	}

	/**
	 * Sends a message with byte array body synchronously.
	 *
	 * @param topic the topic name
	 * @param tags  the message tags for filtering
	 * @param keys  the business-unique key
	 * @param body  the message body as byte array
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final String topic, final String tags, final String keys, final byte[] body)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {

		Message msg = new Message(topic, // topic
				tags, // tag
				keys, // key用于标识业务的唯一性
				body// body 二进制字节数组
		);

		return producer.send(msg);
	}

	/**
	 * Sends a message with String body synchronously.
	 *
	 * @param topic the topic name
	 * @param tags  the message tags for filtering
	 * @param keys  the business-unique key
	 * @param body  the message body as a String
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException            if the RocketMQ client encounters an error
	 * @throws RemotingException            if a network communication error occurs
	 * @throws MQBrokerException            if the broker returns an error
	 * @throws InterruptedException         if the sending thread is interrupted
	 * @throws UnsupportedEncodingException if the default charset is not supported
	 */
	public SendResult send(final String topic, final String tags, final String keys, final String body)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException, UnsupportedEncodingException {
		Message msg = new Message(topic, // topic
				tags, // tag
				keys, // key用于标识业务的唯一性
				body.getBytes(RemotingHelper.DEFAULT_CHARSET)// body 二进制字节数组
		);
		return producer.send(msg);
	}

	/**
	 * Sends a message synchronously using the default routing strategy.
	 *
	 * @param msg the {@link Message} to send
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Message msg)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msg);
	}

	/**
	 * Sends a message synchronously with a custom timeout.
	 *
	 * @param msg     the {@link Message} to send
	 * @param timeout the send timeout in milliseconds
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Message msg, final long timeout)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msg, timeout);
	}

	/**
	 * Sends a message asynchronously with a callback.
	 *
	 * @param msg          the {@link Message} to send
	 * @param sendCallback the callback to invoke on completion
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void send(final Message msg, final SendCallback sendCallback)
			throws MQClientException, RemotingException, InterruptedException {
		producer.send(msg, sendCallback);
	}

	/**
	 * Sends a message asynchronously with a callback and a custom timeout.
	 *
	 * @param msg          the {@link Message} to send
	 * @param sendCallback the callback to invoke on completion
	 * @param timeout      the send timeout in milliseconds
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void send(final Message msg, final SendCallback sendCallback, final long timeout)
			throws MQClientException, RemotingException, InterruptedException {
		producer.send(msg, sendCallback, timeout);
	}

	/**
	 * Sends a message in one-way mode without waiting for a broker acknowledgement.
	 *
	 * @param msg the {@link Message} to send
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void sendOneway(final Message msg) throws MQClientException, RemotingException, InterruptedException {
		producer.sendOneway(msg);
	}

	/**
	 * Sends a message synchronously to a specific message queue.
	 *
	 * @param msg the {@link Message} to send
	 * @param mq  the target {@link MessageQueue}
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Message msg, final MessageQueue mq)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msg, mq);
	}

	/**
	 * Sends a message synchronously to a specific message queue with a custom timeout.
	 *
	 * @param msg     the {@link Message} to send
	 * @param mq      the target {@link MessageQueue}
	 * @param timeout the send timeout in milliseconds
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Message msg, final MessageQueue mq, final long timeout)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msg, mq, timeout);
	}

	/**
	 * Sends a message asynchronously to a specific message queue.
	 *
	 * @param msg          the {@link Message} to send
	 * @param mq           the target {@link MessageQueue}
	 * @param sendCallback the callback to invoke on completion
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void send(final Message msg, final MessageQueue mq, final SendCallback sendCallback)
			throws MQClientException, RemotingException, InterruptedException {
		producer.send(msg, mq, sendCallback);
	}

	/**
	 * Sends a message asynchronously to a specific message queue with a custom timeout.
	 *
	 * @param msg          the {@link Message} to send
	 * @param mq           the target {@link MessageQueue}
	 * @param sendCallback the callback to invoke on completion
	 * @param timeout      the send timeout in milliseconds
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void send(final Message msg, final MessageQueue mq, final SendCallback sendCallback, long timeout)
			throws MQClientException, RemotingException, InterruptedException {
		producer.send(msg, mq, sendCallback, timeout);
	}

	/**
	 * Sends a message in one-way mode to a specific message queue.
	 *
	 * @param msg the {@link Message} to send
	 * @param mq  the target {@link MessageQueue}
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void sendOneway(final Message msg, final MessageQueue mq)
			throws MQClientException, RemotingException, InterruptedException {
		producer.sendOneway(msg, mq);
	}

	/**
	 * Sends a message synchronously using a custom queue selector.
	 *
	 * @param msg      the {@link Message} to send
	 * @param selector the {@link MessageQueueSelector} for routing
	 * @param arg      the argument passed to the selector
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Message msg, final MessageQueueSelector selector, final Object arg)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msg, selector, arg);
	}

	/**
	 * Sends a message synchronously using a custom queue selector with a timeout.
	 *
	 * @param msg      the {@link Message} to send
	 * @param selector the {@link MessageQueueSelector} for routing
	 * @param arg      the argument passed to the selector
	 * @param timeout  the send timeout in milliseconds
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Message msg, final MessageQueueSelector selector, final Object arg, final long timeout)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msg, selector, arg, timeout);
	}

	/**
	 * Sends a message asynchronously using a custom queue selector.
	 *
	 * @param msg          the {@link Message} to send
	 * @param selector     the {@link MessageQueueSelector} for routing
	 * @param arg          the argument passed to the selector
	 * @param sendCallback the callback to invoke on completion
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void send(final Message msg, final MessageQueueSelector selector, final Object arg,
			final SendCallback sendCallback) throws MQClientException, RemotingException, InterruptedException {
		producer.send(msg, selector, arg, sendCallback);
	}

	/**
	 * Sends a message asynchronously using a custom queue selector with a timeout.
	 *
	 * @param msg          the {@link Message} to send
	 * @param selector     the {@link MessageQueueSelector} for routing
	 * @param arg          the argument passed to the selector
	 * @param sendCallback the callback to invoke on completion
	 * @param timeout      the send timeout in milliseconds
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void send(final Message msg, final MessageQueueSelector selector, final Object arg,
			final SendCallback sendCallback, final long timeout)
			throws MQClientException, RemotingException, InterruptedException {
		producer.send(msg, selector, arg, sendCallback, timeout);
	}

	/**
	 * Sends a message in one-way mode using a custom queue selector.
	 *
	 * @param msg      the {@link Message} to send
	 * @param selector the {@link MessageQueueSelector} for routing
	 * @param arg      the argument passed to the selector
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public void sendOneway(final Message msg, final MessageQueueSelector selector, final Object arg)
			throws MQClientException, RemotingException, InterruptedException {
		producer.sendOneway(msg, selector, arg);
	}

	/**
	 * Sends a transactional message.
	 *
	 * @param msg          the {@link Message} to send
	 * @param tranExecuter the local transaction executor
	 * @param arg          the argument passed to the executor
	 * @return the {@link TransactionSendResult} from the broker
	 * @throws MQClientException if the RocketMQ client encounters an error
	 */
	public TransactionSendResult sendMessageInTransaction(final Message msg,
			final LocalTransactionExecuter tranExecuter, final Object arg) throws MQClientException {
		return producer.sendMessageInTransaction(msg, tranExecuter, arg);
	}

	/**
	 * Sends a batch of messages synchronously.
	 *
	 * @param msgs the collection of {@link Message} to send
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Collection<Message> msgs)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msgs);
	}

	/**
	 * Sends a batch of messages synchronously with a custom timeout.
	 *
	 * @param msgs    the collection of {@link Message} to send
	 * @param timeout the send timeout in milliseconds
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Collection<Message> msgs, final long timeout)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msgs, timeout);
	}

	/**
	 * Sends a batch of messages synchronously to a specific message queue.
	 *
	 * @param msgs the collection of {@link Message} to send
	 * @param mq   the target {@link MessageQueue}
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Collection<Message> msgs, final MessageQueue mq)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msgs, mq);
	}

	/**
	 * Sends a batch of messages synchronously to a specific message queue with a custom timeout.
	 *
	 * @param msgs    the collection of {@link Message} to send
	 * @param mq      the target {@link MessageQueue}
	 * @param timeout the send timeout in milliseconds
	 * @return the {@link SendResult} from the broker
	 * @throws MQClientException    if the RocketMQ client encounters an error
	 * @throws RemotingException    if a network communication error occurs
	 * @throws MQBrokerException    if the broker returns an error
	 * @throws InterruptedException if the sending thread is interrupted
	 */
	public SendResult send(final Collection<Message> msgs, final MessageQueue mq, final long timeout)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		return producer.send(msgs, mq, timeout);
	}

	/**
	 * Registers a concurrent message listener on the consumer.
	 *
	 * @param messageListener the {@link MessageListenerConcurrently} to register
	 */
	public void registerMessageListener(final MessageListenerConcurrently messageListener){
		consumer.registerMessageListener(messageListener);
	}

	/**
	 * Registers an orderly message listener on the consumer.
	 *
	 * @param messageListener the {@link MessageListenerOrderly} to register
	 */
	public void registerMessageListener(final MessageListenerOrderly messageListener){
		consumer.registerMessageListener(messageListener);
	}

	/**
	 * Returns the underlying {@link MQProducer}.
	 *
	 * @return the producer instance
	 */
	public MQProducer getProducer() {
		return producer;
	}

	/**
	 * Sets the underlying {@link MQProducer}.
	 *
	 * @param producer the producer instance to use
	 */
	public void setProducer(MQProducer producer) {
		this.producer = producer;
	}

	/**
	 * Returns the underlying {@link MQPushConsumer}.
	 *
	 * @return the consumer instance
	 */
	public MQPushConsumer getConsumer() {
		return consumer;
	}

	/**
	 * Sets the underlying {@link MQPushConsumer}.
	 *
	 * @param consumer the consumer instance to use
	 */
	public void setConsumer(MQPushConsumer consumer) {
		this.consumer = consumer;
	}
	
}
