package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.context.trade.TradeProductIdList;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.stock.exchange.model.DefaultTaskContext;
import org.hotwheel.util.CollectionUtils;
import org.hotwheel.util.StringUtils;
import org.mymmsc.api.assembly.Api;

import java.util.List;

/**
 * 标的ID分片任务
 *
 * Created by wangfeng on 2016/12/16.
 */
public class BidUuidsTask extends PartitionTask<DefaultTaskContext> {
    public BidUuidsTask(int threadNum, int threshold, int batchSize, String taskName) {
        super(threadNum, threshold, batchSize, taskName);
    }

    @Override
    public DefaultTaskContext getContext() {
        DefaultTaskContext pc = new DefaultTaskContext();

        return pc;
    }

    @Override
    public boolean execute(DefaultTaskContext context) {
        boolean bRet = false;
        List<String> data = context.rows;
        List<Integer> shardList = (List<Integer>) args[0];
        List<String> allDebtors = (List<String>) args[1];
        //Set<String> bidList = (Set<String>) args[2];
        if (CollectionUtils.isEmpty(shardList)) {
            logger.info("shardList is empty");
            return bRet;
        }
        List<Integer> tmpList = shardList.subList(start, end);
        final int shard = tmpList.get(0);
        logger.info("{}-shard: {}->{}, {}", taskName, start, end, shard);
        int total = allDebtors.size();
        for (int i = 0; i < total; i += Runtime.batchSize) {
            int from = i;
            int to = from + Runtime.batchSize;
            if (to > total) {
                to = total;
            }
            List<String> subList = allDebtors.subList(from, to);
            String debtorId = StringUtils.collectionToCommaDelimitedString(subList);
            long startId = 0;
            while (startId >= 0) {
                TradeProductIdList list = TradeInnerApi.getProductList(debtorId, startId, shard);
                if (list != null) {
                    startId = list.nextID;
                    if (list.infoList != null) {
                        for (String productId : list.infoList) {
                            if (Api.isEmpty(productId)) {
                                logger.error("shard={},debtorId={}, productId为空", shard, debtorId);
                                continue;
                            }
                            data.add(productId.trim());
                        }
                    }
                }
            }
        }

        return true;
    }
}
