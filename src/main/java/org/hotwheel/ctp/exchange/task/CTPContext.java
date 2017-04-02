package org.hotwheel.ctp.exchange.task;

import org.hotwheel.spring.scheduler.SchedulerContext;
import org.hotwheel.weixin.OldWeChat;

/**
 * 进一步封装消息
 *
 * Created by wangfeng on 2017/3/28.
 * @version 1.0.3
 */
public abstract class CTPContext extends SchedulerContext {
    protected static OldWeChat weChat = null;

    public static OldWeChat getWeChat() {
        return weChat;
    }

    public static void setWeChat(OldWeChat weChat) {
        CTPContext.weChat = weChat;
    }
}
