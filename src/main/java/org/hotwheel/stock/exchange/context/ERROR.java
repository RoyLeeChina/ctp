/**
 * 
 */
package org.hotwheel.stock.exchange.context;

/**
 * 错误码
 * 
 * @author WangFeng
 * @remark 错误码分段
 */
public final class ERROR {
	/** 正确码 */
	public final static int iSUCCESS = 0;
	/** 正确码描述 */
	public final static String sSUCCESS = "成功";
	public final static int UNKNOWN = 10000;
	public final static int API_BASE = UNKNOWN + 10000;
	public final static int TOKEN = API_BASE + 20;
	public final static int PAY = API_BASE + 30;
	/** 文件类错误码基点 */
	public final static int FILE = UNKNOWN + 97000;
	/** 应用程序上传错误码基点 */
	public final static int APPS = UNKNOWN + 98000;

	/**< 消息类 */
	public final static int SMC = 99000;
	/**< 消息-小秘书 */
	public final static int SMS = SMC + 100;
	/**< 消息-小秘书 */
	public final static int NOTICE = SMC + 200;
}
