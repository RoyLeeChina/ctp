package org.hotwheel.weixin;

import com.google.gson.Gson;
import org.hotwheel.weixin.MsgBean.AddMsgListEntity;

import java.util.List;

/**
 * Created by wangfeng on 2017/3/26.
 */
public class HeartBeatThread extends Thread{

    private HttpClient hc=HttpClient.getInstance();
    private StringSubClass ss=new StringSubClass();
    private Gson gson=new Gson();
    private boolean beat=true;
    private OnNewMsgListener mNewMsgListener;
    private WeChatApp wechat;

    public interface OnNewMsgListener{//接收新消息监听器
        void onNewMsg(String text);
        void startBeat();
    }
    public HeartBeatThread(WeChatApp wechat) {
        this.wechat=wechat;
    }

    public void setmNewMsgListener(OnNewMsgListener mNewMsgListener) {
        this.mNewMsgListener = mNewMsgListener;
    }
    @Override
    public void run() {
        if (mNewMsgListener!=null) {
            mNewMsgListener.startBeat();
        }
        while (beat) {
            //window.synccheck={retcode:"0",selector:"7"}
            String syncResult=hc.get("https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin/synccheck?skey="+wechat.skey
                    +"&sid="+wechat.wxsid
                    +"&uin="+wechat.wxuin
                    +"&deviceId=" + wechat.deviceId + ""
                    +"&synckey="+wechat.keyString
                    +"&r="+System.currentTimeMillis()
                    +"&_="+System.currentTimeMillis()
            );
//			System.err.println(syncResult);
            String selector;
            try {
                selector=ss.subStringOne(syncResult, "selector:\"", "\"}");
            } catch (Exception e) {
                continue;
            }

            if (!selector.equals("0")) {//有消息
                if (mNewMsgListener!=null) {
                    //获取新消息
                    String data2="{\"BaseRequest\":{\"Uin\":\""+wechat.wxuin+"\",\"Sid\":\""+wechat.wxsid+"\",\"Skey\":\""+wechat.skey+"\",\"DeviceID\":\"" + wechat.deviceId + "\"},\"SyncKey\":"
                            +wechat.gson.toJson(wechat.initbean.getSyncKey())+",\"rr\":"+System.currentTimeMillis()+"}";
                    String newMsg=hc.post(wechat.baseUrl+"/webwxsync?sid="+wechat.wxsid+"&skey="+wechat.skey+"&pass_ticket="+wechat.pass_ticket,data2);//


                    //同步键更新
                    wechat.syncKeys(newMsg);

                    //获取消息
                    MsgBean msgBean=gson.fromJson(newMsg, MsgBean.class);
                    List<AddMsgListEntity> msgList = msgBean.getAddMsgList();
                    for (AddMsgListEntity addMsgListEntity : msgList) {
                        if (addMsgListEntity.getFromUserName().startsWith("@@")) {//只处理群消息
                            String msg=addMsgListEntity.getContent();
                            msg=msg.substring(msg.indexOf("<br/>")+5);
                            mNewMsgListener.onNewMsg(msg);
                        }
                    }//for
                }
            }
            //wechat.sendMessage("123");
        }//while
    }
}
