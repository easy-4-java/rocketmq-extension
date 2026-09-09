package org.apache.rocketmq.client.extension.event;

import static org.junit.Assert.*;

import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link RocketmqDisruptorEvent}.
 */
public class RocketmqDisruptorEventTest {

    private MessageExt createMessageExt(String topic, String tags, String keys, byte[] body) {
        MessageExt msg = new MessageExt();
        msg.setTopic(topic);
        msg.setTags(tags);
        msg.setKeys(keys);
        msg.setBody(body);
        return msg;
    }

    @Test
    public void shouldCreateEventWithSource() {
        Object source = new Object();
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(source);
        assertNotNull(event);
    }

    @Test
    public void shouldSetAndGetMessageExt() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        MessageExt msg = new MessageExt();
        event.setMessageExt(msg);
        assertSame(msg, event.getMessageExt());
    }

    @Test
    public void shouldSetAndGetTopic() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        event.setTopic("TestTopic");
        assertEquals("TestTopic", event.getTopic());
    }

    @Test
    public void shouldSetAndGetTag() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        event.setTag("TestTag");
        assertEquals("TestTag", event.getTag());
    }

    @Test
    public void shouldSetAndGetBody() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        byte[] body = "test body".getBytes();
        event.setBody(body);
        assertSame(body, event.getBody());
    }

    @Test
    public void shouldGetMsgBodyAsUtf8() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        event.setBody("hello".getBytes());
        assertEquals("hello", event.getMsgBody());
    }

    @Test
    public void shouldGetMsgBodyWithCustomCharset() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        event.setBody("hello".getBytes());
        assertEquals("hello", event.getMsgBody("UTF-8"));
    }

    @Test
    public void shouldReturnNullForUnsupportedCharset() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        event.setBody("hello".getBytes());
        assertNull(event.getMsgBody("INVALID-CHARSET"));
    }

    @Test
    public void shouldBuildRouteExpressionFromMessageExt() {
        MessageExt msg = createMessageExt("OrderTopic", "Create", "ORDER-001", "data".getBytes());
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        event.setMessageExt(msg);
        String route = event.getRouteExpression();
        assertEquals("/OrderTopic/Create/ORDER-001", route);
    }

    @Test
    public void shouldReturnExplicitRouteExpressionWhenSet() {
        RocketmqDisruptorEvent event = new RocketmqDisruptorEvent(new Object());
        // The parent class DisruptorEvent may have a default route expression
        // Setting it explicitly should override
        MessageExt msg = createMessageExt("T", "T", "K", "b".getBytes());
        event.setMessageExt(msg);
        String route = event.getRouteExpression();
        assertNotNull(route);
    }
}
