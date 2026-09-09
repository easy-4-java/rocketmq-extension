package org.apache.rocketmq.client.extension.exception;

import org.apache.rocketmq.client.exception.MQClientException;

/**
 * Extended {@link MQClientException} for RocketMQ extension library errors.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.rocketmq.client.exception.MQClientException
 */
@SuppressWarnings("serial")
public class RocketMQException extends MQClientException {

    public RocketMQException(int responseCode, String errorMessage) {
        super(responseCode, errorMessage);
    }
    
    public RocketMQException(Exception e) {
        super(e.getMessage(), null);
    }
    
    public RocketMQException(String errorMessage) {
        super(errorMessage, null);
    }
    
    public RocketMQException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
 
    
}
