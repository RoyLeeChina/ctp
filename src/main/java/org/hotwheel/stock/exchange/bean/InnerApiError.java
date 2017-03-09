package org.hotwheel.stock.exchange.bean;

/**
 * 内部统一错误信息格式
 *
 * Created by wangfeng on 16/7/28.
 */
public class InnerApiError {
    /**< 错误码 */
    public int returnCode;
    /**< 错误信息 */
    public String returnMessage;
    /**< 返回给用户的错误信息 */
    public String returnUserMessage;
}
