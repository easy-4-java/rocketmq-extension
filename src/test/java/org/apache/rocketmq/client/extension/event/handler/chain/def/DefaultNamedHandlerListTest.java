package org.apache.rocketmq.client.extension.event.handler.chain.def;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.EventHandler;
import org.apache.rocketmq.client.extension.event.handler.chain.HandlerChain;
import org.apache.rocketmq.client.extension.event.handler.chain.ProxiedHandlerChain;
import org.junit.Test;

/**
 * Tests for {@link DefaultNamedHandlerList}.
 */
public class DefaultNamedHandlerListTest {

    @Test
    public void shouldCreateWithName() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertEquals("test", list.getName());
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldCreateWithNameAndBackingList() {
        List<EventHandler<RocketmqEvent>> backing = new ArrayList<>();
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test", backing);
        assertEquals("test", list.getName());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenBackingListIsNull() {
        new DefaultNamedHandlerList("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenNameIsBlank() {
        new DefaultNamedHandlerList("  ");
    }

    @Test
    public void shouldSetName() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("old");
        list.setName("new");
        assertEquals("new", list.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSetNameIsBlank() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        list.setName("");
    }

    @Test
    public void shouldDelegateSize() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertEquals(0, list.size());
    }

    @Test
    public void shouldDelegateIsEmpty() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldDelegateContains() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertFalse(list.contains("anything"));
    }

    @Test
    public void shouldDelegateIterator() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertNotNull(list.iterator());
        assertFalse(list.iterator().hasNext());
    }

    @Test
    public void shouldDelegateToArray() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertNotNull(list.toArray());
        assertEquals(0, list.toArray().length);
    }

    @Test
    public void shouldDelegateToArrayWithType() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent>[] arr = list.toArray(new EventHandler[0]);
        assertNotNull(arr);
        assertEquals(0, arr.length);
    }

    @Test
    public void shouldDelegateAdd() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        assertTrue(list.add(handler));
        assertEquals(1, list.size());
    }

    @Test
    public void shouldDelegateRemove() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertTrue(list.remove(handler));
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldDelegateContainsAll() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertTrue(list.containsAll(new ArrayList<>()));
    }

    @Test
    public void shouldDelegateAddAll() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        List<EventHandler<RocketmqEvent>> toAdd = new ArrayList<>();
        toAdd.add((event, chain) -> {});
        assertTrue(list.addAll(toAdd));
        assertEquals(1, list.size());
    }

    @Test
    public void shouldDelegateAddAllAtIndex() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        list.add((event, chain) -> {});
        List<EventHandler<RocketmqEvent>> toAdd = new ArrayList<>();
        toAdd.add((event, chain) -> {});
        assertTrue(list.addAll(0, toAdd));
        assertEquals(2, list.size());
    }

    @Test
    public void shouldDelegateRemoveAll() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        List<Object> toRemove = new ArrayList<>();
        toRemove.add(handler);
        assertTrue(list.removeAll(toRemove));
    }

    @Test
    public void shouldDelegateRetainAll() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        // retainAll on empty list returns false (nothing changed)
        assertFalse(list.retainAll(new ArrayList<>()));
    }

    @Test
    public void shouldDelegateClear() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        list.add((event, chain) -> {});
        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldDelegateGet() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertSame(handler, list.get(0));
    }

    @Test
    public void shouldDelegateSet() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> h1 = (event, chain) -> {};
        EventHandler<RocketmqEvent> h2 = (event, chain) -> {};
        list.add(h1);
        list.set(0, h2);
        assertSame(h2, list.get(0));
    }

    @Test
    public void shouldDelegateAddAtIndex() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(0, handler);
        assertEquals(1, list.size());
    }

    @Test
    public void shouldDelegateRemoveAtIndex() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertSame(handler, list.remove(0));
    }

    @Test
    public void shouldDelegateIndexOf() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertEquals(0, list.indexOf(handler));
    }

    @Test
    public void shouldDelegateLastIndexOf() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        EventHandler<RocketmqEvent> handler = (event, chain) -> {};
        list.add(handler);
        assertEquals(0, list.lastIndexOf(handler));
    }

    @Test
    public void shouldDelegateListIterator() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertNotNull(list.listIterator());
    }

    @Test
    public void shouldDelegateListIteratorAtIndex() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        assertNotNull(list.listIterator(0));
    }

    @Test
    public void shouldDelegateSubList() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        list.add((event, chain) -> {});
        assertNotNull(list.subList(0, 1));
    }

    @Test
    public void shouldProxy() {
        DefaultNamedHandlerList list = new DefaultNamedHandlerList("test");
        ProxiedHandlerChain orig = new ProxiedHandlerChain();
        HandlerChain<RocketmqEvent> proxied = list.proxy(orig);
        assertNotNull(proxied);
        assertTrue(proxied instanceof ProxiedHandlerChain);
    }
}
