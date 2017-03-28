package org.hotwheel.weixin;

import com.google.gson.Gson;
import org.hotwheel.assembly.Api;
import org.hotwheel.weixin.bean.AddMsgListEntity;

import java.util.List;

/**
 * 心跳线程
 *
 * Created by wangfeng on 2017/3/26.
 * @version 1.0.2
 */
public class HeartBeatThread extends Thread {

    private WxHttpClient hc = WxHttpClient.getInstance();
    private StringSubClass ss = new StringSubClass();
    private Gson gson = new Gson();
    private boolean beat = true;
    private OnNewMsgListener mNewMsgListener;
    private WeChatApp wechat;

    /**
     * 接收新消息监听器
     */
    public interface OnNewMsgListener {

        void onNewMsg(final String fromUser, final String toUser, String text);

        void startBeat();
    }

    public HeartBeatThread(WeChatApp wechat) {
        this.wechat = wechat;
    }

    public void setmNewMsgListener(OnNewMsgListener mNewMsgListener) {
        this.mNewMsgListener = mNewMsgListener;
    }

    @Override
    public void run() {
        if (mNewMsgListener != null) {
            mNewMsgListener.startBeat();
        }
        while (beat) {
            String[] hosts = {
                    "webpush.weixin.qq.com",
                    "webpush2.weixin.qq.com",
                    "webpush.wechat.com",
                    "webpush1.wechat.com",
                    "webpush2.wechat.com"
            };
            //window.synccheck={retcode:"0",selector:"7"}
            String syncResult = "";
            String selector = "";
            for (String host : hosts) {
                syncResult = hc.get("https://"+host+"/cgi-bin/mmwebwx-bin/synccheck?skey=" + wechat.skey
                        + "&sid=" + wechat.wxsid
                        + "&uin=" + wechat.wxuin
                        + "&deviceId=" + wechat.deviceId + ""
                        + "&synckey=" + wechat.keyString
                        + "&r=" + System.currentTimeMillis()
                        + "&_=" + System.currentTimeMillis()
                );

                try {
                    selector = ss.subStringOne(syncResult, "selector:\"", "\"}");
                    if (!Api.isEmpty(selector) && selector.equals("0")) {
                        continue;
                    } else if (!Api.isEmpty(selector) && !selector.equals("0")) {
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (Api.isEmpty(selector)) {
                Api.sleep(5 * 1000);
                continue;
            }

            if (!selector.equals("0")) {//有消息
                if (mNewMsgListener != null) {
                    //获取新消息
                    String data2 = "{\"BaseRequest\":{\"Uin\":\"" + wechat.wxuin + "\",\"Sid\":\"" + wechat.wxsid + "\",\"Skey\":\"" + wechat.skey + "\",\"DeviceID\":\"" + wechat.deviceId + "\"},\"SyncKey\":"
                            + wechat.gson.toJson(wechat.initbean.getSyncKey()) + ",\"rr\":" + System.currentTimeMillis() + "}";
                    String newMsg = hc.post(wechat.baseUrl + "/webwxsync?sid=" + wechat.wxsid + "&skey=" + wechat.skey + "&pass_ticket=" + wechat.pass_ticket, data2);//
                    //同步键更新
                    wechat.syncKeys(newMsg);

                    //获取消息
                    MsgBean msgBean = gson.fromJson(newMsg, MsgBean.class);
                    List<AddMsgListEntity> msgList = msgBean.getAddMsgList();
                    for (AddMsgListEntity addMsgListEntity : msgList) {
                        // 只处理群消息
                        if (addMsgListEntity.getFromUserName().startsWith("@@")) {
                            String fromUser = addMsgListEntity.getFromUserName();
                            String toUser = addMsgListEntity.getToUserName();
                            String msg = addMsgListEntity.getContent();
                            msg = msg.substring(msg.indexOf("<br/>") + 5);
                            mNewMsgListener.onNewMsg(fromUser, toUser, msg);
                        } else if ( addMsgListEntity.getToUserName().equalsIgnoreCase(wechat.kFromUser)) {
                            String fromUser = addMsgListEntity.getFromUserName();
                            String toUser = addMsgListEntity.getToUserName();
                            String msg = addMsgListEntity.getContent();
                            msg = "@王布衣 " + msg.trim();
                            mNewMsgListener.onNewMsg(fromUser, toUser, msg);
                        }
                    }
                }
            }
        }
    }
}
