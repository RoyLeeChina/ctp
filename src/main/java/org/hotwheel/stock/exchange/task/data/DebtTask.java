package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.context.trade.DebtInfoList;
import org.hotwheel.stock.exchange.context.trade.OrderInfo;
import org.hotwheel.stock.exchange.context.trade.DebtInfo;
import org.hotwheel.stock.exchange.context.trade.ProductInfo;
import org.hotwheel.stock.exchange.http.DebtApi;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.stock.exchange.model.DHBid;
import org.hotwheel.stock.exchange.model.DHOrder;
import org.hotwheel.stock.exchange.model.ExchangeContext;
import org.hotwheel.util.CollectionUtils;
import org.mymmsc.api.assembly.Api;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 债务信息/协议批量文件
 *
 * Created by wangfeng on 2016/12/10.
 * @since 3.0.2
 */
public class DebtTask extends PartitionTask<ExchangeContext> {
    private static File file = null;
    private static String filename = "overdueDebts.csv";

    //private List<String> allDebts = null;
    public DebtTask() {
        super(Runtime.threadNum, Runtime.threshold, Runtime.batchSize, "DebtTask");
    }

    public DebtTask(int threadNum, int threshold, int batchSize, String taskName) {
        super(threadNum, threshold, batchSize, taskName);
    }

    @Override
    public ExchangeContext getContext() {
        ExchangeContext context = new ExchangeContext();
        synchronized (taskName) {
            if(file == null || isNewFile(file, reportPath, filename)) {
                file = createCVSFile(reportPath, filename);
            }
            context.file = file;
            context.allBidUuids = (List<String>) args[0];
        }
        return context;
    }

    @Override
    public boolean execute(ExchangeContext context) {
        List<String> data = context.rows;
        boolean bRet = false;

        if (CollectionUtils.isEmpty(context.allBidUuids)) {
            logger.info("allDebts is empty");
            return bRet;
        }
        List<String> allUuids = context.allBidUuids.subList(start, end);
        for (int k = 0; k < allUuids.size(); k++) {
            String bidUuids = allUuids.get(k);
            List<DebtInfo> debts = new ArrayList<>();
            for (int shard = 0; shard < Runtime.kShardMax; shard++) {
                long startId = 0;
                while (startId >= 0) {
                    DebtInfoList list = TradeInnerApi.getDebtList(bidUuids, startId, shard, false);
                    if (list == null || list.infoList == null || list.infoList.size() == 0) {
                        break;
                    } else {
                        startId = list.nextID;
                        if (list.infoList != null) {
                            for (DebtInfo debt : list.infoList) {
                                debts.add(debt);
                                context.creditors.add(debt.toUser);
                                context.debtIds.add(debt.uuid);
                                data.add(debt.toLine());
                            }
                        }
                    }
                }
            }
            {
                // 产品
                File productFile = createCVSFile(Runtime.reportPath, ProductInfo.filename);
                Collections.sort(debts, new Comparator<DebtInfo>() {
                    @Override
                    public int compare(DebtInfo o1, DebtInfo o2) {
                        int iRet = 0;
                        long p1 = Api.valueOf(long.class, o1.bid_uuid);
                        long p2 = Api.valueOf(long.class, o2.bid_uuid);
                        if (p1 < p2) {
                            iRet = -1;
                        } else if (p1 > p2){
                            iRet = 1;
                        } else {
                            iRet = 0;
                        }
                        return iRet;
                    }
                });

                String productId = null;
                String nextProductId = null;
                int total = debts.size();
                ProductInfo productInfo = new ProductInfo();
                for (int i = 0; i < total; i ++) {
                    DebtInfo debt = debts.get(i);
                    DebtInfo nextDebt = null;
                    if(i + 1 < total) {
                        nextDebt = debts.get(i + 1);
                        if (!debt.bid_uuid.equals(nextDebt.bid_uuid)) {
                            nextDebt = null;
                        }
                    }
                    productInfo.plus(debt);
                    if (nextDebt == null) {
                        context.numberOfBid += 1;
                        DHBid dhBid = DHBid.valueOf(productInfo);
                        DebtApi.submit(dhBid);
                        productInfo = new ProductInfo();
                    }
                }

                // 订单
                File orderFile = createCVSFile(Runtime.reportPath, OrderInfo.filename);
                for (int i = 0; i < total; i ++) {
                    OrderInfo orderInfo = new OrderInfo();
                    DebtInfo debt = debts.get(i);
                    orderInfo.plus(debt);
                    context.numberOfOrder += 1;
                    DHOrder order = DHOrder.valueOf(debt);
                    //BaseContext.writeToFile(orderFile, orderInfo.toLine());
                    DebtApi.submit(order);
                }
            }
        }

        return true;
    }
}
