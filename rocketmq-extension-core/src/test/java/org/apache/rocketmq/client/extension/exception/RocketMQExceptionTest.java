package org.apache.rocketmq.client.extension.exception;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.exception.MQClientException;
import org.junit.Test;

/**
 * Tests for {@link RocketMQException}.
 */
public class RocketMQExceptionTest {

    @Test
    public void shouldCreateWithResponseCodeAndMessage() {
        RocketMQException ex = new RocketMQException(100, "topic missing");
        assertEquals("topic missing", ex.getErrorMessage());
        assertEquals(100, ex.getResponseCode());
    }

    @Test
    public void shouldCreateWithException() {
        Exception cause = new RuntimeException("cause");
        RocketMQException ex = new RocketMQException(cause);
        assertEquals("cause", ex.getErrorMessage());
    }

    @Test
    public void shouldCreateWithErrorMessage() {
        RocketMQException ex = new RocketMQException("something failed");
        assertEquals("something failed", ex.getErrorMessage());
    }

    @Test
    public void shouldCreateWithErrorMessageAndCause() {
        Throwable cause = new RuntimeException("root");
        RocketMQException ex = new RocketMQException("error", cause);
        assertEquals("error", ex.getErrorMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void shouldBeMQClientException() {
        RocketMQException ex = new RocketMQException("test");
        assertTrue(ex instanceof MQClientException);
    }
}
