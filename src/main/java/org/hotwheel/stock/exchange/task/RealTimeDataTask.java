package org.hotwheel.stock.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.stock.StockOptions;
import org.hotwheel.stock.dao.IStockRealTime;
import org.hotwheel.stock.model.StockRealTime;
import org.hotwheel.stock.util.StockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实时数据
 *
 * Created by wangfeng on 2017/3/14.
 * @version 1.0.0
 */
@Service("realTimeDataTask")
public class RealTimeDataTask extends SchedulerContext {
    private static Logger logger = LoggerFactory.getLogger(RealTimeDataTask.class);

    @Autowired
    private IStockRealTime stockRealTime;

    @Override
    protected void service() {
        String code = "sz000088";
        while (true) {
            List<StockRealTime> stockRealTimeList = StockApi.getRealTime(code);
            if (stockRealTimeList != null && stockRealTimeList.size() > 0) {
                for (StockRealTime realTime : stockRealTimeList) {
                    try {
                        StockRealTime old = stockRealTime.select(realTime.getFullCode());
                        if (old != null) {
                            stockRealTime.update(realTime);
                        } else {
                            stockRealTime.insert(realTime);
                        }
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
            Api.sleep(StockOptions.kRealTimenterval);
        }
    }
}
