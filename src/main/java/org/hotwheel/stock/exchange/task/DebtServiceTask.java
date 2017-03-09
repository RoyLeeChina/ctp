package org.hotwheel.stock.exchange.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.hotwheel.stock.exchange.bean.InnerApiResult;
import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.http.DebtApi;
import org.hotwheel.stock.exchange.http.InnerApi;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.stock.exchange.model.DefaultTaskContext;
import org.hotwheel.stock.exchange.model.ExchangeContext;
import org.hotwheel.stock.exchange.task.data.*;
import org.hotwheel.stock.exchange.util.HttpApi;
import org.hotwheel.asio.ScoreBoard;
import org.hotwheel.scheduling.ThreadPool;
import org.hotwheel.spring.helper.SFTPHelper;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.util.StringUtils;
import org.mymmsc.api.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 业务数据任务
 *
 * Created by wangfeng on 2017/1/7.
 * @since 1.0.0
 */
@Service("OverdueServiceTask")
public class DebtServiceTask extends SchedulerContext {
    private static final Logger logger = LoggerFactory.getLogger(DebtServiceTask.class);

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
    private Set<String> allDebtIds = new HashSet<String>();
    // 债权人ID集合
    private Set<String> allCreditors = new HashSet<String>();
    // 债务人ID集合
    private Set<String> allDebtors = new HashSet<>();
    //private Vector<String> allOrders = new Vector<String>();

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

    /**
     * 全部的productId(去重)
     * @return
     */
    private List<String> getAllBidUuids() {
        // TODO 缺少异常崩溃的校验处理机制
        while (true) {
            String memberId = TradeInnerApi.popDebtor();
            if(Api.isEmpty(memberId)) {
                break;
            } else {
                //logger.error("3-debtor={}", memberId);
                allDebtors.add(memberId);
            }
        }

        // productId List
        logger.info("notice: checkout bids, begin");
        long tm = System.currentTimeMillis();
        List<String> tmpDebtors = Lists.newArrayList(allDebtors);
        ThreadPool tpDebotId = new ThreadPool(Runtime.threadNum);
        BidUuidsTask bidUuidsTask = new BidUuidsTask(Runtime.kShardMax, 1, 1, "BidUuidsTask");
        bidUuidsTask.init(0, Runtime.kShardMax, TradeInnerApi.kShardList, tmpDebtors);
        //执行一个任务
        Future<DefaultTaskContext> futureContext = tpDebotId.submit(bidUuidsTask);
        Api.nanoSleep(1);
        if(futureContext != null) {
            DefaultTaskContext ret = null;
            try {
                ret = futureContext.get();
            } catch (Exception e) {
                logger.error("FJP get failed.", e);
            }

            if(ret != null && ret.rows != null) {
                for (String productId : ret.rows) {
                    if (!Api.isEmpty(productId)) {
                        bidList.add(productId);
                    }
                }
                logger.info("{}\t==> {} rows.", ret.taskName, ret.rows.size());
            }
        }
        tpDebotId.close();
        logger.info("notice: bid {} rows", bidList.size());
        logger.info("notice: checkout bids, end");
        long ums = (System.currentTimeMillis() - tm);
        long speed = InnerApi.speed(bidList.size(), ums);
        logger.info("Requests per second: " + speed + " [#/sec] (mean)");
        InnerApi.notify("产品列表,Requests per second: " + speed + " [#/sec] (mean)");
        TradeInnerApi.finishedBidInfo();
        return Lists.newArrayList(bidList);
    }

