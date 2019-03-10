package org.hotwheel.ctp.util;

import org.hotwheel.assembly.Api;
import org.hotwheel.assembly.ResourceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * 邮件发送接口
 *
 * @version 1.0.0
 */
public class EmailApi {
    private static Logger logger = LoggerFactory.getLogger(EmailApi.class);

    private static String smtpHost = "smtp.sina.cn";
    private static String smtpUser = "StockExchange@sina.cn";
    private static String smtpPswd = "********";

    static {
        ResourceBundle runtime = ResourceApi.getBundle("runtime");
        smtpHost = runtime.getString("mail.smtp.host");
        smtpUser = runtime.getString("mail.smtp.user");
        smtpPswd = runtime.getString("mail.smtp.pswd");

        if (Api.isEmpty(smtpHost)) {
            smtpHost = Api.getEnv("mail_smtp_host");
        }
        if (Api.isEmpty(smtpUser)) {
            smtpUser = Api.getEnv("mail_smtp_user");
        }
        if (Api.isEmpty(smtpPswd)) {
            smtpPswd = Api.getEnv("mail_smtp_pswd");
        }
    }

    /**
     * 发送邮件
     *
     * @param subject
     * @param toaddress
     * @param content
     */
    public static boolean send(final String toaddress, final String subject, final String content) {
        boolean bRet = false;
        try {
            Email mail = new Email(smtpHost);
            mail.setFrom(smtpUser);
            mail.setNeedAuth(true);
            mail.setSubject(subject);
            mail.setBody(content);
            mail.setTo(toaddress);
            mail.setNamePass(smtpUser, smtpPswd);
            //emailHandle.addFileAffix("/Users/wangfeng/Downloads/123.csv");// 附件文件路径

            bRet = mail.send();
        } catch (Exception e) {
            //
        }
        return bRet;
    }
}