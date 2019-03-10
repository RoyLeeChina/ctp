package org.hotwheel.ctp.model;

/**
 * 策略消息
 * <p>
 * Created by wangfeng on 2017/3/22.
 *
 * @version 1.0.2
 */
public class PolicyMessage {
    private String userName;
    private String nickName;
    private String groupName;
    private String message;
    private String title;
    private StringBuffer buffer = new StringBuffer();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