    protected void service() {
        //DebtApi.init();
        init();
        InnerApi.notify("业务数据，开始执行。");
        while (!TradeInnerApi.getStatusDebtors()) {
            Api.sleep(1);
            continue;
        }
        // 债务人所有标的
        List<String> allBidUuids = null;
        allBidUuids = getAllBidUuids();
        logger.info("notice: productId number={}", allBidUuids.size());
        // 标的
        isTaskException = false;
        try {
            //genBidsInfo(allBidUuids);
            genBidsInfoByFjp(allBidUuids);
            //BaseContext.createCVSFile(Runtime.reportPath, "all.ok");
        } catch (Exception e) {
            InnerApi.notify("业务批量处理异常, " + e.getMessage());
            isTaskException = true;
            logger.error("occur exception: {} " , e);
        } finally {
            //
        }

        // 开始上传文件
        if(Runtime.hasUpload && !isTaskException) {
            logger.info("jdb-trade check friends upload...");
            String curDateStr = Api.toString(new Date(), "yyyyMMdd");
            //SFTP 上传开始
            try {
                String fileList = "overdueBidsInfo.csv,overdueCreditorOrders.csv";
                fileList += ",overdueDebts.csv,overdueRepayFlow.csv";
                fileList += ",RRC_PAY.in,RRC_PAY.out";
                fileList += ",overdueHacked.csv";
                fileList += ",overdueEntryUuids.csv,creditorInfos.csv";
                fileList += ",all.ok";
                String uploadPath = Runtime.reportPath.concat(File.separator).concat(curDateStr);
                logger.info("{} upload path：{}", taskName, uploadPath);
                SFTPHelper sftpHelper = new SFTPHelper(Runtime.ftpIp, Runtime.ftpPort, Runtime.ftpUserName, Runtime.ftpPassWord, Runtime.remotePath);
                sftpHelper.upload(new File(uploadPath),
                        fileList);
                sftpHelper.disconnect();
                // 业务文件上传完成, 置状态
                //hasServiceUploaded = true;
            } catch (Exception e) {
                InnerApi.notify("文件上传异常");
                logger.error("文件上传异常：", e);
                isTaskException = true;
                return;
            }
            for (int i = 0; i < 5 && !isTaskException; i++) {
                Map<String, Object> retMap = null;
                try {
                    @SuppressWarnings("rawtypes")
                    Map params = ImmutableMap.of("txDate", curDateStr, "accessKey", "04400053-e6b0-480d-bd5c-c8bc63cffc4b");
                    InnerApiResult result = HttpApi.request(Runtime.reportCallBack, null, params, InnerApiResult.class);
                    if (result == null || result.error == null || result.error.returnCode != 0) {
                        logger.info("回调通知失败！");
                        InnerApi.notify("回调采集调度接口失败！");
                    } else {
                        logger.info("回调通知成功！");
                    }
                } catch (Exception e) {
                    logger.error("异常：", e);
                    try {
                        logger.info("10分钟后将 retry!!");
                        Thread.sleep(10 * 60 * 1000);
                        continue;
                    } catch (InterruptedException e1) {
                        //
                    }
                }
                break;
            }
            InnerApi.notify("业务批量完成。");
        } else if(!Runtime.hasUpload) {
            InnerApi.notify("业务批量完成。");
        } else if(isTaskException){
            InnerApi.notify("业务批量失败。");
        }
    }

    /**
     * 拉取债务数据
     * @param allBidUuids 去重后的标的id列表
     */
    private void genBidsInfoByFjp(final List<String> allBidUuids) {
        // 标的
        final int batchOnceMax = Runtime.threadNum * Runtime.threshold;
        // 协议
        long dtm = System.currentTimeMillis();
        int taskTotal = allBidUuids.size();

        // 标的按照分页尺寸重新组合标的id列表
        List<String> allBids = new ArrayList<String>();
        for (int i = 0; i < taskTotal; i += Runtime.batchSize) {
            int from = i;
            int to = from + Runtime.batchSize;
            if (to > taskTotal) {
                to = taskTotal;
            }
            List<String> tmpList = allBidUuids.subList(from, to);
            String tmpUuids = StringUtils.collectionToCommaDelimitedString(tmpList);
            allBids.add(tmpUuids);
        }
        // 标的总数
        long numberOfBid = 0;
        // 订单总数
        long numberOfOrder = 0;
        // 还款流水总数
        long numberOfRepay = 0;

        taskTotal = allBids.size();
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
            DebtTask task = new DebtTask();
            task.init(iStart, iEnd, allBids/*, creditors, allDebtIds, numberOfValidated*/);
            //执行一个任务
            Future<ExchangeContext> future = threadPool.submit(task);
            Api.nanoSleep(1);
            if(future != null) {
                try {
                    ExchangeContext<String> context = future.get();
                    if (context != null) {
                        numberOfBid += context.numberOfBid;
                        numberOfOrder += context.numberOfOrder;
                        if (context.creditors != null && context.creditors.size() > 0) {
                            allCreditors.addAll(context.creditors);
                        }
                        if (context.debtIds != null && context.debtIds.size() > 0) {
                            allDebtIds.addAll(context.debtIds);
                        }
                    }
                } catch (InterruptedException e) {
                    logger.error("execute [{}] interrupted:", task.getTaskName(), e);
                } catch (ExecutionException e) {
                    logger.error("execute [{}] failed:", task.getTaskName(), e);
                }
            }
            threadPool.close();
            logger.info("运行 计时点{}", System.currentTimeMillis() - tm);
        }
        logger.info("notice: bid {} rows", numberOfBid);
        logger.info("notice: order {} rows", numberOfOrder);
        long ums = System.currentTimeMillis() - dtm;
        long speed = InnerApi.speed(allBidUuids.size(), ums);
        logger.info("Requests per second: " + speed + " [#/sec] (mean)");
        InnerApi.notify("协议、标的、订单批量文件，Requests per second: " + speed + " [#/sec] (mean)");
        DebtApi.verifyBid(numberOfBid);
        DebtApi.verifyOrder(numberOfOrder);
        // 设置redis推送债务数据完成状态 [wangfeng on 2017/2/23 14:28]
        DebtApi.pushDebtFinished();

