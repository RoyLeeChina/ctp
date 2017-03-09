package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.trade.TradeDebtorList;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.util.CollectionUtils;

import java.util.List;

/**
 * 债务人ID任务
 *
 * Created by wangfeng on 2016/12/15.
 * @since 3.0.3
 */
public class DebtorIdTask extends PartitionTask<ShardContext> {
    public DebtorIdTask(int threadNum, int threshold, int batchSize, String taskName) {
        super(threadNum, threshold, batchSize, taskName);
    }

    @Override
    public ShardContext getContext() {
        ShardContext pc = new ShardContext();

        return pc;
    }

    @Override
    public boolean execute(ShardContext context) {
        boolean bRet = false;
        List<String> data = context.rows;
        List<Integer> shardList = (List<Integer>) args[0];
        //ConcurrentSkipListSet<String> allDebtors = (ConcurrentSkipListSet<String>)args[1];
        if (CollectionUtils.isEmpty(shardList)) {
            logger.info("shardList is empty");
            return bRet;
        }
        List<Integer> tmpList = shardList.subList(start, end);
        final int shard = tmpList.get(0);
        long startId = 0;
        logger.info("{}-shard: {}->{}, {}", taskName, start, end, shard);
        while (startId >= 0) {
            TradeDebtorList list = TradeInnerApi.getDebtorList(startId, shard);
            if (list != null) {
                startId = list.nextID;
                if (list.infoList != null) {
                    for (String debtorId : list.infoList) {
                        if (BaseContext.isInteger(debtorId)) {
                            //allDebtors.add(debtorId);
                            data.add(debtorId);
                        } else {
                            logger.error("debtorId-error: " + debtorId);
                        }
                    }
                }
            }
        }

        return true;
    }
}
