package org.hotwheel.weixin;

/**
 * 微信demo
 */
public class WeChatDemo {

    public static void main(String[] args) {
        WeChat weChat = new WeChat();
        weChat.getUuid();
        weChat.downloadQrCode();
    }

}
