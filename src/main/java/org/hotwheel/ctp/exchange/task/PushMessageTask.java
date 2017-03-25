package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockMessage;
import org.hotwheel.ctp.dao.IStockUser;
import org.hotwheel.ctp.model.StockMessage;
import org.hotwheel.ctp.model.UserInfo;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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

    //@Autowired
    private JavaMailSenderImpl mailSender;

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
                        boolean bResult = sendMail(toMail, subject, content);
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

    public JavaMailSenderImpl getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    private boolean sendMail(final String toUser, final String title, final String message) {
        boolean bRet = false;
        try {
            String fromUser = "StockExchange@sina.cn";

            // 建立邮件讯息
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // 设定收件人、寄件人、主题与内文
            mailMessage.setTo(toUser);
            mailMessage.setFrom(fromUser);
            mailMessage.setSubject(title);
            mailMessage.setText(message);
            //setBody(mailMessage, "This is a test!\r\n123.");
            // 传送邮件
            mailSender.send(mailMessage);
            bRet = true;
        } catch (Exception e) {
            logger.error("发送邮件失败: ", e);
        }
        return bRet;
    }
}
