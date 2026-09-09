package org.apache.rocketmq.client.extension.exception;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for {@link EventHandleException}.
 */
public class EventHandleExceptionTest {

    @Test
    public void shouldCreateWithException() {
        Exception cause = new RuntimeException("cause");
        EventHandleException ex = new EventHandleException(cause);
        assertEquals("cause", ex.getMessage());
    }

    @Test
    public void shouldCreateWithErrorMessage() {
        EventHandleException ex = new EventHandleException("error occurred");
        assertEquals("error occurred", ex.getMessage());
    }

    @Test
    public void shouldCreateWithErrorMessageAndCause() {
        Throwable cause = new RuntimeException("root cause");
        EventHandleException ex = new EventHandleException("error", cause);
        assertEquals("error", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    public void shouldBeRuntimeException() {
        EventHandleException ex = new EventHandleException("test");
        assertTrue(ex instanceof RuntimeException);
    }
}
