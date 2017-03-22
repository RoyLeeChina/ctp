package org.hotwheel.ctp.model;

/**
 * 策略消息
 *
 * Created by wangfeng on 2017/3/22.
 * @version 1.0.2
 */
public class PolicyMessage {
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
}
