package org.hotwheel.weixin;

/**
 * 消息类型
 * Created by wangfeng on 2017/4/3.
 *
 * @version 2.0.1
 */
public enum WxMsgType {


    /**
     * 文本消息
     */
    //public final static int TEXT = 1;

    /**
     * 成功获取联系人信息
     */
    //public final static int GOTCONTACT = 51;

    /**
     * 视频
     */
    //public final static int VIDEO = 62;

    /**
     * 群管理信息
     */
    //public final static int GROUP = 10000;

    TEXT(1, "文本"),
    IMAGE(3, "图片"),
    VOICE(34, "语音"),
    FriendRequest(37, "好友请求"),
    RecommendInfo(42, "名片"),
    CdnUrl(47, "cdnurl"),
    AppMsg(49, "应用消息"),
    GOTCONTACT(51, "成功获取联系人信息"),
    VIDEO(62, "视频"),
    GROUPMANAGE(10000, "群管理信息"),
    UNDO(10002, "撤回消息");

    /**
     * 回撤 消息
     */
    //public final static int UNDO = 10002;


    // 成员变量
    private int code;
    private String message;

    private WxMsgType(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

    public int intValue() {
        return code;
    }

    public boolean equals(int code) {
        return this.code == code;
    }
}
