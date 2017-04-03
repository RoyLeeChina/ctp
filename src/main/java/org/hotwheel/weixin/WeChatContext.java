package org.hotwheel.weixin;

/**
 * 消息接口
 *
 * Created by wangfeng on 2017/4/3.
 * @version 2.0.1
 */
public interface WeChatContext {
    /**
     * 消息接口
     * @param groupId
     * @param fromUser
     * @param toUser
     * @param text
     */
    void handleMessage(final String groupId, final String fromUser, final String toUser, String text);
}
