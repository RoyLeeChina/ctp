package org.hotwheel.stock.exchange.controller.inner;

import org.hotwheel.stock.exchange.bean.InnerApiResult;
import org.hotwheel.stock.exchange.http.DebtApi;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.stock.exchange.task.CollectionTask;
import org.hotwheel.stock.exchange.task.DebtServiceTask;
import org.mymmsc.api.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 债务数据控制器
 *
 * Created by wangfeng on 2017/1/10.
 */
@Controller
@RequestMapping("/innerApi")
public class DebtController {
    private static final Logger logger = LoggerFactory.getLogger(DebtController.class);
    private final static int kErrorCode = 10000;

    @Autowired
    private DebtServiceTask debtService;

    @Autowired
    private CollectionTask collectionTask;

    private static Thread taskThread = null;
    private static TransferThread transferThread = null;
    private static Boolean threadRunning = Boolean.valueOf(false);
    private static boolean onlyService = false;
    private static String threadName = "DebtServiceTask";

    private void doProcessDebt() {
        if (!getRunning()) {
            transferThread = new TransferThread();
            //threadName = transferThread.getClass().getSimpleName();
            taskThread = new Thread(transferThread, threadName);
            // 此次将User线程变为Daemon线程
            taskThread.setDaemon(true);
            taskThread.start();
            //setPhrase(TransferInfo.Phrase_Notifying_Core);
            logger.info("{} start", threadName);
        }
    }

    @ResponseBody
    @RequestMapping("/resetAndStart")
    public InnerApiResult resetAndStart() {
        InnerApiResult result = new InnerApiResult();
        onlyService = false;
        setRunning(false);
        DebtApi.init();
        doProcessDebt();
        result.setSuccess();
        return result;
    }

    /**
     * 获取债务数据
     * @param idList
     * @return
     */
    @ResponseBody
    @RequestMapping("/getDebt.cgi")
    public InnerApiResult resetDebtTask(String idList) {
        int errno = kErrorCode + 100;
        String message = "操作失败";
        InnerApiResult result = new InnerApiResult();
        String[] uuids;
        if (Api.isEmpty(idList)) {
            errno += 1;
            message = "参数[idList], 不能为空";
        } else if (getRunning()) {
            errno += 2;
            message = "上一次任务未完成, 稍候再试";
        } else {
            DebtApi.init();
            setRunning(false);
            uuids = idList.split(",");
            DebtApi.push(uuids);
            onlyService = true;
            doProcessDebt();
            errno = 0;
            message = "SUCCESS";
        }
        result.set(errno, message);
        return result;
    }

    private void setRunning(boolean status) {
        synchronized (threadName) {
            threadRunning = status;
        }
        if (!status && taskThread != null) {
            logger.info("强制终止线程[{}], start", threadName);
            try {
                taskThread.interrupt();
            } catch (Exception e) {
                //
            } finally {
                //
            }
            logger.info("强制终止线程[{}], stop", threadName);
        }
    }

    private boolean getRunning() {
        boolean bRet = true;
        synchronized (threadName) {
            bRet = threadRunning.booleanValue();
        }
        return bRet;
    }

    public class TransferThread implements Runnable {

        @Override
        public void run() {
            try {
                setRunning(true);
                if (!onlyService) {
                    collectionTask.doTask();
                } else {
                    TradeInnerApi.finishedDebtors();
                }
                debtService.doTask();
            } catch (Exception e) {
                logger.error(threadName + " failed: ", e);
            } finally {
                logger.info("{} stop", threadName);
                onlyService = false;
                setRunning(false);
            }
        }
    }
}
