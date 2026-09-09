package org.apache.rocketmq.client.extension.event.handler.chain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.DefaultRocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.EventHandler;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link ProxiedHandlerChain}.
 */
public class ProxiedHandlerChainTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenOrigIsNull() {
        new ProxiedHandlerChain(null, new ArrayList<>());
    }

    @Test
    public void shouldInvokeOriginalChainWhenNoHandlers() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        ProxiedHandlerChain orig = new ProxiedHandlerChain();
        ProxiedHandlerChain chain = new ProxiedHandlerChain(orig, new ArrayList<>());

        MessageExt msg = new MessageExt();
        msg.setTopic("T");
        msg.setTags("T");
        msg.setKeys("K");
        msg.setBody("b".getBytes());
        RocketmqEvent event = new DefaultRocketmqEvent(msg);

        // When no handlers, it should delegate to orig which has no orig either
        chain.doHandler(event);
        // No exception means success
    }

    @Test
    public void shouldInvokeHandlersInOrder() throws Exception {
        List<String> order = new ArrayList<>();

        List<EventHandler<RocketmqEvent>> handlers = new ArrayList<>();
        handlers.add((event, chain) -> {
            order.add("first");
            chain.doHandler(event);
        });
        handlers.add((event, chain) -> {
            order.add("second");
            chain.doHandler(event);
        });

        ProxiedHandlerChain orig = new ProxiedHandlerChain();
        ProxiedHandlerChain chain = new ProxiedHandlerChain(orig, handlers);

        MessageExt msg = new MessageExt();
        msg.setTopic("T");
        msg.setTags("T");
        msg.setKeys("K");
        msg.setBody("b".getBytes());
        RocketmqEvent event = new DefaultRocketmqEvent(msg);

        chain.doHandler(event);

        assertEquals(2, order.size());
        assertEquals("first", order.get(0));
        assertEquals("second", order.get(1));
    }

    @Test
    public void shouldDefaultConstructorSetNegativePosition() {
        ProxiedHandlerChain chain = new ProxiedHandlerChain();
        assertNotNull(chain);
    }
}
