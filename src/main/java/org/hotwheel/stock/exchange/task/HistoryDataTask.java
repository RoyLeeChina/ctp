package org.hotwheel.stock.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.stock.StockOptions;
import org.hotwheel.stock.dao.IStockRealTime;
import org.hotwheel.stock.model.StockHistory;
import org.hotwheel.stock.util.StockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 历史数据
 * Created by wangfeng on 2017/3/10.
 */
@Service("historyDataTask")
public class HistoryDataTask extends SchedulerContext {
    private static Logger logger = LoggerFactory.getLogger(HistoryDataTask.class);

    @Autowired
    private IStockRealTime stockRealTime;

    @Override
    protected void service() {
        String code = "sz000088";
        while (true) {
            List<StockHistory> shList = StockApi.getHistory(code);
            if (shList != null && shList.size() > 0) {
                for (StockHistory history : shList) {
                    try {
                        //
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
            Api.sleep(StockOptions.kRealTimenterval);
        }
    }
}
