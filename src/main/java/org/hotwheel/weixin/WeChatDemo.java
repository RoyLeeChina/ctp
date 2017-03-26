package org.hotwheel.weixin;

import org.hotwheel.weixin.HeartBeatThread.OnNewMsgListener;
import org.hotwheel.weixin.WaitScanAndLoginThread.OnScanListener;

public class WeChatDemo {

    public static void main(String[] args) {
        WeChatApp weChat=new WeChatApp();
        weChat.setmScanListener(new OnScanListener() {

            @Override
            public void onSure() {
                System.out.println("登陆成功");

            }

            @Override
            public void onScan() {
                System.out.println("已经扫描成功，等待确认登陆");

            }
        });
        weChat.setmNewMsgListener(new OnNewMsgListener() {

            @Override
            public void onNewMsg(String text) {//只处理群消息
                System.out.println("接收到消息:"+text);

            }

            @Override
            public void startBeat() {
                System.out.println("开始心跳");

            }
        });
        weChat.gogogo();

    }

}
