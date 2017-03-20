package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockHistory;
import org.hotwheel.ctp.dao.IStockMonitor;
import org.hotwheel.ctp.dao.IStockSubscribe;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.model.StockMonitor;
import org.hotwheel.ctp.util.PolicyApi;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                    }
                }
            }
            Api.sleep(StockOptions.kRealTimenterval);
        }
    }
}
