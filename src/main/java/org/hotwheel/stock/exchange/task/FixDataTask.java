package org.hotwheel.stock.exchange.task;

import com.google.common.collect.Lists;
import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.dao.stock.IOverdueErrorDao;
import org.hotwheel.stock.exchange.http.InnerApi;
import org.hotwheel.stock.exchange.model.DefaultTaskContext;
import org.hotwheel.stock.exchange.task.data.DebtFixTask;
import org.hotwheel.stock.exchange.task.data.RepayflowFixTask;
import org.hotwheel.asio.ScoreBoard;
import org.hotwheel.scheduling.ThreadPool;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.util.StringUtils;
import org.mymmsc.api.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.Future;

/**
 * 修复数据任务
 *
 * Created by wangfeng on 2017/1/13.
 */
@Service("fixDataTask")
public class FixDataTask extends SchedulerContext {
    private static final Logger logger = LoggerFactory.getLogger(FixDataTask.class);

    @Autowired
    private IOverdueErrorDao ermasError;
    private File productFile = null;
    private File orderFile = null;
    private File repayFile = null;
    private File userMappingFile = null;
    private File hackedFile = null;
    private File debtorFile = null;
    private File creditorFile = null;

    // 产品ID集合
    private Set<String> bidList = new HashSet<String>();
    // 债务ID集合
    private LinkedHashMap<String, Date> mapDebts = new LinkedHashMap<>();
    // 债权人ID集合
    private Set<String> allCreditors = new HashSet<String>();

    private List<String> allDebtors = new ArrayList<>();
    private Vector<String> allOrders = new Vector<String>();

    private ScoreBoard scoreBoard = new ScoreBoard();

    private void init() {
        productFile = null;
        orderFile = null;
        repayFile = null;
        userMappingFile = null;
        hackedFile = null;
        debtorFile = null;
        creditorFile = null;
    }

    protected void service() {
        InnerApi.notify("补录标的还款明细，开始执行。");
        try {
            logger.info("notice: checkout all bid-uuids");
            List<String> bidList = ermasError.getClearBidUuids();
            Set<String> bidUuids = new HashSet<>();
            if (bidList != null) {
                for (String bidUuid : bidList) {
                    if (!Api.isEmpty(bidUuid)) {
                        bidUuids.add(bidUuid);
                    }
                }
            }
            List<String> allBidUuid = Lists.newArrayList(bidUuids);
            logger.info("notice: checkout all bid-uuids is {} rows", allBidUuid.size());

            // 标的
            final int batchOnceMax = Runtime.threadNum * Runtime.threshold;
            // 协议
            //BaseContext.createCVSFile(Runtime.reportPath, "overdueAgreements.csv");
            // 协议
            long dtm = System.currentTimeMillis();
            int taskTotal = allBidUuid.size();
            List<String> listProducts = new ArrayList<String>();
            for (int i = 0; i < taskTotal; i += Runtime.batchSize) {
                int from = i;
                int to = from + Runtime.batchSize;
                if (to > taskTotal) {
                    to = taskTotal;
                }
                List<String> tmpList = allBidUuid.subList(from, to);
                String tmpUuids = StringUtils.collectionToCommaDelimitedString(tmpList);
                listProducts.add(tmpUuids);
            }
            taskTotal = listProducts.size();
            long[] numberOfValidated = new long[2];
            Long numberOfRepay = Long.valueOf(0);
            for (int i = 0; i < taskTotal; i += batchOnceMax) {
                logger.info("data agreements ----------------");
                long tm = System.currentTimeMillis();
                int iStart = i;
                int iEnd = iStart + batchOnceMax;

                if(iEnd >= taskTotal) {
                    iEnd = taskTotal;
                }
                logger.info("product-uuids {} rows from {}->{}", taskTotal, iStart , iEnd);
                ThreadPool threadPool = new ThreadPool(Runtime.threadNum);
                DebtFixTask task = new DebtFixTask();
                task.init(iStart, iEnd, listProducts, allCreditors, mapDebts, numberOfValidated);
                //执行一个任务
                Future<DefaultTaskContext> future = threadPool.submit(task);
                Api.nanoSleep(1);
                if(future != null) {
                    writeToFile(future);
                }
                threadPool.close();
                logger.info("运行 计时点{}", System.currentTimeMillis() - tm);
            }
            logger.info("notice: bid {} rows", numberOfValidated[0]);
            logger.info("notice: order {} rows", numberOfValidated[1]);
            long ums = System.currentTimeMillis() - dtm;
            long speed = InnerApi.speed(allBidUuid.size(), ums);
            logger.info("Requests per second: " + speed + " [#/sec] (mean)");
            InnerApi.notify("协议、标的、订单批量文件，Requests per second: " + speed + " [#/sec] (mean)");
            //DebtApi.verifyBid(numberOfValidated[0]);
            //DebtApi.verifyOrder(numberOfValidated[1]);
            // 还款流水
            dtm = System.currentTimeMillis();
            //List<MiniDebt> tmpList = Lists.newArrayList(debtIdList);
            List<String> tmpList = Lists.newArrayList(mapDebts.keySet());
            taskTotal = tmpList.size();
            for (int i = 0; i < taskTotal; i += batchOnceMax) {
                logger.info("data repayflow ----------------");
                long tm = System.currentTimeMillis();
                int iStart = i;
                int iEnd = iStart + batchOnceMax;

                if(iEnd >= taskTotal) {
                    iEnd = taskTotal;
                }
                logger.info("product-uuids {} rows from {}->{}", taskTotal, iStart , iEnd);
                ThreadPool threadPool = new ThreadPool(Runtime.threadNum);
                RepayflowFixTask task = new RepayflowFixTask(Runtime.threadNum, Runtime.threshold, Runtime.batchSize, "RepayflowTask");
                task.init(iStart, iEnd, tmpList, mapDebts);
                //执行一个任务
                Future<DefaultTaskContext> future = threadPool.submit(task);
                Api.nanoSleep(1);
                if(future != null) {
                    numberOfRepay += writeToFile(future);
                }
                threadPool.close();
                logger.info("运行 计时点{}", System.currentTimeMillis() - tm);
            }
            logger.info("notice: repay {} rows", numberOfRepay);
            ums = System.currentTimeMillis() - dtm;
            speed = InnerApi.speed(mapDebts.size(), ums);
            logger.info("Requests per second: " + speed + " [#/sec] (mean)");
            InnerApi.notify("还款流水批量文件，Requests per second: " + speed + " [#/sec] (mean), 业务-债务数据，完成。");
        } catch (Exception e) {
            logger.error("任务失败:", e);
        }
    }
}
