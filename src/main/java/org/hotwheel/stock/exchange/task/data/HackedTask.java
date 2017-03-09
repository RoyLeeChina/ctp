package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.stock.exchange.context.trade.HackedInfo;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.stock.exchange.model.DefaultTaskContext;
import org.hotwheel.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * 停催/冒用任务
 *
 * Created by wangfeng on 2016/11/21.
 * @since 2.1.0
 */
public class HackedTask extends PartitionTask<DefaultTaskContext> {
    private static File file = null;
    private static String filename = "overdueHacked.csv";
    private List<String> allDebtors = null;

    public HackedTask(int threadNum, int threshold, int batchSize, String taskName) {
        super(threadNum, threshold, batchSize, taskName);
    }

    @Override
    public DefaultTaskContext getContext() {
        DefaultTaskContext pc = new DefaultTaskContext();
        synchronized (taskName) {
            if (file == null || isNewFile(file, reportPath, filename)) {
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
        for (int i = 0; i < total; i++) {
            HackedInfo hackedInfo = TradeInnerApi.getHackedInfo(taskList.get(i));
            if (hackedInfo != null) {
                data.add(hackedInfo.toLine());
            }
        }
        return true;
    }
}