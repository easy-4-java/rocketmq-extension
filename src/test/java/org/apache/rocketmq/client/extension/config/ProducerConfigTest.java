package org.apache.rocketmq.client.extension.config;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link ProducerConfig}.
 */
public class ProducerConfigTest {

    @Test
    public void shouldReturnDefaultProducerGroup() {
        ProducerConfig config = new ProducerConfig();
        assertEquals("ProducerGroup", config.getProducerGroup());
    }

    @Test
    public void shouldSetAndGetProducerGroup() {
        ProducerConfig config = new ProducerConfig();
        config.setProducerGroup("MyGroup");
        assertEquals("MyGroup", config.getProducerGroup());
    }

    @Test
    public void shouldReturnDefaultCreateTopicKey() {
        ProducerConfig config = new ProducerConfig();
        assertNotNull(config.getCreateTopicKey());
    }

    @Test
    public void shouldSetAndGetCreateTopicKey() {
        ProducerConfig config = new ProducerConfig();
        config.setCreateTopicKey("MyTopic");
        assertEquals("MyTopic", config.getCreateTopicKey());
    }

    @Test
    public void shouldReturnDefaultDefaultTopicQueueNums() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(4, config.getDefaultTopicQueueNums());
    }

    @Test
    public void shouldSetAndGetDefaultTopicQueueNums() {
        ProducerConfig config = new ProducerConfig();
        config.setDefaultTopicQueueNums(8);
        assertEquals(8, config.getDefaultTopicQueueNums());
    }

    @Test
    public void shouldReturnDefaultSendMsgTimeout() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(3000, config.getSendMsgTimeout());
    }

    @Test
    public void shouldSetAndGetSendMsgTimeout() {
        ProducerConfig config = new ProducerConfig();
        config.setSendMsgTimeout(5000);
        assertEquals(5000, config.getSendMsgTimeout());
    }

    @Test
    public void shouldReturnDefaultSendLatencyFaultEnable() {
        ProducerConfig config = new ProducerConfig();
        assertFalse(config.isSendLatencyFaultEnable());
    }

    @Test
    public void shouldSetAndGetSendLatencyFaultEnable() {
        ProducerConfig config = new ProducerConfig();
        config.setSendLatencyFaultEnable(true);
        assertTrue(config.isSendLatencyFaultEnable());
    }

    @Test
    public void shouldReturnDefaultCompressMsgBodyOverHowmuch() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(1024 * 4, config.getCompressMsgBodyOverHowmuch());
    }

    @Test
    public void shouldSetAndGetCompressMsgBodyOverHowmuch() {
        ProducerConfig config = new ProducerConfig();
        config.setCompressMsgBodyOverHowmuch(2048);
        assertEquals(2048, config.getCompressMsgBodyOverHowmuch());
    }

    @Test
    public void shouldReturnDefaultRetryTimesWhenSendFailed() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(2, config.getRetryTimesWhenSendFailed());
    }

    @Test
    public void shouldSetAndGetRetryTimesWhenSendFailed() {
        ProducerConfig config = new ProducerConfig();
        config.setRetryTimesWhenSendFailed(3);
        assertEquals(3, config.getRetryTimesWhenSendFailed());
    }

    @Test
    public void shouldReturnDefaultRetryTimesWhenSendAsyncFailed() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(2, config.getRetryTimesWhenSendAsyncFailed());
    }

    @Test
    public void shouldSetAndGetRetryTimesWhenSendAsyncFailed() {
        ProducerConfig config = new ProducerConfig();
        config.setRetryTimesWhenSendAsyncFailed(5);
        assertEquals(5, config.getRetryTimesWhenSendAsyncFailed());
    }

    @Test
    public void shouldReturnDefaultRetryAnotherBrokerWhenNotStoreOK() {
        ProducerConfig config = new ProducerConfig();
        assertFalse(config.isRetryAnotherBrokerWhenNotStoreOK());
    }

    @Test
    public void shouldSetAndGetRetryAnotherBrokerWhenNotStoreOK() {
        ProducerConfig config = new ProducerConfig();
        config.setRetryAnotherBrokerWhenNotStoreOK(true);
        assertTrue(config.isRetryAnotherBrokerWhenNotStoreOK());
    }

    @Test
    public void shouldReturnDefaultMaxMessageSize() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(1024 * 1024 * 4, config.getMaxMessageSize());
    }

    @Test
    public void shouldSetAndGetMaxMessageSize() {
        ProducerConfig config = new ProducerConfig();
        config.setMaxMessageSize(1024);
        assertEquals(1024, config.getMaxMessageSize());
    }

    @Test
    public void shouldSetAndGetLatencyMax() {
        ProducerConfig config = new ProducerConfig();
        long[] latency = {50L, 100L, 200L};
        config.setLatencyMax(latency);
        assertArrayEquals(latency, config.getLatencyMax());
    }

    @Test
    public void shouldSetAndGetNotAvailableDuration() {
        ProducerConfig config = new ProducerConfig();
        long[] duration = {1000L, 2000L};
        config.setNotAvailableDuration(duration);
        assertArrayEquals(duration, config.getNotAvailableDuration());
    }

    @Test
    public void shouldReturnDefaultTransaction() {
        ProducerConfig config = new ProducerConfig();
        assertFalse(config.isTransaction());
    }

    @Test
    public void shouldSetAndGetTransaction() {
        ProducerConfig config = new ProducerConfig();
        config.setTransaction(true);
        assertTrue(config.isTransaction());
    }

    @Test
    public void shouldSetAndGetTransactionCheckListener() {
        ProducerConfig config = new ProducerConfig();
        assertNull(config.getTransactionCheckListener());
    }

    @Test
    public void shouldReturnDefaultCheckThreadPoolMinSize() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(1, config.getCheckThreadPoolMinSize());
    }

    @Test
    public void shouldSetAndGetCheckThreadPoolMinSize() {
        ProducerConfig config = new ProducerConfig();
        config.setCheckThreadPoolMinSize(5);
        assertEquals(5, config.getCheckThreadPoolMinSize());
    }

    @Test
    public void shouldReturnDefaultCheckThreadPoolMaxSize() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(1, config.getCheckThreadPoolMaxSize());
    }

    @Test
    public void shouldSetAndGetCheckThreadPoolMaxSize() {
        ProducerConfig config = new ProducerConfig();
        config.setCheckThreadPoolMaxSize(10);
        assertEquals(10, config.getCheckThreadPoolMaxSize());
    }

    @Test
    public void shouldReturnDefaultCheckRequestHoldMax() {
        ProducerConfig config = new ProducerConfig();
        assertEquals(2000, config.getCheckRequestHoldMax());
    }

    @Test
    public void shouldSetAndGetCheckRequestHoldMax() {
        ProducerConfig config = new ProducerConfig();
        config.setCheckRequestHoldMax(5000);
        assertEquals(5000, config.getCheckRequestHoldMax());
    }

    @Test
    public void shouldReturnDefaultWhenProducerGroupIsEmpty() {
        ProducerConfig config = new ProducerConfig();
        config.setProducerGroup("");
        assertEquals("ProducerGroup", config.getProducerGroup());
    }
}