        // 还款流水
        dtm = System.currentTimeMillis();
        List<String> tmpList = Lists.newArrayList(allDebtIds);
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
            RepayflowTask task = new RepayflowTask(Runtime.threadNum, Runtime.threshold, Runtime.batchSize, "RepayflowTask");
            task.init(iStart, iEnd, tmpList);
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
        speed = InnerApi.speed(tmpList.size(), ums);
        logger.info("Requests per second: " + speed + " [#/sec] (mean)");
        InnerApi.notify("还款流水批量文件，Requests per second: " + speed + " [#/sec] (mean), 业务-债务数据，完成。");
        DebtApi.verifyRepay(numberOfRepay);
        //logger.info("notice: 还款流水运行 计时点{}", System.currentTimeMillis() - tm);
        // RRC_PAY.in -> RRC_PAY.out
        Set<String> setPayList = new HashSet<>();
        setPayList.addAll(allDebtors);
        setPayList.addAll(allCreditors);
        List<String> allMembers = Lists.newArrayList(setPayList);
        taskTotal = allMembers.size();
        for (int i = 0; i < taskTotal; i += batchOnceMax) {
            logger.info("data rrc-pay-infos ----------------");
            long tm = System.currentTimeMillis();
            int iStart = i;
            int iEnd = iStart + batchOnceMax;

            if(iEnd >= taskTotal) {
                iEnd = taskTotal;
            }
            logger.info("rrc-pay-infos {} rows from {}->{}", allMembers.size(), iStart , iEnd);
            ThreadPool threadPool = new ThreadPool(Runtime.threadNum);
            UserMappingTask task = new UserMappingTask(Runtime.threadNum, Runtime.threshold, Runtime.batchSize, "UserMappingTask");
            task.init(iStart, iEnd, allMembers);
            //执行一个任务
            Future<DefaultTaskContext> future = threadPool.submit(task);
            Api.nanoSleep(1);
            if(future != null) {
                writeToFile(future);
            }
            threadPool.close();
            logger.info("运行 计时点{}", System.currentTimeMillis() - tm);
        }

        // 停催标志
        List listDebtors = Lists.newArrayList(allDebtors);
        taskTotal = listDebtors.size();
        for (int i = 0; i < taskTotal; i += batchOnceMax) {
            logger.info("data hacked-infos ----------------");
            long tm = System.currentTimeMillis();
            int iStart = i;
            int iEnd = iStart + batchOnceMax;

            if(iEnd >= taskTotal) {
                iEnd = taskTotal;
            }
            logger.info("hacked-infos {} rows from {}->{}", listDebtors.size(), iStart , iEnd);
            ThreadPool threadPool = new ThreadPool(Runtime.threadNum);
            HackedTask task = new HackedTask(Runtime.threadNum, Runtime.threshold, Runtime.batchSize, "HackedTask");
            task.init(iStart, iEnd, listDebtors);
            //执行一个任务
            Future<DefaultTaskContext> future = threadPool.submit(task);
            Api.nanoSleep(1);
            if(future != null) {
                writeToFile(future);
            }
            threadPool.close();
            logger.info("运行 计时点{}", System.currentTimeMillis() - tm);
        }

        // 债务人信息
        taskTotal = listDebtors.size();
        for (int i = 0; i < taskTotal; i += batchOnceMax) {
            logger.info("data debtor-infos ----------------");
            long tm = System.currentTimeMillis();
            int iStart = i;
            int iEnd = iStart + batchOnceMax;

            if(iEnd >= taskTotal) {
                iEnd = taskTotal;
            }
            logger.info("debtor-infos {} rows from {}->{}", listDebtors.size(), iStart , iEnd);
            ThreadPool threadPool = new ThreadPool(Runtime.threadNum);
            DebtorInfoTask task = new DebtorInfoTask(Runtime.threadNum, Runtime.threshold, Runtime.batchSize, "DebtorInfoTask");
            task.init(iStart, iEnd, listDebtors);
            //执行一个任务
            Future<DefaultTaskContext> future = threadPool.submit(task);
            Api.nanoSleep(1);
            if(future != null) {
                writeToFile(future);
            }
            threadPool.close();
            logger.info("运行 计时点{}", System.currentTimeMillis() - tm);
        }

        // 债权人信息
        List<String> listCreditors = Lists.newArrayList(allCreditors);
        taskTotal = listCreditors.size();
        for (int i = 0; i < taskTotal; i += batchOnceMax) {
            logger.info("data debtor-infos ----------------");
            long tm = System.currentTimeMillis();
            int iStart = i;
            int iEnd = iStart + batchOnceMax;

            if(iEnd >= taskTotal) {
                iEnd = taskTotal;
            }
            logger.info("creditor-infos {} rows from {}->{}", listCreditors.size(), iStart , iEnd);
            ThreadPool threadPool = new ThreadPool(Runtime.threadNum);
            CreditorInfoTask task = new CreditorInfoTask(Runtime.threadNum, Runtime.threshold, Runtime.batchSize, "CreditorInfoTask");
            task.init(iStart, iEnd, listCreditors);
            //执行一个任务
            Future<DefaultTaskContext> future = threadPool.submit(task);
            Api.nanoSleep(1);
            if(future != null) {
                writeToFile(future);
            }
            threadPool.close();
            logger.info("运行 计时点{}", System.currentTimeMillis() - tm);
        }
    }
}
