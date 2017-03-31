package org.hotwheel.weixin;

import com.google.gson.Gson;
import org.hotwheel.assembly.Api;
import org.hotwheel.weixin.bean.AddMsgListEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private WeChat weChat;
    private static Set<String> msgIdList = new HashSet<>();

    /**
     * 接收新消息监听器
     */
    public interface OnNewMsgListener {

        void onNewMsg(final String fromUser, final String toUser, String text);

        void startBeat();
    }

    public HeartBeatThread(WeChat wechat) {
        this.weChat = wechat;
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
            try {
                String host = "webpush.wx.qq.com";
                //window.synccheck={retcode:"0",selector:"7"}
                String syncResult = "";
                String selector = "";
                syncResult = hc.get("https://" + host + "/cgi-bin/mmwebwx-bin/synccheck?skey=" + weChat.skey
                        + "&sid=" + weChat.wxsid
                        + "&uin=" + weChat.wxuin
                        + "&deviceId=" + weChat.deviceId + ""
                        + "&synckey=" + weChat.syncKey
                        + "&r=" + System.currentTimeMillis()
                        + "&_=" + System.currentTimeMillis()
                );

                selector = ss.subStringOne(syncResult, "selector:\"", "\"}");
                if (Api.isEmpty(selector)) {
                    msgIdList.clear();
                    Api.sleep(3 * 1000);
                    continue;
                }

                if (!selector.equals("0")) {//有消息
                    if (mNewMsgListener != null) {
                        //获取新消息
                        String data2 = "{\"BaseRequest\":{\"Uin\":\"" + weChat.wxuin + "\",\"Sid\":\"" + weChat.wxsid + "\",\"Skey\":\"" + weChat.skey + "\",\"DeviceID\":\"" + weChat.deviceId + "\"},\"SyncKey\":"
                                + weChat.gson.toJson(weChat.initbean.getSyncKey()) + ",\"rr\":" + System.currentTimeMillis() + "}";
                        String newMsg = hc.post(weChat.baseUrl + "/webwxsync?sid=" + weChat.wxsid + "&skey=" + weChat.skey + "&pass_ticket=" + weChat.pass_ticket, data2);//
                        if (!Api.isEmpty(newMsg)) {
                            //同步键更新
                            weChat.syncKeys(newMsg);

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
                                } else if (addMsgListEntity.getToUserName().equalsIgnoreCase(weChat.kFromUser)) {
                                    String msgId = addMsgListEntity.getMsgId();
                                    String fromUser = addMsgListEntity.getFromUserName();
                                    String nickName = weChat.mapFriendAndGroup2.get(fromUser);
                                    if (!Api.isEmpty(nickName) && !msgIdList.contains(msgId)) {
                                        String toUser = addMsgListEntity.getToUserName();
                                        String msg = addMsgListEntity.getContent();
                                        msg = "@王布衣 " + msg.trim();
                                        msgIdList.add(msgId);
                                        mNewMsgListener.onNewMsg(fromUser, toUser, msg);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //
            }
        }
    }
}
