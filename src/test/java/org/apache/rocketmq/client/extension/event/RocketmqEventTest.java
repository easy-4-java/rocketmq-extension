package org.apache.rocketmq.client.extension.event;

import static org.junit.Assert.*;

import org.apache.rocketmq.common.message.MessageAccessor;
import org.apache.rocketmq.common.message.MessageClientIDSetter;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link RocketmqEvent}.
 */
public class RocketmqEventTest {

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
        RocketmqEvent event = new RocketmqEvent(source);
        assertNotNull(event);
        assertEquals(source, event.getSource());
    }

    @Test
    public void shouldCreateEventFromMessageExt() throws Exception {
        MessageExt msg = createMessageExt("TopicTest", "TagA", "Key1", "hello".getBytes());
        RocketmqEvent event = new RocketmqEvent(msg);

        assertEquals("TopicTest", event.getTopic());
        assertEquals("TagA", event.getTag());
        assertNotNull(event.getBody());
        assertSame(msg, event.getMessageExt());
        assertEquals("/TopicTest/TagA/Key1", event.getRouteExpression());
    }

    @Test
    public void shouldGetMsgBodyAsUtf8() throws Exception {
        MessageExt msg = createMessageExt("Topic", "Tag", "Key", "hello".getBytes("UTF-8"));
        RocketmqEvent event = new RocketmqEvent(msg);
        assertEquals("hello", event.getMsgBody());
    }

    @Test
    public void shouldGetMsgBodyWithCustomCharset() throws Exception {
        MessageExt msg = createMessageExt("Topic", "Tag", "Key", "hello".getBytes("UTF-8"));
        RocketmqEvent event = new RocketmqEvent(msg);
        assertEquals("hello", event.getMsgBody("UTF-8"));
    }

    @Test
    public void shouldReturnNullForUnsupportedCharset() throws Exception {
        MessageExt msg = createMessageExt("Topic", "Tag", "Key", "hello".getBytes("UTF-8"));
        RocketmqEvent event = new RocketmqEvent(msg);
        assertNull(event.getMsgBody("INVALID-CHARSET"));
    }

    @Test
    public void shouldSetAndGetTopic() {
        RocketmqEvent event = new RocketmqEvent(new Object());
        event.setTopic("MyTopic");
        assertEquals("MyTopic", event.getTopic());
    }

    @Test
    public void shouldSetAndGetTag() {
        RocketmqEvent event = new RocketmqEvent(new Object());
        event.setTag("MyTag");
        assertEquals("MyTag", event.getTag());
    }

    @Test
    public void shouldSetAndGetBody() {
        RocketmqEvent event = new RocketmqEvent(new Object());
        byte[] body = "test".getBytes();
        event.setBody(body);
        assertSame(body, event.getBody());
    }

    @Test
    public void shouldSetAndGetMessageExt() {
        RocketmqEvent event = new RocketmqEvent(new Object());
        MessageExt msg = new MessageExt();
        event.setMessageExt(msg);
        assertSame(msg, event.getMessageExt());
    }

    @Test
    public void shouldSetAndGetRouteExpression() {
        RocketmqEvent event = new RocketmqEvent(new Object());
        event.setRouteExpression("/topic/tag/key");
        assertEquals("/topic/tag/key", event.getRouteExpression());
    }

    @Test
    public void shouldBuildRouteExpression() throws Exception {
        MessageExt msg = createMessageExt("OrderTopic", "Create", "ORDER-001", "data".getBytes());
        RocketmqEvent event = new RocketmqEvent(msg);
        assertEquals("/OrderTopic/Create/ORDER-001", event.getRouteExpression());
    }

    @Test
    public void shouldInheritFromApplicationEvent() throws Exception {
        MessageExt msg = createMessageExt("T", "T", "K", "b".getBytes());
        RocketmqEvent event = new RocketmqEvent(msg);
        assertTrue(event instanceof org.springframework.context.ApplicationEvent);
    }
}
