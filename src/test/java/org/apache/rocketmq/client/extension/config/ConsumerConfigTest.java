package org.apache.rocketmq.client.extension.config;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for {@link ConsumerConfig}.
 */
public class ConsumerConfigTest {

    @Test
    public void shouldReturnDefaultValues() {
        ConsumerConfig config = new ConsumerConfig();
        assertEquals("CLUSTERING", config.getMessageModel());
        assertEquals("CONSUME_FROM_LAST_OFFSET", config.getConsumeFromWhere());
        assertNotNull(config.getConsumeTimestamp());
        assertTrue(config.getSubscription().isEmpty());
        assertEquals(20, config.getConsumeThreadMin());
        assertEquals(64, config.getConsumeThreadMax());
        assertEquals(100000L, config.getAdjustThreadPoolNumsThreshold());
        assertEquals(2000, config.getConsumeConcurrentlyMaxSpan());
        assertEquals(1000, config.getPullThresholdForQueue());
        assertEquals(0L, config.getPullInterval());
        assertEquals(1, config.getConsumeMessageBatchMaxSize());
        assertEquals(32, config.getPullBatchSize());
        assertFalse(config.isPostSubscriptionWhenPull());
        assertFalse(config.isUnitMode());
        assertEquals(-1, config.getMaxReconsumeTimes());
        assertEquals(1000L, config.getSuspendCurrentQueueTimeMillis());
        assertEquals(15L, config.getConsumeTimeout());
        assertEquals(3, config.getRetryTimesWhenConsumeFailed());
    }

    @Test
    public void shouldSetAndGetConsumerGroup() {
        ConsumerConfig config = new ConsumerConfig();
        config.setConsumerGroup("testGroup");
        assertEquals("testGroup", config.getConsumerGroup());
    }

    @Test
    public void shouldSetAndGetMessageModel() {
        ConsumerConfig config = new ConsumerConfig();
        config.setMessageModel("BROADCASTING");
        assertEquals("BROADCASTING", config.getMessageModel());
    }

    @Test
    public void shouldSetAndGetConsumeFromWhere() {
        ConsumerConfig config = new ConsumerConfig();
        config.setConsumeFromWhere("CONSUME_FROM_FIRST_OFFSET");
        assertEquals("CONSUME_FROM_FIRST_OFFSET", config.getConsumeFromWhere());
    }

    @Test
    public void shouldSetAndGetConsumeTimestamp() {
        ConsumerConfig config = new ConsumerConfig();
        config.setConsumeTimestamp("20131223171201");
        assertEquals("20131223171201", config.getConsumeTimestamp());
    }

    @Test
    public void shouldSetAndGetSubscription() {
        ConsumerConfig config = new ConsumerConfig();
        Map<String, String> sub = new HashMap<>();
        sub.put("TopicTest", "TagA");
        config.setSubscription(sub);
        assertEquals(1, config.getSubscription().size());
        assertEquals("TagA", config.getSubscription().get("TopicTest"));
    }

    @Test
    public void shouldSetAndGetConsumeThreadMin() {
        ConsumerConfig config = new ConsumerConfig();
        config.setConsumeThreadMin(10);
        assertEquals(10, config.getConsumeThreadMin());
    }

    @Test
    public void shouldSetAndGetConsumeThreadMax() {
        ConsumerConfig config = new ConsumerConfig();
        config.setConsumeThreadMax(128);
        assertEquals(128, config.getConsumeThreadMax());
    }

    @Test
    public void shouldSetAndGetAdjustThreadPoolNumsThreshold() {
        ConsumerConfig config = new ConsumerConfig();
        config.setAdjustThreadPoolNumsThreshold(200000L);
        assertEquals(200000L, config.getAdjustThreadPoolNumsThreshold());
    }

    @Test
    public void shouldSetAndGetConsumeConcurrentlyMaxSpan() {
        ConsumerConfig config = new ConsumerConfig();
        config.setConsumeConcurrentlyMaxSpan(3000);
        assertEquals(3000, config.getConsumeConcurrentlyMaxSpan());
    }

    @Test
    public void shouldSetAndGetPullThresholdForQueue() {
        ConsumerConfig config = new ConsumerConfig();
        config.setPullThresholdForQueue(500);
        assertEquals(500, config.getPullThresholdForQueue());
    }

    @Test
    public void shouldSetAndGetPullInterval() {
        ConsumerConfig config = new ConsumerConfig();
        config.setPullInterval(100L);
        assertEquals(100L, config.getPullInterval());
    }

    @Test
    public void shouldSetAndGetConsumeMessageBatchMaxSize() {
        ConsumerConfig config = new ConsumerConfig();
        config.setConsumeMessageBatchMaxSize(10);
        assertEquals(10, config.getConsumeMessageBatchMaxSize());
    }

    @Test
    public void shouldSetAndGetPullBatchSize() {
        ConsumerConfig config = new ConsumerConfig();
        config.setPullBatchSize(64);
        assertEquals(64, config.getPullBatchSize());
    }

    @Test
    public void shouldSetAndGetPostSubscriptionWhenPull() {
        ConsumerConfig config = new ConsumerConfig();
        config.setPostSubscriptionWhenPull(true);
        assertTrue(config.isPostSubscriptionWhenPull());
    }

    @Test
    public void shouldSetAndGetUnitMode() {
        ConsumerConfig config = new ConsumerConfig();
        config.setUnitMode(true);
        assertTrue(config.isUnitMode());
    }

    @Test
    public void shouldSetAndGetMaxReconsumeTimes() {
        ConsumerConfig config = new ConsumerConfig();
        config.setMaxReconsumeTimes(16);
        assertEquals(16, config.getMaxReconsumeTimes());
    }

    @Test
    public void shouldSetAndGetSuspendCurrentQueueTimeMillis() {
        ConsumerConfig config = new ConsumerConfig();
        config.setSuspendCurrentQueueTimeMillis(2000L);
        assertEquals(2000L, config.getSuspendCurrentQueueTimeMillis());
    }

    @Test
    public void shouldSetAndGetConsumeTimeout() {
        ConsumerConfig config = new ConsumerConfig();
        config.setConsumeTimeout(30L);
        assertEquals(30L, config.getConsumeTimeout());
    }

    @Test
    public void shouldSetAndGetRetryTimesWhenConsumeFailed() {
        ConsumerConfig config = new ConsumerConfig();
        config.setRetryTimesWhenConsumeFailed(5);
        assertEquals(5, config.getRetryTimesWhenConsumeFailed());
    }
}
