package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockCode;
import org.hotwheel.ctp.dao.IStockHistory;
import org.hotwheel.ctp.dao.IStockMonitor;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.util.StockApi;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 历史数据
 * Created by wangfeng on 2017/3/10.
 *
 * @version 1.0.1
 */
@Service("historyDataTask")
public class HistoryDataTask extends SchedulerContext {
    private static Logger logger = LoggerFactory.getLogger(HistoryDataTask.class);

    @Autowired
    private IStockMonitor stockMonitor;

    @Autowired
    private IStockHistory stockHistory;

    @Autowired
    private IStockCode stockCode;

    @Override
    protected void service() {

        while (true) {
            if (isTimeExpire()) {
                logger.info("运行时间{}->{}到, 任务退出", taskStartTime, taskEndTime);
                break;
            }
            // 捡出全部股票的策略
            List<String> allCodes = stockCode.getAll();
            for (String code : allCodes) {
                logger.info("code={}", code);
                Date lastDay = stockHistory.getLastDate(code);
                long dataLen = Api.valueOf(long.class, StockOptions.DEFAULT_DATALEN);
                if (!Api.isEmpty(lastDay)) {
                    long diffDays = Api.diffDays(lastDay, new Date());
                    if (diffDays == 0) {
                        logger.info("code={}的历史数据已经到达今日{}", code, Api.toString(lastDay, StockOptions.DateFormat));
                        continue;
                    } else {
                        dataLen = diffDays;
                    }
                }
                // 一周内的数据, 逐条处理
                if (dataLen < 7) {
                    List<StockHistory> shList = StockApi.getHistory(code, dataLen);
                    if (shList != null && shList.size() > 0) {
                        for (StockHistory history : shList) {
                            String day = Api.toString(history.getDay(), StockOptions.DateFormat);
                            try {
                                StockHistory old = stockHistory.select(code, day);
                                if (old != null) {
                                    stockHistory.update(history);
                                } else {
                                    stockHistory.insert(history);
                                }
                            } catch (Exception e) {
                                logger.error("", e);
                            }
                        }
                    }
                } else {
                    int ret = stockHistory.deleteOne(code);
                    logger.debug("delete {}, {}", code, ret);
                    List<StockHistory> shList = StockApi.getHistory(code);
                    if (shList != null && shList.size() > 0) {
                        try {
                            ret = stockHistory.insertBatch(shList);
                            logger.debug("insertBatch {}, {}", code, ret);
                        } catch (Exception e) {
                            logger.error("", e);
                        }
                    }
                }
                Api.sleep(1000);
            }
            break;
        }
    }
}
