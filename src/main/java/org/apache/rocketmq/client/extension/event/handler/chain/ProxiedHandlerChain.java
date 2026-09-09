package org.apache.rocketmq.client.extension.event.handler.chain;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.EventHandler;

/**
 * A {@link HandlerChain} implementation that delegates through a list of handlers
 * and then falls back to the original (proxied) chain.
 *
 * <p>Maintains a cursor position so each handler in the list is invoked in order
 * before delegating to the original chain.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see HandlerChain
 * @see EventHandler
 */
public class ProxiedHandlerChain implements HandlerChain<RocketmqEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ProxiedHandlerChain.class);
	
    private ProxiedHandlerChain orig;
    private List<EventHandler<RocketmqEvent>> handlers;
    private int currentPosition = 0;

    public ProxiedHandlerChain() {
        this.currentPosition = -1;
    }
    
    public ProxiedHandlerChain(ProxiedHandlerChain orig, List<EventHandler<RocketmqEvent>> handlers) {
        if (orig == null) {
            throw new NullPointerException("original HandlerChain cannot be null.");
        }
        this.orig = orig;
        this.handlers = handlers;
        this.currentPosition = 0;
    }

    @Override
	public void doHandler(RocketmqEvent event) throws Exception {
        if (this.handlers == null || this.handlers.size() == this.currentPosition) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking original filter chain.");
            }
            if(this.orig != null) {
            	this.orig.doHandler(event);
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking wrapped filter at index [" + this.currentPosition + "]");
            }
            this.handlers.get(this.currentPosition++).doHandler(event, this);
        }
    }
    
}
