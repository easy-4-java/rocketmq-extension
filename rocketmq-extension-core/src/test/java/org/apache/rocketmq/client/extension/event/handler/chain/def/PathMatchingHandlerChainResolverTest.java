package org.apache.rocketmq.client.extension.event.handler.chain.def;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.extension.event.DefaultRocketmqEvent;
import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.EventHandler;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChainManager;
import org.apache.rocketmq.client.extension.event.handler.chain.ProxiedHandlerChain;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link PathMatchingHandlerChainResolver}.
 */
public class PathMatchingHandlerChainResolverTest {

    private RocketmqEvent createEvent(String topic, String tags, String keys) throws Exception {
        MessageExt msg = new MessageExt();
        msg.setTopic(topic);
        msg.setTags(tags);
        msg.setKeys(keys);
        msg.setBody("body".getBytes());
        return new DefaultRocketmqEvent(msg);
    }

    @Test
    public void shouldCreateWithDefaults() {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        assertNotNull(resolver.getHandlerChainManager());
        assertNotNull(resolver.getPathMatcher());
    }

    @Test
    public void shouldReturnNullWhenNoChains() throws Exception {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        RocketmqEvent event = createEvent("Topic", "Tag", "Key");
        HandlerChain<RocketmqEvent> result = resolver.getChain(event, new ProxiedHandlerChain());
        assertNull(result);
    }

    @Test
    public void shouldReturnChainWhenPathMatches() throws Exception {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        HandlerChainManager<RocketmqEvent> manager = resolver.getHandlerChainManager();
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("h1", handler);
        manager.createChain("/Topic/Tag/**", "h1");

        RocketmqEvent event = createEvent("Topic", "Tag", "Key");
        HandlerChain<RocketmqEvent> orig = new ProxiedHandlerChain();
        HandlerChain<RocketmqEvent> result = resolver.getChain(event, orig);
        assertNotNull(result);
    }

    @Test
    public void shouldReturnNullWhenNoPathMatches() throws Exception {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        HandlerChainManager<RocketmqEvent> manager = resolver.getHandlerChainManager();
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("h1", handler);
        manager.createChain("/Other/**", "h1");

        RocketmqEvent event = createEvent("Topic", "Tag", "Key");
        HandlerChain<RocketmqEvent> result = resolver.getChain(event, new ProxiedHandlerChain());
        assertNull(result);
    }

    @Test
    public void shouldSetAndGetHandlerChainManager() {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        HandlerChainManager<RocketmqEvent> manager = new DefaultHandlerChainManager();
        resolver.setHandlerChainManager(manager);
        assertSame(manager, resolver.getHandlerChainManager());
    }

    @Test
    public void shouldSetAndGetPathMatcher() {
        PathMatchingHandlerChainResolver resolver = new PathMatchingHandlerChainResolver();
        assertNotNull(resolver.getPathMatcher());
    }
}
