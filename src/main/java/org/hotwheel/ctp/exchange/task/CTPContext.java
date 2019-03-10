package org.hotwheel.ctp.exchange.task;

import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.weixin.WeChat;

/**
 * 进一步封装消息
 * <p>
 * Created by wangfeng on 2017/3/28.
 *
 * @version 1.0.3
 */
public abstract class CTPContext extends SchedulerContext {
    protected static WeChat weChat = null;

    /**
     * 服务器是否正在关闭
     */
    protected static boolean serverIsCloseing = false;

    public static WeChat getWeChat() {
        return weChat;
    }

    public static void setWeChat(WeChat weChat) {
        CTPContext.weChat = weChat;
    }

    public static boolean isServerCloseing() {
        return serverIsCloseing;
    }

    public static void setServerCloseing(boolean serverIsCloseing) {
        CTPContext.serverIsCloseing = serverIsCloseing;
    }
}
