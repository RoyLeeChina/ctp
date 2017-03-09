package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.stock.exchange.context.trade.UserInfo;
import org.hotwheel.stock.exchange.http.DebtApi;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.stock.exchange.model.DefaultTaskContext;
import org.hotwheel.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * 债务人信息
 *
 * Created by wangfeng on 2016/11/20.
 * @since 2.1.0
 */
public class DebtorInfoTask extends PartitionTask<DefaultTaskContext> {
    private static File file = null;
    private static String filename = "overdueEntryUuids.csv";
    private List<String> allDebtors = null;

    public DebtorInfoTask(int threadNum, int threshold, int batchSize, String taskName) {
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
        allDebtors = (List<String>) args[0];
        if (CollectionUtils.isEmpty(allDebtors)) {
            logger.info("allDebtors is empty");
            return bRet;
        }
        List<String> taskList = allDebtors.subList(start, end);
        logger.info("pull {}->{}/{} from {}", taskList.size(), start, end, allDebtors.size());
        int size = taskList.size();
        //int counter = size % kBatchSize == 0 ? size / kBatchSize : size / kBatchSize + 1;
        int total = taskList.size();
        for (int i = 0; i < total; i += batchSize) {
            int from = i;
            int to = from + batchSize;
            if(to >= total) {
                to = total;
            }
            List<String> tmpList = taskList.subList(from, to);

            UserInfo[] users = TradeInnerApi.getUserList(tmpList);
            if(users != null) {
                for (UserInfo userInfo : users) {
                    //data.add(userInfo.toLine());
                    DebtApi.submitDebtor(userInfo);
                }
            }
        }
        return true;
    }
}
