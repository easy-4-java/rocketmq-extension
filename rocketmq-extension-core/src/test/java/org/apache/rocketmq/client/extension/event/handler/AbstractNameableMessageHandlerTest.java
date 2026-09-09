package org.apache.rocketmq.client.extension.event.handler;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.junit.Test;

/**
 * Tests for {@link AbstractNameableMessageHandler}.
 */
public class AbstractNameableMessageHandlerTest {

    private static class TestNameableHandler extends AbstractNameableMessageHandler<RocketmqEvent> {
        @Override
        public void doHandler(RocketmqEvent event, HandlerChain<RocketmqEvent> handlerChain) throws Exception {
            handlerChain.doHandler(event);
        }
    }

    @Test
    public void shouldSetAndGetName() {
        TestNameableHandler handler = new TestNameableHandler();
        handler.setName("myHandler");
        assertEquals("myHandler", handler.getName());
    }

    @Test
    public void shouldReturnNullNameByDefault() {
        TestNameableHandler handler = new TestNameableHandler();
        assertNull(handler.getName());
    }
}
