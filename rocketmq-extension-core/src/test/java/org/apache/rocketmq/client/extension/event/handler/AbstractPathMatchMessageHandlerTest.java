package org.apache.rocketmq.client.extension.event.handler;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.DefaultRocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.ProxiedHandlerChain;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link AbstractPathMatchMessageHandler}.
 */
public class AbstractPathMatchMessageHandlerTest {

    private static class TestPathMatchHandler extends AbstractPathMatchMessageHandler<RocketmqEvent> {
        private boolean onPreHandleCalled = false;
        private boolean onPreHandleResult = true;

        @Override
        protected boolean onPreHandle(RocketmqEvent event) throws Exception {
            onPreHandleCalled = true;
            return onPreHandleResult;
        }
    }

    private RocketmqEvent createEvent(String topic, String tags, String keys) throws Exception {
        MessageExt msg = new MessageExt();
        msg.setTopic(topic);
        msg.setTags(tags);
        msg.setKeys(keys);
        msg.setBody("body".getBytes());
        return new DefaultRocketmqEvent(msg);
    }

    @Test
    public void shouldProcessPath() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        EventHandler<RocketmqEvent> result = handler.processPath("/test/**");
        assertSame(handler, result);
    }

    @Test
    public void shouldGetAppliedPaths() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.processPath("/path1/**");
        handler.processPath("/path2/**");
        assertEquals(2, handler.getAppliedPaths().size());
    }

    @Test
    public void shouldPassThroughWhenNoAppliedPaths() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("T", "T", "K");
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        // No paths applied, preHandle should return true
        boolean result = handler.preHandle(event);
        assertTrue(result);
    }

    @Test
    public void shouldCallOnPreHandleWhenPathMatches() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.processPath("/Topic/**");
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("Topic", "Tag", "Key");
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        handler.doHandlerInternal(event, chain);
        assertTrue(handler.onPreHandleCalled);
    }

    @Test
    public void shouldSkipWhenPathDoesNotMatch() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        handler.processPath("/Other/**");
        handler.setEnabled(true);
        RocketmqEvent event = createEvent("Topic", "Tag", "Key");
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        boolean result = handler.preHandle(event);
        assertTrue(result);
        assertFalse(handler.onPreHandleCalled);
    }

    @Test
    public void shouldSetAndGetPathMatcher() {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        assertNotNull(handler.getPathMatcher());
    }

    @Test
    public void shouldGetPathWithinEvent() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        RocketmqEvent event = createEvent("T", "Tag", "K");
        String path = handler.getPathWithinEvent(event);
        assertNotNull(path);
    }

    @Test
    public void shouldPathsMatch() throws Exception {
        TestPathMatchHandler handler = new TestPathMatchHandler();
        RocketmqEvent event = createEvent("Topic", "Tag", "Key");
        assertTrue(handler.pathsMatch("/Topic/**", event));
        assertFalse(handler.pathsMatch("/Other/**", event));
    }
}
