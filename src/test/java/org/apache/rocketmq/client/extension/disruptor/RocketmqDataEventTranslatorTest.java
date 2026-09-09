package org.apache.rocketmq.client.extension.disruptor;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.extension.event.RocketmqDisruptorEvent;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Test;

/**
 * Tests for {@link RocketmqDataEventTranslator}.
 */
public class RocketmqDataEventTranslatorTest {

    @Test
    public void shouldCreateTranslatorWithContext() throws Exception {
        MessageQueue mq = new MessageQueue("TopicTest", "broker-a", 0);
        ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(mq);
        RocketmqDataEventTranslator translator = new RocketmqDataEventTranslator(context);
        assertNotNull(translator);
        assertSame(context, translator.getContext());
    }

    @Test
    public void shouldTranslateMessageToEvent() throws Exception {
        MessageQueue mq = new MessageQueue("TopicTest", "broker-a", 0);
        ConsumeConcurrentlyContext context = new ConsumeConcurrentlyContext(mq);
        RocketmqDataEventTranslator translator = new RocketmqDataEventTranslator(context);

        MessageExt msg = new MessageExt();
        msg.setTopic("OrderTopic");
        msg.setTags("Create");
        msg.setBody("order-data".getBytes());

        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        translator.translateTo(event, 0L, msg);

        assertSame(msg, event.getMessageExt());
        assertEquals("OrderTopic", event.getTopic());
        assertEquals("Create", event.getTag());
        assertNotNull(event.getBody());
    }

    @Test
    public void shouldSetAndGetContext() throws Exception {
        MessageQueue mq = new MessageQueue("T", "B", 0);
        ConsumeConcurrentlyContext context1 = new ConsumeConcurrentlyContext(mq);
        ConsumeConcurrentlyContext context2 = new ConsumeConcurrentlyContext(mq);
        RocketmqDataEventTranslator translator = new RocketmqDataEventTranslator(context1);
        translator.setContext(context2);
        assertSame(context2, translator.getContext());
    }
}
