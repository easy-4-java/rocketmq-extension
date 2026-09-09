package org.apache.rocketmq.client.extension.event.handler.chain.def;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.EventHandler;
import org.apache.rocketmq.client.extension.event.handler.NamedHandlerList;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.ProxiedHandlerChain;
import org.junit.Test;

/**
 * Tests for {@link DefaultHandlerChainManager}.
 */
public class DefaultHandlerChainManagerTest {

    @Test
    public void shouldCreateEmptyManager() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertNotNull(manager.getHandlers());
        assertTrue(manager.getHandlers().isEmpty());
    }

    @Test
    public void shouldAddHandler() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("testHandler", handler);
        assertNotNull(manager.getHandler("testHandler"));
    }

    @Test
    public void shouldSetAndGetHandlers() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertNotNull(manager.getHandlers());
    }

    @Test
    public void shouldSetAndGetHandlerChains() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertNotNull(manager.getHandlerChains());
    }

    @Test
    public void shouldGetHandler() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertNull(manager.getHandler("nonexistent"));
    }

    @Test
    public void shouldCreateChain() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        manager.addHandler("h1", handler);
        manager.createChain("chain1", "h1");
        assertNotNull(manager.getChain("chain1"));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenChainNameIsBlank() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        manager.createChain("", "h1");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenChainDefinitionIsBlank() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        manager.createChain("chain1", "");
    }

    @Test
    public void shouldCreateChainWithMultipleHandlers() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        manager.addHandler("h1", (event, chain) -> {});
        manager.addHandler("h2", (event, chain) -> {});
        manager.createChain("chain1", "h1,h2");
        NamedHandlerList<RocketmqEvent> chain = manager.getChain("chain1");
        assertNotNull(chain);
        assertEquals(2, chain.size());
    }

    @Test
    public void shouldAddToChain() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        manager.addHandler("h1", (event, chain) -> {});
        manager.createChain("chain1", "h1");
        manager.addHandler("h2", (event, chain) -> {});
        manager.addToChain("chain1", "h2");
        assertEquals(2, manager.getChain("chain1").size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenAddToChainWithBlankName() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        manager.addToChain("", "h1");
    }

    @Test(expected = Exception.class)
    public void shouldThrowWhenAddToChainWithUnknownHandler() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        manager.createChain("chain1", "");
        manager.addToChain("chain1", "nonexistent");
    }

    @Test
    public void shouldReturnHasChains() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertFalse(manager.hasChains());

        manager.addHandler("h1", (event, chain) -> {});
        manager.createChain("chain1", "h1");
        assertTrue(manager.hasChains());
    }

    @Test
    public void shouldGetChainNames() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        assertNotNull(manager.getChainNames());
        assertTrue(manager.getChainNames().isEmpty());

        manager.addHandler("h1", (event, chain) -> {});
        manager.createChain("chain1", "h1");
        assertTrue(manager.getChainNames().contains("chain1"));
    }

    @Test
    public void shouldProxy() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        manager.addHandler("h1", (event, chain) -> {});
        manager.createChain("chain1", "h1");

        ProxiedHandlerChain orig = new ProxiedHandlerChain();
        HandlerChain<RocketmqEvent> proxied = manager.proxy(orig, "chain1");
        assertNotNull(proxied);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenProxyWithUnknownChain() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        ProxiedHandlerChain orig = new ProxiedHandlerChain();
        manager.proxy(orig, "nonexistent");
    }

    @Test
    public void shouldOverwriteHandler() {
        DefaultHandlerChainManager manager = new DefaultHandlerChainManager();
        EventHandler<RocketmqEvent> h1 = (event, chain) -> {};
        EventHandler<RocketmqEvent> h2 = (event, chain) -> {};
        manager.addHandler("test", h1);
        manager.addHandler("test", h2);
        assertSame(h2, manager.getHandler("test"));
    }
}
