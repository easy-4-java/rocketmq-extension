package org.apache.rocketmq.client.extension.event.handler;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChainResolver;
import org.apache.rocketmq.client.extension.event.handler.chain.ProxiedHandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.def.DefaultHandlerChainManager;
import org.apache.rocketmq.client.extension.event.handler.chain.def.PathMatchingHandlerChainResolver;
import org.apache.rocketmq.client.extension.exception.EventHandleException;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link AbstractRouteableMessageHandler}.
 */
public class AbstractRouteableMessageHandlerTest {

    private RocketmqEvent createEvent() throws Exception {
        MessageExt msg = new MessageExt();
        msg.setTopic("T");
        msg.setTags("T");
        msg.setKeys("K");
        msg.setBody("b".getBytes());
        return new RocketmqEvent(msg);
    }

    @Test
    public void shouldCreateWithDefaultConstructor() {
        AbstractRouteableMessageHandler<RocketmqEvent> handler = new AbstractRouteableMessageHandler<>();
        assertNull(handler.getHandlerChainResolver());
    }

    @Test
    public void shouldCreateWithResolver() {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        AbstractRouteableMessageHandler<RocketmqEvent> handler = new AbstractRouteableMessageHandler<>(resolver);
        assertSame(resolver, handler.getHandlerChainResolver());
    }

    @Test
    public void shouldSetAndGetHandlerChainResolver() {
        AbstractRouteableMessageHandler<RocketmqEvent> handler = new AbstractRouteableMessageHandler<>();
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        handler.setHandlerChainResolver(resolver);
        assertSame(resolver, handler.getHandlerChainResolver());
    }

    @Test
    public void shouldExecuteChainDirectlyWhenNoResolver() throws Exception {
        AbstractRouteableMessageHandler<RocketmqEvent> handler = new AbstractRouteableMessageHandler<>();
        handler.setEnabled(true);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        // Should not throw, just pass through
        handler.doHandlerInternal(event, chain);
    }

    @Test
    public void shouldExecuteChainWithResolver() throws Exception {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        DefaultHandlerChainManager manager = (DefaultHandlerChainManager) resolver.getHandlerChainManager();
        manager.addHandler("h1", (event, chain) -> {});
        manager.createChain("/T/**", "h1");

        AbstractRouteableMessageHandler<RocketmqEvent> handler = new AbstractRouteableMessageHandler<>(resolver);
        handler.setEnabled(true);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        handler.doHandlerInternal(event, chain);
    }

    @Test(expected = EventHandleException.class)
    public void shouldThrowEventHandleExceptionOnNonIOException() throws Exception {
        AbstractRouteableMessageHandler<RocketmqEvent> handler = new AbstractRouteableMessageHandler<>() {
            @Override
            protected void executeChain(RocketmqEvent event, HandlerChain<RocketmqEvent> origChain) throws Exception {
                throw new RuntimeException("test");
            }
        };
        handler.setEnabled(true);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        handler.doHandlerInternal(event, chain);
    }

    @Test
    public void shouldReturnOriginalChainWhenNoResolver() throws Exception {
        AbstractRouteableMessageHandler<RocketmqEvent> handler = new AbstractRouteableMessageHandler<>();
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain orig = new ProxiedHandlerChain();
        HandlerChain<RocketmqEvent> result = handler.getExecutionChain(event, orig);
        assertSame(orig, result);
    }

    @Test
    public void shouldReturnResolvedChain() throws Exception {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        DefaultHandlerChainManager manager = (DefaultHandlerChainManager) resolver.getHandlerChainManager();
        manager.addHandler("h1", (event, chain) -> {});
        manager.createChain("/T/**", "h1");

        AbstractRouteableMessageHandler<RocketmqEvent> handler = new AbstractRouteableMessageHandler<>(resolver);
        RocketmqEvent event = createEvent();
        ProxiedHandlerChain orig = new ProxiedHandlerChain();
        HandlerChain<RocketmqEvent> result = handler.getExecutionChain(event, orig);
        assertNotNull(result);
        assertNotSame(orig, result);
    }
}
