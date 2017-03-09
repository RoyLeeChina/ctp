package org.hotwheel.stock.exchange.context.pay;

/**
 * Created by wangfeng on 2016/12/22.
 */
public class PayInnerError {
    public int result;
    /**< 错误码 */
    public String returnCode;
    /**< 错误信息 */
    public String returnMessage;
    /**< 返回给用户的错误信息 */
    public String returnUserMessage;
}
