package org.hotwheel.ctp.exchange.task;

import com.google.common.collect.Lists;
import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockMonitor;
import org.hotwheel.ctp.dao.IStockRealTime;
import org.hotwheel.ctp.dao.IStockSubscribe;
import org.hotwheel.ctp.dao.IStockUser;
import org.hotwheel.ctp.model.*;
import org.hotwheel.ctp.util.DateUtils;
import org.hotwheel.ctp.util.EmailApi;
import org.hotwheel.ctp.util.PolicyApi;
import org.hotwheel.ctp.util.StockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 实时数据
 *
 * Created by wangfeng on 2017/3/14.
 * @version 1.0.0
 */
@Service("realTimeDataTask")
public class RealTimeDataTask extends CTPContext {
    private static Logger logger = LoggerFactory.getLogger(RealTimeDataTask.class);

    @Autowired
    private IStockUser stockUser;

    @Autowired
    private IStockMonitor stockMonitor;

    @Autowired
    private IStockSubscribe stockSubscribe;

    @Autowired
    private IStockRealTime stockRealTime;

    @Override
    protected void service() {
        while (true) {
            if (isTimeExpire()) {
                logger.info("运行时间{}->{}到, 任务退出", taskStartTime, taskEndTime);
                break;
            }
            if (weChat == null || !weChat.isRunning()) {
                Api.sleep(StockOptions.kRealTimenterval);
                continue;
            }
            // 时间范围内
            if (isTimerCycle()) {
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
                if (allCodes == null || allCodes.size() < 1) {
                    //
                } else {
                    List<StockRealTime> stockRealTimeList = StockApi.getRealTime(Lists.newArrayList(allCodes));
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
                                if (realTime.getNow() < 0.01) {
                                    continue;
                                }
                                StockMonitor sm = mapMonitor.get(stockCode);
                                if (sm != null) {
                                    // 买入价格
                                    double tmpPrice = realTime.getNow();
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
                                    String field = null;
                                    // 策略判断
                                    if (tmpPrice >= resistance) {
                                        field = "resistance";
                                        keywords = "突破阻力位" + resistance;
                                    } else if (tmpPrice >= pressure2) {
                                        field = "pressure2";
                                        keywords = "突破第二压力位" + pressure2;
                                    } else if (tmpPrice >= pressure1) {
                                        field = "pressure1";
                                        keywords = "突破第一压力位" + pressure1;
                                    } else if (tmpPrice <= support1) {
                                        field = "support1";
                                        keywords = "跌破第一支撑位" + support1;
                                    } else if (tmpPrice <= support2) {
                                        field = "support2";
                                        keywords = "跌破第二支撑位" + support2;
                                    } else if (tmpPrice <= stop) {
                                        field = "stop";
                                        keywords = "触及止损位" + stop;
                                    }
                                    // 如果命中价格范围监控, 输出策略提醒的关键字
                                    if (!Api.isEmpty(keywords)) {
                                        List<StockSubscribe> tmpSubscribe = stockSubscribe.queryByCode(stockCode);
                                        //logger.info("{}({}) {}, 现价{}, 涨跌幅{}%.", stockName, stockCode, keywords, tmpPrice, zf);
                                        if (tmpSubscribe == null) {
                                            logger.info("{} 暂无用户订阅");
                                        } else {
                                            for (StockSubscribe userSubscribe : tmpSubscribe) {
                                                Policy policy = PolicyApi.get(userSubscribe.getRemark());
                                                boolean bSent = true;
                                                if (policy == null) {
                                                    bSent = false;
                                                    policy = new Policy();
                                                } else {
                                                    bSent = (boolean) Api.getValue(policy, field);
                                                }
                                                // 判断是否当天发送过
                                                if (bSent && !Api.isEmpty(userSubscribe.getSendDate())) {
                                                    Date today = DateUtils.getZero(new Date());
                                                    // 如果已经是第二天了
                                                    if (today.after(userSubscribe.getSendDate())) {
                                                        policy = new Policy();
                                                        bSent = false;
                                                    }
                                                }
                                                // 如果没有发送过
                                                if (!bSent) {
                                                    UserInfo user = stockUser.select(userSubscribe.getPhone());
                                                    if (user == null) {
                                                        logger.info("not found user={}", userSubscribe.getPhone());
                                                    } else {
                                                        // 如果没有发送, 设置发送状态
                                                        Api.setValue(policy, field, true);
                                                        String title = StockOptions.kPrefixMessage + "盘中策略提醒";
                                                        String content = String.format("%s: %s(%s) ,现价%.2f %s, 涨跌幅%s%%.",
                                                                userSubscribe.getPhone(), stockName, stockCode, tmpPrice, keywords, zf);
                                                        logger.info(content);
                                                        if (!Api.isEmpty(user.getWeixin())) {
                                                            weChat.sendMessage(user.getWeixin(), title + ": " + content);
                                                        } else if (!Api.isEmpty(user.getEmail())) {
                                                            if (EmailApi.send(user.getEmail(), title, content)) {
                                                                userSubscribe.setRemark(policy.toString());
                                                                userSubscribe.setSendDate(new Date());
                                                                stockSubscribe.update(userSubscribe);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
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
}
