package org.hotwheel.weixin;

import org.hotwheel.util.StringUtils;

/**
 * 微信demo
 */
public class WeChatDemo {

    public static void main(String[] args) {
        String[] aa = "aaa|bbb|ccc".split("\\|");

        aa = "abc@123".split("@");

        //String[] aa = "aaa|bbb|ccc".split("\\|"); 这样才能得到正确的结果

        for (int i = 0; i < aa.length; i++) {

            System.out.println("--" + aa[i]);

        }

        String s = "dy 600111";
        char kg = 8197;
        s = kg + s;
        String s0 = StringUtils.trimWhitespace(s);
        String s1 = s.replace(kg, (char) 32);
        WeChat weChat = new WeChat();
        weChat.getUuid();
        weChat.downloadQrCode();
    }

}
