package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.dao.*;
import org.hotwheel.ctp.model.*;
import org.hotwheel.ctp.util.EmailApi;
import org.hotwheel.ctp.util.PolicyApi;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成当天策略任务
 *
 * Created by wangfeng on 2017/3/21.
 * @version 1.0.1
 */
@Service("createPolicyTask")
public class CreatePolicyTask extends SchedulerContext {
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

    @Override
    protected void service() {
        while (true) {
            if (isTimeExpire()) {
                logger.info("运行时间{}->{}到, 任务退出", taskStartTime, taskEndTime);
                break;
            }
            // 获取订阅的个股
            List<String> allCode = new ArrayList<>();
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
                        List<User> userList = stockUser.selectAll();
                        if (userList != null && userList.size() > 0) {
                            for (User user : userList) {
                                if (!Api.isEmpty(user.getEmail())) {
                                    String content = String.format("%s(%s): %s~%s/%s~%s, 阻力位%s, 止损位%s。",
                                            stockName, code, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                                            info.getResistance(), info.getStop());
                                    logger.info(content);
                                    String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                                    EmailApi.send(user.getEmail(), prefix + "-CTP策略订阅早盘提示-" + stockName, content);
                                }
                            }
                        }
                    } else {
                        List<StockSubscribe> tmpSubscribe = stockSubscribe.queryByCode(code);
                        if (tmpSubscribe == null) {
                            logger.info("{} 暂无用户订阅");
                        } else {
                            for (StockSubscribe userSubscribe : tmpSubscribe) {
                                User user = stockUser.select(userSubscribe.getPhone());
                                if (user == null) {
                                    logger.info("not found user={}", userSubscribe.getPhone());
                                } else if (!Api.isEmpty(user.getEmail())) {
                                    String content = String.format("%s(%s): %s~%s/%s~%s, 阻力位%s, 止损位%s。",
                                            stockName, code, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                                            info.getResistance(), info.getStop());
                                    logger.info(content);
                                    String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                                    EmailApi.send(user.getEmail(), prefix + "-CTP策略订阅早盘提示", content);
                                }
                            }
                        }
                    }
                }
            }
            //Api.sleep(StockOptions.kRealTimenterval);
            break;
        }
    }
}
