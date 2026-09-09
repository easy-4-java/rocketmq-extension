package org.apache.rocketmq.client.extension.disruptor;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.extension.event.RocketmqDisruptorEvent;
import org.junit.Test;

/**
 * Tests for {@link RocketmqDataEventFactory}.
 */
public class RocketmqDataEventFactoryTest {

    @Test
    public void shouldCreateNewInstance() {
        RocketmqDataEventFactory factory = new RocketmqDataEventFactory();
        RocketmqDisruptorEvent event = factory.newInstance();
        assertNotNull(event);
        assertTrue(event instanceof RocketmqDisruptorEvent);
    }

    @Test
    public void shouldCreateDistinctInstances() {
        RocketmqDataEventFactory factory = new RocketmqDataEventFactory();
        RocketmqDisruptorEvent event1 = factory.newInstance();
        RocketmqDisruptorEvent event2 = factory.newInstance();
        assertNotSame(event1, event2);
    }
}
