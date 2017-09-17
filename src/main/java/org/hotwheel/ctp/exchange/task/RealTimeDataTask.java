package org.hotwheel.ctp.exchange.task;

import com.google.common.collect.Lists;
import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockMonitor;
import org.hotwheel.ctp.dao.IStockRealTime;
import org.hotwheel.ctp.dao.IStockSubscribe;
import org.hotwheel.ctp.dao.IStockUser;
import org.hotwheel.ctp.data.MoneyFlowUtils;
import org.hotwheel.ctp.model.*;
import org.hotwheel.ctp.util.DateUtils;
import org.hotwheel.ctp.util.EmailApi;
import org.hotwheel.ctp.util.PolicyApi;
import org.hotwheel.ctp.util.StockApi;
import org.hotwheel.weixin.bean.ContactInfo;
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
            if (CTPContext.isServerCloseing()) {
                logger.info("SERVER正在关闭, 线程{}->{}任务退出", taskName);
                break;
            }
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
                                    //tmpPrice = 100.00;
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
                                    Date now = new Date();
                                    String tm = Api.toString(now, StockOptions.TimeFormat);
                                    // 如果命中价格范围监控, 输出策略提醒的关键字
                                    if (!Api.isEmpty(keywords)) {
                                        List<StockSubscribe> tmpSubscribe = stockSubscribe.queryByCode(stockCode);
                                        //logger.info("{}({}) {}, 现价{}, 涨跌幅{}%.", stockName, stockCode, keywords, tmpPrice, zf);
                                        if (tmpSubscribe == null || tmpSubscribe.size() < 1) {
                                            logger.info("{} 暂无用户订阅");
                                        } else {
                                            StockMoneyFlow moneyFlow = MoneyFlowUtils.getOne(stockCode);
                                            Map<String, StringBuffer> mapGroupMessage = new HashMap<>();
                                            String title = StockOptions.kPrefixMessage + "(" + tm + ")盘中策略提醒";
                                            String content = String.format("%s(%s) ,现价%.2f %s, 涨跌幅%s%%",
                                                    stockName, stockCode, tmpPrice, keywords, zf);
                                            if (moneyFlow == null) {
                                                content += ".";
                                            } else {
                                                double r0 = moneyFlow.r0_in - moneyFlow.r0_out;
                                                double r1 = moneyFlow.r1_in - moneyFlow.r1_out;
                                                double r2 = moneyFlow.r2_in - moneyFlow.r2_out;
                                                double r3 = moneyFlow.r3_in - moneyFlow.r3_out;

                                                r0 /= 10000;
                                                r1 /= 10000;
                                                r2 /= 10000;
                                                r3 /= 10000;

                                                double vzb = (moneyFlow.r0_out + moneyFlow.r1_out);
                                                double vall = (moneyFlow.r0 + moneyFlow.r1 + moneyFlow.r2 + moneyFlow.r3);
                                                String zb = "N/A";
                                                if (vall > 0) {
                                                    zb = String.format("%.2f%%", 100 * (vzb / vall));
                                                }
                                                content += String.format(", 超大单净流入%.2f万元, 大单净流入%.2f万元, 中单净流入%.2f万元, 散单净流入%.2f万元, 主力资金流出占比%s.", r0, r1, r2, r3, zb);
                                            }
                                            String message = title + ": " + content;
                                            logger.info(content);
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
                                                        bSent = false;
                                                        String fullName = user.getWeixin();
                                                        if (!Api.isEmpty(fullName)) {
                                                            //weChat.sendMessage(user.getWeixin(), title + ": " + content);
                                                            ContactInfo contactInfo = weChat.parseContact(fullName);
                                                            if (contactInfo == null) {
                                                                logger.info("weixin={}, 好友和群信息无法识别, 不能推送", fullName);
                                                            } else if (Api.isEmpty(contactInfo.getGroupId())){
                                                                // 如果不是群消息
                                                                weChat.sendMessageByUserId(contactInfo.getToUserName(), message);
                                                            } else {
                                                                // 非好友, 发信息到群里
                                                                String groupId = contactInfo.getGroupId();
                                                                StringBuffer sb = mapGroupMessage.get(groupId);
                                                                if (sb == null) {
                                                                    sb = new StringBuffer();
                                                                }
                                                                sb.append("@" + contactInfo.getNickName() + " ");
                                                                mapGroupMessage.put(groupId, sb);
                                                            }
                                                            bSent = true;
                                                        } else if (!Api.isEmpty(user.getEmail())) {
                                                            if (EmailApi.send(user.getEmail(), title, content)) {
                                                                bSent = true;
                                                            }
                                                        }
                                                        if (bSent) {
                                                            userSubscribe.setRemark(policy.toString());
                                                            userSubscribe.setSendDate(new Date());
                                                            stockSubscribe.update(userSubscribe);
                                                        }
                                                    }
                                                }
                                            }
                                            // 合并发送
                                            if (mapGroupMessage.size() > 0) {
                                                for (Map.Entry<String, StringBuffer> entry : mapGroupMessage.entrySet()) {
                                                    String groupId = entry.getKey();
                                                    StringBuffer sb = entry.getValue();
                                                    if (!Api.isEmpty(groupId) && !Api.isEmpty(sb.toString())) {
                                                        String groupName = weChat.getNickName(groupId);
                                                        String toUser = sb.toString();
                                                        logger.info("推送群消息[{}]: {}=>{}", groupName, toUser, message);
                                                        weChat.sendMessageByUserId(groupId, toUser + message);
                                                    }
                                                }
                                                mapGroupMessage.clear();
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
