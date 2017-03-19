package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockCode;
import org.hotwheel.ctp.dao.IStockHistory;
import org.hotwheel.ctp.dao.IStockMonitor;
import org.hotwheel.ctp.index.EMAIndex;
import org.hotwheel.ctp.model.EMA;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.util.StockApi;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 历史数据
 * Created by wangfeng on 2017/3/10.
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
                List<StockHistory> shList = StockApi.getHistory(code);
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

                    EMAIndex emaIndex = new EMAIndex();
                    emaIndex.compute(shList);
                    List<EMA> tmpList = emaIndex.getListEma();
                    for (EMA ema : tmpList) {
                        logger.debug("date={}, EMA{}={}, EMA{}={}", ema.getDay(), ema.getCycle1(), ema.getEma1(), ema.getCycle2(), ema.getEma2());
                    }

                }
            }
            Api.sleep(StockOptions.kRealTimenterval);
        }
    }
}
