package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.context.trade.RepayInfoList;
import org.hotwheel.stock.exchange.model.DefaultTaskContext;
import org.hotwheel.stock.exchange.context.trade.RepayInfo;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.util.CollectionUtils;
import org.hotwheel.util.StringUtils;
import org.mymmsc.api.assembly.Api;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 修复还款数据
 *
 * Created by wangfeng on 2017/1/13.
 */
public class RepayflowFixTask extends PartitionTask<DefaultTaskContext> {
    private static File file = null;
    private String filename = "overdueRepayFlow.csv";
    private String[] fields = {"id","product_id","trade_no","person_id","loan_id","trade_date","trade_time","subject_name","tradeTerm","tranAmt","sub_type","trade_type","trade_method","memo","isDelete"};
    private String valueTemp = "${id},${product_id},${trade_no},${person_id},${loan_id},${trade_date},${trade_time},${subject_name},${tradeTerm},${tranAmt},${sub_type},${trade_type},${trade_method},${memo},${isDelete}";

    public RepayflowFixTask(int threadNum, int threshold, int batchSize, String taskName) {
        super(threadNum, threshold, batchSize, taskName);
    }

    @Override
    public DefaultTaskContext getContext() {
        DefaultTaskContext pc = new DefaultTaskContext();
        synchronized (taskName) {
            if(file == null || isNewFile(file, reportPath, filename)) {
                file = createCVSFile(reportPath, filename);
            }
            pc.file = file;
            //pc.fields = fields;
        }
        return pc;
    }

    @Override
    public boolean execute(DefaultTaskContext context) {
        boolean bRet = false;
        List<String> data = context.rows;
        List<String> allUuids = (List<String>) args[0];
        LinkedHashMap<String, Date> mapDebts = (LinkedHashMap<String, Date>) args[1];
        if (CollectionUtils.isEmpty(allUuids)) {
            logger.info("allUuids is empty");
            return bRet;
        }

        List<String> subList = allUuids.subList(start, end);
        logger.info("pull {} {}/{} from {}", subList.size(), start, end, allUuids.size());

        for (int i = 0; i < subList.size(); i += kBatchSize) {
            int from = i;
            int to = from + kBatchSize;
            if (to > subList.size()) {
                to = subList.size();
            }
            List<String> tmpList = subList.subList(from, to);
            String uuid = StringUtils.collectionToCommaDelimitedString(tmpList);
            for (int shard = 0; shard < Runtime.kShardMax; shard++) {
                long startId = 0;
                while (startId >= 0) {
                    RepayInfoList result = TradeInnerApi.getRepayList(uuid, startId, shard);
                    if(result == null || result.infoList == null || result.infoList.size() < 0) {
                        break;
                    } else {
                        startId = result.nextID;
                        for (RepayInfo repayInfo : result.infoList) {
                            Date endTime = mapDebts.get(repayInfo.debtId);
                            Date updateTime = repayInfo.createTime;
                            // 逾期1天, 且还款流水在借款到期日之前, 忽略
                            if(/*overdueDays == 1 && */endTime != null && updateTime != null
                                    && endTime.after(updateTime)) {
                                long overdueDays = Api.diffDays(endTime, updateTime);
                                logger.debug("uuid={}, 逾期{}天, 且还款流水{}在借款到期日{}之前, 忽略", repayInfo.debtId, overdueDays, updateTime, endTime);
                                continue;
                            } else {
                                String line = repayInfo.toLine();
                                data.add(line);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
