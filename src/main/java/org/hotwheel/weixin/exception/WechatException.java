package org.hotwheel.weixin.exception;

/**
 * 微信异常
 *
 * @version 2.0.1
 */
public class WechatException extends RuntimeException {

    private static final long serialVersionUID = 209248116271894410L;

    public WechatException() {
        super();
    }

    public WechatException(String message) {
        super(message);
    }

    public WechatException(Throwable cause) {
        super(cause);
    }

}
