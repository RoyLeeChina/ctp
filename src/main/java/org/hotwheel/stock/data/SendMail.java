package org.hotwheel.stock.data;

import org.hotwheel.assembly.ResourceApi;

import java.util.ResourceBundle;

public class SendMail {
	private static String hostName = "smtp.sina.cn";
	private static String fromAddress = "StockExchange@sina.cn";
	private static String fromAPass = "********";

	static {
		ResourceBundle runtime = ResourceApi.getBundle("runtime");
		hostName    = runtime.getString("email.smtp");
		fromAddress = runtime.getString("email.user");
		fromAPass   = runtime.getString("email.pswd");
	}

	/**
	 * 
	 * @param subject
	 * @param toaddress
	 * @param content
	 * @throws Exception
	 */
	public static void send(final String toaddress, final String subject, final String content) throws Exception {
		EmailHandle emailHandle = new EmailHandle(hostName);
		emailHandle.setFrom(fromAddress);
		emailHandle.setNeedAuth(true);
		emailHandle.setSubject(subject);
		emailHandle.setBody(content);
		emailHandle.setTo(toaddress);
		emailHandle.setFrom(fromAddress);
		//emailHandle.addFileAffix("/Users/wangfeng/Downloads/123.csv");// 附件文件路径
		emailHandle.setNamePass(fromAddress, fromAPass);
		emailHandle.sendEmail();
	}
}
