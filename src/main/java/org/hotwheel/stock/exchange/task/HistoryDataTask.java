package org.hotwheel.stock.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.stock.StockOptions;
import org.hotwheel.stock.dao.IStockHistory;
import org.hotwheel.stock.dao.IStockMonitor;
import org.hotwheel.stock.model.StockHistory;
import org.hotwheel.stock.model.StockMonitor;
import org.hotwheel.stock.util.StockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Override
    protected void service() {
        while (true) {
            if (isTimeExpire()) {
                logger.info("运行时间{}->{}到, 任务退出", taskStartTime, taskEndTime);
                break;
            }
            // 捡出全部股票的策略
            List<StockMonitor> listMonitor = stockMonitor.queryAll();
            Map<String, StockMonitor> mapMonitor = new HashMap<>();
            Set<String> allCodes = new HashSet<>();
            if (listMonitor != null) {
                for (StockMonitor sm : listMonitor) {
                    String code = sm.getCode();
                    allCodes.add(code);
                    mapMonitor.put(code, sm);
                }
            }

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
                }
            }
            Api.sleep(StockOptions.kRealTimenterval);
        }
    }
}
