package org.apache.rocketmq.client.extension.event.handler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.extension.event.DefaultRocketmqEvent;
import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.ProxiedHandlerChain;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link AbstractEnabledMessageHandler}.
 */
public class AbstractEnabledMessageHandlerTest {

    private static class TestEnabledHandler extends AbstractEnabledMessageHandler<RocketmqEvent> {
        private boolean internalCalled = false;

        @Override
        protected void doHandlerInternal(RocketmqEvent event, HandlerChain<RocketmqEvent> handlerChain) throws Exception {
            internalCalled = true;
            handlerChain.doHandler(event);
        }
    }

    private RocketmqEvent createEvent() throws Exception {
        MessageExt msg = new MessageExt();
        msg.setTopic("T");
        msg.setTags("T");
        msg.setKeys("K");
        msg.setBody("b".getBytes());
        return new DefaultRocketmqEvent(msg);
    }

    @Test
    public void shouldBeEnabledByDefault() {
        TestEnabledHandler handler = new TestEnabledHandler();
        assertTrue(handler.isEnabled());
    }

    @Test
    public void shouldSetEnabled() {
        TestEnabledHandler handler = new TestEnabledHandler();
        handler.setEnabled(false);
        assertFalse(handler.isEnabled());
    }

    @Test
    public void shouldCallDoHandlerInternalWhenEnabled() throws Exception {
        TestEnabledHandler handler = new TestEnabledHandler();
        handler.setEnabled(true);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        handler.doHandler(event, chain);
        assertTrue(handler.internalCalled);
    }

    @Test
    public void shouldSkipDoHandlerInternalWhenDisabled() throws Exception {
        TestEnabledHandler handler = new TestEnabledHandler();
        handler.setEnabled(false);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        handler.doHandler(event, chain);
        assertFalse(handler.internalCalled);
    }
}
