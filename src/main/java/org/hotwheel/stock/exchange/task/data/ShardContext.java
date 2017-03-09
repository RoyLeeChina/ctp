package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.scheduling.PartitionContext;

/**
 * 交易分片任务
 *
 * Created by wangfeng on 2016/12/15.
 */
public class ShardContext extends PartitionContext<String, ShardContext> {

    @Override
    public boolean merge(ShardContext context) {
        rows.addAll(context.rows);
        return true;
    }
}
