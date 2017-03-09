package org.hotwheel.stock.exchange.bean;

/**
 * 内部统一接口响应
 *
 * Created by wangfeng on 16/7/28.
 */
public class InnerApiResult<T> {
    /**< 接口状态 */
    public InnerApiError error;
    /**< 数据区 */
    public T data;

    /**
     * 设定业务处理成功
     */
    public void setSuccess() {
        setSuccess("操作成功");
    }

    /**
     * 设定响应成功, 并指定用户界面的响应消息
     * @param message
     */
    public void setSuccess(String message) {
        set(0, "SUCCESS", message);
    }

    /**
     * 设定响应状态及信息, 接口响应和用户界面响应信息相同
     * @param status
     * @param message
     */
    public void set(int status, String message) {
        set(status, message, message);
    }

    /**
     * 设置响应
     * @param status
     * @param message
     * @param userMessage
     */
    public void set(int status, String message, String userMessage) {
        if (error == null) {
            error = new InnerApiError();
        }
        error.returnCode = status;
        error.returnMessage = message;
        error.returnUserMessage = userMessage;
    }
}
