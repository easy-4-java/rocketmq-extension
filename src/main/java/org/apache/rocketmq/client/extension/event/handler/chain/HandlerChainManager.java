package org.apache.rocketmq.client.extension.event.handler.chain;

import java.util.Map;
import java.util.Set;

import org.apache.rocketmq.client.extension.event.RocketmqEvent;
import org.apache.rocketmq.client.extension.event.handler.EventHandler;
import org.apache.rocketmq.client.extension.event.handler.NamedHandlerList;

/**
 * Manager responsible for creating, maintaining, and resolving handler chains.
 *
 * @param <T> the concrete event type
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see HandlerChain
 * @see EventHandler
 */
public interface HandlerChainManager<T extends RocketmqEvent> {

	/**
	 * 获取所有HandlerChain
	 * @return
	 */
    Map<String, EventHandler<T>> getHandlers();

    /**
     * 根据指定的chainName获取Handler列表
     */
    NamedHandlerList<T> getChain(String chainName);

    /**
     * 是否有HandlerChain
     */
    boolean hasChains();

    /**
     * 获取HandlerChain名称列表
     */
    Set<String> getChainNames();

    /**
     * <p>生成代理HandlerChain,先执行chainName指定的filerChian,最后执行servlet容器的original<p>
     */
    HandlerChain<T> proxy(HandlerChain<T> original, String chainName);

   /**
    * 
    * <p>方法说明：增加handler到handler列表中<p>
    */
    void addHandler(String name, EventHandler<T> handler);
    
    /**
     * <p>方法说明：创建HandlerChain<p>
     */
    void createChain(String chainName, String chainDefinition);

    /**
     * <p>方法说明：追加handler到指定的HandlerChian中<p>
     */
    void addToChain(String chainName, String handlerName);
	
}