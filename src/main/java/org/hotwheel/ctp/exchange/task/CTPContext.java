package org.hotwheel.ctp.exchange.task;

import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.weixin.WeChatApp;

/**
 * 进一步封装消息
 *
 * Created by wangfeng on 2017/3/28.
 * @version 1.0.3
 */
public abstract class CTPContext extends SchedulerContext {
    private static WeChatApp weChat = null;

    public static WeChatApp getWeChat() {
        return weChat;
    }

    public static void setWeChat(WeChatApp weChat) {
        CTPContext.weChat = weChat;
    }
}
