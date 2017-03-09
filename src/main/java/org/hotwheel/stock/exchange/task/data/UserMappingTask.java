package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.stock.exchange.model.DefaultTaskContext;
import org.hotwheel.stock.exchange.context.trade.UserMapping;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * 借贷宝ID和支付ID映射任务
 *
 * Created by wangfeng on 2016/11/21.
 * @since 2.1.0
 */
public class UserMappingTask extends PartitionTask<DefaultTaskContext> {
    private static File file = null;
    private static String filename = "RRC_PAY.in";
    private List<String> allDebtors = null;

    public UserMappingTask(int threadNum, int threshold, int batchSize, String taskName) {
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
        String sql = null;
        String sRet = null;
        int total = taskList.size();
        for (int i = 0; i < total; i++) {
            String memberId = taskList.get(i);
            UserMapping userMapping = TradeInnerApi.getUserMaping(memberId);
            if (userMapping != null) {

                data.add(userMapping.toLine());
            }
            /*
            String idCode = TradeInnerApi.getIdentity(memberId);
            if (Api.isEmpty(idCode)) {
                idCode = "";
            }
            UserMapping userMapping = new UserMapping();
            userMapping.memberId = memberId;
            userMapping.idCode = idCode;
            data.add(userMapping.toLine());
            */
        }
        return true;
    }
}
