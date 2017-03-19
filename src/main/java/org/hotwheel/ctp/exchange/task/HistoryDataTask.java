package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.core.io.DefaultResourceLoader;
import org.hotwheel.core.io.Resource;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockHistory;
import org.hotwheel.ctp.dao.IStockMonitor;
import org.hotwheel.ctp.index.EMAIndex;
import org.hotwheel.ctp.model.EMA;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.model.StockMonitor;
import org.hotwheel.ctp.util.ExcelApi;
import org.hotwheel.ctp.util.StockApi;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

        ExcelApi excelApi = new ExcelApi();
        String filename = "classpath:/stock/china-stock-list.xlsx";
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(filename);
        ExcelApi api = new ExcelApi();
        try {
            String filepath = resource.getFile().getAbsolutePath();
            api.read(filepath);
        } catch (IOException e) {
            logger.error("read file failed: ", e);
        }

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
