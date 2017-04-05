package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.*;
import org.hotwheel.ctp.model.*;
import org.hotwheel.ctp.util.DateUtils;
import org.hotwheel.ctp.util.EmailApi;
import org.hotwheel.ctp.util.PolicyApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 生成当天策略任务
 *
 * Created by wangfeng on 2017/3/21.
 * @version 1.0.1
 */
@Service("createPolicyTask")
public class CreatePolicyTask extends CTPContext {
    private Logger logger = LoggerFactory.getLogger(CreatePolicyTask.class);

    private final static String kIndexShangHai = "sh000001";
    private final static String kIndexShenZhen = "sz399001";
    private final static String kIndexChuangYe = "sz399006";
    private final static String kAllIndex = kIndexShangHai + ',' + kIndexShenZhen + ',' + kIndexChuangYe;

    @Autowired
    private IStockUser stockUser;

    @Autowired
    private IStockCode stockCode;

    @Autowired
    private IStockSubscribe stockSubscribe;

    @Autowired
    private IStockHistory stockHistory;

    @Autowired
    private IStockMonitor stockMonitor;

    @Autowired
    private IStockMessage stockMessage;

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
            //Map<String, PolicyMessage> mapMessage = new HashMap<>();
            // 获取订阅的个股
            Set<String> allCode = new HashSet<>();
            // 上证指数
            allCode.add("sh000001");
            // 深证成指
            allCode.add("sz399001");
            // 创业板指数
            allCode.add("sz399006");
            List<String> stockList = stockSubscribe.checkoutAllCode();
            if (stockList != null && stockList.size() > 0) {
                allCode.addAll(stockList);
            }
            for (String code : allCode) {
                // 查询现存历史记录
                List<StockHistory> shList = stockHistory.selectOne(code);
                StockMonitor info = PolicyApi.dxcl(shList);
                if (info != null) {
                    info.setCode(code);
                    StockMonitor old = stockMonitor.query(code);
                    int result = -1;
                    if (old == null) {
                        result = stockMonitor.insert(info);
                        if (result == 0) {
                            logger.error("{}添加{}策略价格范围失败", info.getDay(), code);
                        }
                    } else {
                        result = stockMonitor.update(info);
                        if (result == 0) {
                            logger.error("{}更新{}策略价格范围失败", info.getDay(), code);
                        }
                    }
                    StockCode sc = stockCode.select(code, code);
                    String stockName = null;
                    if (sc != null) {
                        stockName = sc.getName();
                    }
                    logger.info("{}({}): {}~{}/{}~{}, 阻力位{}, 止损位{}。",
                            stockName, code, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                            info.getResistance(), info.getStop());
                    if (kAllIndex.indexOf(code) >= 0) {
                        String content = String.format("%s(%s): %s~%s/%s~%s, 阻力位%s, 止损位%s。",
                                stockName, code, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                                info.getResistance(), info.getStop());
                        logger.info(content);
                        String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                        String title = prefix + "-CTP策略订阅早盘提示-" + stockName;
                        content += StockOptions.kSuffixMessage;
                        weChat.sendGroupMessage("", title + ": " + content);
                    } else {
                        List<StockSubscribe> tmpSubscribe = stockSubscribe.queryByCode(code);
                        if (tmpSubscribe == null) {
                            logger.info("{} 暂无用户订阅");
                        } else {
                            for (StockSubscribe userSubscribe : tmpSubscribe) {
                                UserInfo user = stockUser.select(userSubscribe.getPhone());
                                if (user == null) {
                                    logger.info("not found user={}", userSubscribe.getPhone());
                                } else {
                                    boolean bSent = true;
                                    Date tmpDate = user.getSendDate();
                                    Date sendDate = DateUtils.getZero(tmpDate);
                                    Date today = DateUtils.getZero(new Date());
                                    // 如果已经是第二天了
                                    if (bSent && today.after(sendDate)) {
                                        bSent = false;
                                    }
                                    bSent = false;
                                    if (!bSent) {
                                        String content = String.format("%s(%s): %s~%s/%s~%s, 阻力位%s, 止损位%s。",
                                                stockName, code, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                                                info.getResistance(), info.getStop());
                                        logger.info("{}: {}", user.getMemberName(), content);
                                        String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                                        String title = prefix + "-CTP策略订阅早盘提示";
                                        content += StockOptions.kSuffixMessage;
                                        if (!Api.isEmpty(user.getWeixin())) {
                                            weChat.sendMessage(user.getWeixin(), title + ": " + content);
                                        } else if (!Api.isEmpty(user.getEmail())) {
                                            EmailApi.send(user.getEmail(), prefix + "-CTP策略订阅早盘提示", content);
                                        }
                                        stockUser.finished(user);
                                    }
                                }
                                Api.sleep(1000);
                            }
                        }
                    }
                }
                Api.sleep(1000);
            }
            /*
            for (Map.Entry<String, PolicyMessage> entry : mapMessage.entrySet()) {
                String phone = entry.getKey();
                PolicyMessage pm = entry.getValue();
                if (pm != null) {
                    StockMessage sm = new StockMessage();
                    sm.setFlag("00");
                    sm.setPhone(phone);
                    sm.setRemark(pm.getBuffer().toString());
                    //EmailApi.send(email, pm.getTitle(), pm.getBuffer().toString());
                    stockMessage.insert(sm);
                }
            }*/
            //Api.sleep(StockOptions.kRealTimenterval);
            break;
        }
    }
}
