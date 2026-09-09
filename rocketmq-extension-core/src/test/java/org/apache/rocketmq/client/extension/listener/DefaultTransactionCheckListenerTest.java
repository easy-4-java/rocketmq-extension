package org.apache.rocketmq.client.extension.listener;

import static org.junit.Assert.*;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

/**
 * Tests for {@link DefaultTransactionCheckListener}.
 */
public class DefaultTransactionCheckListenerTest {

    @Test
    public void shouldAlwaysCommit() {
        DefaultTransactionCheckListener listener = new DefaultTransactionCheckListener();
        MessageExt msg = new MessageExt();
        LocalTransactionState state = listener.checkLocalTransactionState(msg);
        assertEquals(LocalTransactionState.COMMIT_MESSAGE, state);
    }
}
