package org.apache.rocketmq.client.extension.listener;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * Default {@link TransactionCheckListener} that always commits the transaction.
 *
 * <p>This is a no-op implementation suitable for applications that do not require
 * transactional message back-checking. Override or replace for custom logic.</p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 * @see org.apache.rocketmq.client.producer.TransactionCheckListener
 */
public class DefaultTransactionCheckListener implements TransactionCheckListener {
	
	@Override
	public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
		
		// 进行业务检查
		
		return LocalTransactionState.COMMIT_MESSAGE;
	}

}
