package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.*;
import org.hotwheel.ctp.model.*;
import org.hotwheel.ctp.util.EmailApi;
import org.hotwheel.ctp.util.PolicyApi;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            List<String> allCode = stockSubscribe.checkoutAllCode();
            if (allCode != null && allCode.size() > 0) {
                for (String code : allCode) {
                    List<StockHistory> shList = stockHistory.selectOne(code);
                    StockMonitor info = PolicyApi.dxcl(shList);
                    if (info != null) {
                        info.setCode(code);
                        StockMonitor old = stockMonitor.query(code);
                        int result = -1;
                        if (old == null) {
                            //info.setFlag(StockOptions.kNormalState);
                            //info.setCreateTime(today);
                            //info.setPressure1(Api.toString(pressure1));
                            //info.setSupport1(Api.toString(support1));
                            //info.setPressure2(Api.toString(pressure2));
                            //info.setSupport2(Api.toString(support2));
                            //info.setStop(Api.toString(stop));
                            //info.setResistance(Api.toString(resistance));
                            result = stockMonitor.insert(info);
                            if (result == 0) {
                                logger.error("{}添加{}策略价格范围失败", info.getDay(), code);
                            }
                        } else {
                            //info.setFlag(StockOptions.kNormalState);
                            //info.setCreateTime(today);
                            //info.setPressure1(Api.toString(pressure1));
                            //info.setSupport1(Api.toString(support1));
                            //info.setPressure2(Api.toString(pressure2));
                            //info.setSupport2(Api.toString(support2));
                            //info.setStop(Api.toString(stop));
                            //info.setResistance(Api.toString(resistance));
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
                        List<StockSubscribe> tmpSubscribe = stockSubscribe.queryByCode(code);
                        logger.info("{}({}): {}~{}/{}~{}, 阻力位{}, 止损位{}。",
                                stockName, stockCode, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                                info.getResistance(), info.getStop());
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
                                    try {
                                        String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                                        if (EmailApi.send(user.getEmail(), prefix + "-CTP策略订阅早盘提示", content)) {
                                            //
                                        }
                                    } catch (Exception e) {
                                        //
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Api.sleep(StockOptions.kRealTimenterval);
            break;
        }
    }
}
