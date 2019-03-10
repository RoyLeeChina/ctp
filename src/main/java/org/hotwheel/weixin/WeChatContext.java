package org.hotwheel.weixin;

/**
 * 消息接口
 * <p>
 * Created by wangfeng on 2017/4/3.
 *
 * @version 2.0.1
 */
public interface WeChatContext {

    /**
     * 发送帮助信息
     *
     * @param toUser
     */
    void sendHelp(final String toUser);

    /**
     * 消息接口
     *
     * @param groupId
     * @param fromUser
     * @param toUser
     * @param text
     */
    void handleMessage(final String groupId, final String fromUser, final String toUser, String text);
}
