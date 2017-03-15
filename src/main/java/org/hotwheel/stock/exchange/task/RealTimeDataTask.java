package org.hotwheel.stock.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.stock.StockOptions;
import org.hotwheel.stock.dao.IStockMonitor;
import org.hotwheel.stock.dao.IStockRealTime;
import org.hotwheel.stock.dao.IStockSubscribe;
import org.hotwheel.stock.model.StockMonitor;
import org.hotwheel.stock.model.StockRealTime;
import org.hotwheel.stock.model.StockSubscribe;
import org.hotwheel.stock.util.StockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IStockMonitor stockMonitor;

    @Autowired
    private IStockSubscribe stockSubscribe;

    @Autowired
    private IStockRealTime stockRealTime;

    @Override
    protected void service() {
        String code = "sz000088";
        while (true) {
            List<StockMonitor> listMonitor = stockMonitor.queryAll();
            Map<String, StockMonitor> mapMonitor = new HashMap<>();
            if (listMonitor != null) {
                for (StockMonitor sm : listMonitor) {
                    mapMonitor.put(sm.getCode(), sm);
                }
            }
            List<StockSubscribe> listSubscribe = stockSubscribe.queryAll();
            Map<String, StockSubscribe> mapSubscribe = new HashMap<>();
            if (listSubscribe != null) {
                for (StockSubscribe ss : listSubscribe) {
                    mapSubscribe.put(ss.getCode(), ss);
                }
            }

            List<StockRealTime> stockRealTimeList = StockApi.getRealTime(code);
            if (stockRealTimeList != null && stockRealTimeList.size() > 0) {
                for (StockRealTime realTime : stockRealTimeList) {
                    try {
                        String stockCode = realTime.getFullCode();
                        String stockName = realTime.getName();
                        StockRealTime old = stockRealTime.select(realTime.getFullCode());
                        if (old != null) {
                            stockRealTime.update(realTime);
                        } else {
                            stockRealTime.insert(realTime);
                        }
                        StockMonitor sm = mapMonitor.get(stockCode);
                        if (sm != null) {
                            // 买入价格
                            double tmpPrice = realTime.getBuyPrice();
                            double open = realTime.getOpen();
                            // 昨日收盘
                            double close = realTime.getClose();
                            double high = realTime.getHigh();
                            double low = realTime.getLow();

                            // 第一支撑位
                            double support1 = Api.valueOf(double.class, sm.getSupport1());
                            double support2 = Api.valueOf(double.class, sm.getSupport2());
                            double pressure1 = Api.valueOf(double.class, sm.getPressure1());
                            double pressure2 = Api.valueOf(double.class, sm.getPressure2());
                            double stop = Api.valueOf(double.class, sm.getStop());
                            double resistance = Api.valueOf(double.class, sm.getResistance());

                            String zf = String.format("%.2f", 100 * (tmpPrice - close) / close);
                            String keywords = null;
                            // 策略判断
                            if (tmpPrice > resistance) {
                                keywords = "突破阻力位";
                            } else if (tmpPrice > pressure2) {
                                keywords = "突破第二压力位";
                            } else if (tmpPrice > pressure1) {
                                keywords = "突破第一压力位";
                            } else if (tmpPrice <= support1) {
                                keywords = "跌破第一支撑位";
                            } else if (tmpPrice <= support2) {
                                keywords = "跌破第二支撑位";
                            } else if (tmpPrice <= stop) {
                                keywords = "触及止损位";
                            }
                            // 如果命中价格范围监控, 输出策略提醒的关键字
                            if (!Api.isEmpty(keywords)) {
                                logger.info("{}({}) {}, 现价{}, 涨跌幅{}%.", stockName, stockCode, keywords, tmpPrice, zf);
                            }
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
