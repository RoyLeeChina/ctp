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
    private StringSub ss = new StringSub();
    private Gson gson = new Gson();
    private boolean beat = true;
    private OnNewMsgListener mNewMsgListener;
    private WeChat weChat;
    private static Set<String> msgIdList = new HashSet<>();

    /**
     * 接收新消息监听器
     */
    public interface OnNewMsgListener {

        void onNewMsg(final String groupId, final String fromUser, final String toUser, String text);

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
                syncResult = hc.get("https://" + host + "/cgi-bin/mmwebwx-bin/synccheck?wxSkey=" + weChat.wxSkey
                        + "&sid=" + weChat.wxSid
                        + "&uin=" + weChat.wxUin
                        + "&wxDeviceId=" + weChat.wxDeviceId + ""
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
                        String data2 = "{\"BaseRequest\":{\"Uin\":\"" + weChat.wxUin + "\",\"Sid\":\"" + weChat.wxSid + "\",\"Skey\":\"" + weChat.wxSkey + "\",\"DeviceID\":\"" + weChat.wxDeviceId + "\"},\"SyncKey\":"
                                + weChat.gson.toJson(weChat.initbean.getSyncKey()) + ",\"rr\":" + System.currentTimeMillis() + "}";
                        String newMsg = hc.post(weChat.baseUrl + "/webwxsync?sid=" + weChat.wxSid + "&wxSkey=" + weChat.wxSkey + "&pass_ticket=" + weChat.pass_ticket, data2);//
                        if (!Api.isEmpty(newMsg)) {
                            //同步键更新
                            weChat.syncKeys(newMsg);

                            //获取消息
                            WxMessage wxMessage = gson.fromJson(newMsg, WxMessage.class);
                            List<AddMsgListEntity> msgList = wxMessage.getAddMsgList();
                            for (AddMsgListEntity addMsgListEntity : msgList) {
                                // 只处理群消息
                                if (addMsgListEntity.getFromUserName().startsWith("@@")) {
                                    String groupId = addMsgListEntity.getFromUserName();
                                    String toUser = addMsgListEntity.getToUserName();
                                    String msg = addMsgListEntity.getContent();
                                    int pos = msg.indexOf("<br/>");
                                    String fromUser = msg.substring(0, pos);
                                    msg = msg.substring(pos + 5);
                                    mNewMsgListener.onNewMsg(groupId, fromUser, toUser, msg);
                                } else if (addMsgListEntity.getToUserName().equalsIgnoreCase(weChat.kFromUser)) {
                                    String groupId = null;
                                    String msgId = addMsgListEntity.getMsgId();
                                    String fromUser = addMsgListEntity.getFromUserName();
                                    String nickName = weChat.mapUserToNick.get(fromUser);
                                    if (!Api.isEmpty(nickName) && !msgIdList.contains(msgId)) {
                                        String toUser = addMsgListEntity.getToUserName();
                                        String msg = addMsgListEntity.getContent();
                                        msg = "@王布衣 " + msg.trim();
                                        msgIdList.add(msgId);
                                        mNewMsgListener.onNewMsg(groupId, fromUser, toUser, msg);
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
