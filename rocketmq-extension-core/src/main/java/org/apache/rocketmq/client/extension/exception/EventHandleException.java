package org.apache.rocketmq.client.extension.exception;

/**
 * Runtime exception thrown when an error occurs during event handler processing.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@SuppressWarnings("serial")
public class EventHandleException extends RuntimeException {

    public EventHandleException(Exception e) {
        super(e.getMessage(), null);
    }
    
    public EventHandleException(String errorMessage) {
        super(errorMessage, null);
    }
    
    public EventHandleException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
 
    
}
