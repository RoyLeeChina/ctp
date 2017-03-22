package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockMessage;
import org.hotwheel.ctp.dao.IStockUser;
import org.hotwheel.ctp.model.StockMessage;
import org.hotwheel.ctp.model.UserInfo;
import org.hotwheel.ctp.util.EmailApi;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 消息推送任务
 *
 * Created by wangfeng on 2017/3/22.
 * @version 1.0.2
 */
@Service("pushMessageTask")
public class PushMessageTask extends SchedulerContext {
    private static Logger logger = LoggerFactory.getLogger(PushMessageTask.class);

    @Autowired
    private IStockMessage stockMessage;

    @Autowired
    private IStockUser stockUser;

    @Override
    protected void service() {
        while (true) {
            if (isTimeExpire()) {
                logger.info("运行时间{}->{}到, 任务退出", taskStartTime, taskEndTime);
                break;
            }
            stockMessage.cleanAll();
            // 捡出全部未发送的消息
            List<StockMessage> messageList = stockMessage.selectAll("00");
            if (messageList != null && messageList.size() > 0) {
                for (StockMessage message : messageList) {
                    long id = message.getId();
                    String type = "01";
                    String phone = message.getPhone();
                    UserInfo userInfo = stockUser.select(phone);
                    if (userInfo == null) {
                        type = "97";
                    } else {
                        String toMail = userInfo.getEmail();
                        String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                        String subject = prefix + " CTP策略提示";
                        String content = message.getRemark();
                        logger.info("{}({}): {} {}", content, toMail, subject, content);
                        boolean bResult = EmailApi.send(toMail, subject, content);
                        if (bResult) {
                            type = "01";
                        } else {
                            type = "02";
                        }
                    }
                    stockMessage.updateType(id, type);
                }
            }
            Api.sleep(StockOptions.kRealTimenterval);
        }
    }
}
