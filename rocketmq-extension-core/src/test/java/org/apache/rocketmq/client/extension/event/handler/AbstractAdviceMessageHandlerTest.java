package org.apache.rocketmq.client.extension.event.handler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.DefaultRocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.ProxiedHandlerChain;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link AbstractAdviceMessageHandler}.
 */
public class AbstractAdviceMessageHandlerTest {

    private static class TestAdviceHandler extends AbstractAdviceMessageHandler<RocketmqEvent> {
        private boolean preHandleCalled = false;
        private boolean postHandleCalled = false;
        private boolean afterCompletionCalled = false;
        private boolean executeChainCalled = false;
        private boolean preHandleResult = true;

        @Override
        protected boolean preHandle(RocketmqEvent event) throws Exception {
            preHandleCalled = true;
            return preHandleResult;
        }

        @Override
        protected void postHandle(RocketmqEvent event) throws Exception {
            postHandleCalled = true;
        }

        @Override
        public void afterCompletion(RocketmqEvent event, Exception exception) throws Exception {
            afterCompletionCalled = true;
        }

        @Override
        protected void executeChain(RocketmqEvent event, HandlerChain<RocketmqEvent> chain) throws Exception {
            executeChainCalled = true;
            super.executeChain(event, chain);
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
    public void shouldInvokeFullLifecycleWhenEnabled() throws Exception {
        TestAdviceHandler handler = new TestAdviceHandler();
        handler.setEnabled(true);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();

        handler.doHandlerInternal(event, chain);

        assertTrue(handler.preHandleCalled);
        assertTrue(handler.executeChainCalled);
        assertTrue(handler.postHandleCalled);
        assertTrue(handler.afterCompletionCalled);
    }

    @Test
    public void shouldSkipExecutionWhenPreHandleReturnsFalse() throws Exception {
        TestAdviceHandler handler = new TestAdviceHandler();
        handler.preHandleResult = false;
        handler.setEnabled(true);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();

        handler.doHandlerInternal(event, chain);

        assertTrue(handler.preHandleCalled);
        assertFalse(handler.executeChainCalled);
        assertTrue(handler.postHandleCalled);
    }

    @Test
    public void shouldSkipWhenDisabled() throws Exception {
        TestAdviceHandler handler = new TestAdviceHandler();
        handler.setEnabled(false);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();

        handler.doHandlerInternal(event, chain);

        assertFalse(handler.preHandleCalled);
    }

    @Test
    public void shouldCallAfterCompletionOnException() throws Exception {
        TestAdviceHandler handler = new TestAdviceHandler() {
            @Override
            protected void executeChain(RocketmqEvent event, HandlerChain<RocketmqEvent> chain) throws Exception {
                throw new RuntimeException("test error");
            }
        };
        handler.setEnabled(true);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();

        handler.doHandlerInternal(event, chain);
        assertTrue(handler.afterCompletionCalled);
    }

    @Test
    public void shouldSetAndGetEnabled() {
        AbstractAdviceMessageHandler<RocketmqEvent> handler = new TestAdviceHandler();
        handler.setEnabled(true);
        assertTrue(handler.isEnabled());
        handler.setEnabled(false);
        assertFalse(handler.isEnabled());
    }
}
