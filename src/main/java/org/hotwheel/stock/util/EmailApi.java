package org.hotwheel.stock.util;

import org.hotwheel.assembly.Api;
import org.hotwheel.assembly.ResourceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * 邮件发送接口
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
	 * @param subject
	 * @param toaddress
	 * @param content
	 * @throws Exception
	 */
	public static void send(final String toaddress, final String subject, final String content) throws Exception {
		Email mail = new Email(smtpHost);
		mail.setFrom(smtpUser);
		mail.setNeedAuth(true);
		mail.setSubject(subject);
		mail.setBody(content);
		mail.setTo(toaddress);
		//emailHandle.addFileAffix("/Users/wangfeng/Downloads/123.csv");// 附件文件路径
		mail.setNamePass(smtpUser, smtpPswd);
		mail.send();
	}
}