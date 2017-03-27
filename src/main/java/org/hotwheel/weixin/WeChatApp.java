package org.hotwheel.weixin;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import org.hotwheel.assembly.Api;
import org.hotwheel.weixin.DownLoadQrCodeThread.OnloadQrCodeFinnishListener;
import org.hotwheel.weixin.HeartBeatThread.OnNewMsgListener;
import org.hotwheel.weixin.WaitScanAndLoginThread.OnScanListener;
import org.hotwheel.weixin.bean.BaseResponeBean;
import org.hotwheel.weixin.bean.SyncKeyEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信主程序
 *
 * @version 1.0.2
 */
public class WeChatApp {
    private final static String kGroupId = "CTP内测";
    boolean isBeat = false;
    //************** 一些变量
    public String uuid;
    public String baseUrl;
    public String skey;
    public String wxsid;
    public String pass_ticket;
    public String wxuin;
    public String keyString;
    public BaseResponeBean initbean;
    public String deviceId;
    public String fromUser;
    public String nickName;

    // 好友列表
    public Map<String, String> mapFriendAndGroup = new HashMap<>();
    public Map<String, String> mapGroupMember = new HashMap<>();

    //***************监听接口

    public Gson gson = new Gson();
    private HttpClient hc = HttpClient.getInstance();
    private StringSubClass ss = new StringSubClass();
    private OnScanListener mScanListener;
    private OnNewMsgListener mNewMsgListener;
    private OnLoadQrCodeListener mQrCodeListener;

    /**
     * 构造方法
     */
    public WeChatApp() {
        long randomId = System.nanoTime();
        String str = "" + randomId;
        deviceId = "e" + str.substring(2, 17);
    }

    //获取图片的byte数组，主要用于安卓端bitmap展示。pc可不用
    interface OnLoadQrCodeListener {
        void onLoadSuccess(byte[] imageBytes);
    }

    /**
     * 必须先设置两个监听器，然后调用这个方法
     * @remark 获得uuid
     */
    public void startListner() {
        System.setProperty("jsse.enableSNIExtension", "false");//避免ssl异常
        String result = hc.post("https://login.weixin.qq.com/jslogin",
                "appid=wx782c26e4c19acffb&fun=new&lang=zh_CN&_=" + System.currentTimeMillis());
        uuid = ss.subStringOne(result, ".uuid = \"", "\";");//得到uuid
        //开启下载二维码的线程,安卓端需要把这里设置为false
        DownLoadQrCodeThread qrCodeThread = new DownLoadQrCodeThread("https://login.weixin.qq.com/qrcode/" + uuid + "?t=webwx&_=", true);
        qrCodeThread.setListener(new OnloadQrCodeFinnishListener() {

            @Override
            public void onLoadSuccess(byte[] imageBytes) {
                if (mQrCodeListener != null) {
                    mQrCodeListener.onLoadSuccess(imageBytes);
                }
                //二维码下载完成，开启轮询线程等待扫描二维码和登陆
                WaitScanAndLoginThread loginThread = new WaitScanAndLoginThread(uuid, WeChatApp.this);
                loginThread.setmScanListener(mScanListener);
                loginThread.start();
            }
        });
        qrCodeThread.start();

    }


    /**
     * 在成功登陆后初始化微信相关参数
     */
    void init() {
        for (int i = 0; i < 5; i++) {//开5个线程去初始化
            new InitThread().start();
        }

        getFriendAndGroup();

    }

    public void sendGroupMessage(String groupId, String userId, String message) {
        long tt = new Date().getTime();
        //tt = tt / 1000;
        tt = tt * 10000;
        tt += 1234;
        //String from = mapFriendAndGroup.get(groupId);
        //String to = mapFriendAndGroup.get(userId);
        String from = fromUser;
        String to = mapFriendAndGroup.get(groupId);
        message = "@夏日 " + message;
        //String msg = "\"Msg\":{\"Type\":1,\"Content\":\"要发送的消息\",\"FromUserName\":\""+wxuin+"\",\"ToUserName\":\""+wxuin+"\",\"LocalID\":\""+tt+"\",\"ClientMsgId\":\""+tt+"\"}";
        String msg = "\"Msg\":{\"Type\":1,\"Content\":\"" + message + "\",\"FromUserName\":\"" + from + "\",\"ToUserName\":\"" + to + "\",\"LocalID\":\"" + tt + "\",\"ClientMsgId\":\"" + tt + "\"}";
        //String data="{\"BaseRequest\":{\"Uin\":\""+wxuin+"\",\"Sid\":\""+wxsid+"\",\"Skey\":\""+skey+"\",\"DeviceID\":\"" + deviceId + "\"},"+msg+",\"Scene\":0}";
        //e110854731714634
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxuin + "\",\"Sid\":\"" + wxsid + "\",\"Skey\":\"" + skey + "\",\"DeviceID\":\"" + deviceId + "\"}," + msg + "}";
        hc.contentType = "application/json; charset=UTF-8";
        String initResult = hc.post(baseUrl + "/webwxsendmsg?pass_ticket=" + pass_ticket,
                data);
    }

    /**
     * 发送消息 webwxsendmsg?pass_ticket=xxx
     *
     * @param message
     */
    public void sendMessage(String userId, String message) {
        //statusnotify();
        long tt = new Date().getTime();
        //tt = tt / 1000;
        tt = tt * 10000;
        tt += 1234;
        String from = fromUser;
        String to = mapFriendAndGroup.get(userId);
        //String msg = "\"Msg\":{\"Type\":1,\"Content\":\"要发送的消息\",\"FromUserName\":\""+wxuin+"\",\"ToUserName\":\""+wxuin+"\",\"LocalID\":\""+tt+"\",\"ClientMsgId\":\""+tt+"\"}";
        String msg = "\"Msg\":{\"Type\":1,\"Content\":\"" + message + "\",\"FromUserName\":\"" + from + "\",\"ToUserName\":\"" + to + "\",\"LocalID\":\"" + tt + "\",\"ClientMsgId\":\"" + tt + "\"}";
        //String data="{\"BaseRequest\":{\"Uin\":\""+wxuin+"\",\"Sid\":\""+wxsid+"\",\"Skey\":\""+skey+"\",\"DeviceID\":\"" + deviceId + "\"},"+msg+",\"Scene\":0}";
        //e110854731714634
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxuin + "\",\"Sid\":\"" + wxsid + "\",\"Skey\":\"" + skey + "\",\"DeviceID\":\"" + deviceId + "\"}," + msg + "}";
        hc.contentType = "application/json; charset=UTF-8";
        String initResult = hc.post(baseUrl + "/webwxsendmsg?pass_ticket=" + pass_ticket,
                data);
    }

    /**
     * 发送消息 /webwxstatusnotify?lang=zh_CN&pass_ticket=
     */
    public void statusnotify() {
        long tt = new Date().getTime();
        //tt = tt / 1000;
        tt = tt * 10000;
        tt += 1234;
        String from = fromUser;
        String to = fromUser;
        //String msg = "\"Msg\":{\"Type\":1,\"Content\":\"要发送的消息\",\"FromUserName\":\""+wxuin+"\",\"ToUserName\":\""+wxuin+"\",\"LocalID\":\""+tt+"\",\"ClientMsgId\":\""+tt+"\"}";
        String msg = "\"Type\":3,\",\"FromUserName\":\"" + from + "\",\"ToUserName\":\"" + to + "\",\"ClientMsgId\":\"" + tt + "\"}";
        //String data="{\"BaseRequest\":{\"Uin\":\""+wxuin+"\",\"Sid\":\""+wxsid+"\",\"Skey\":\""+skey+"\",\"DeviceID\":\"" + deviceId + "\"},"+msg+",\"Scene\":0}";
        //e110854731714634
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxuin + "\",\"Sid\":\"" + wxsid + "\",\"Skey\":\"" + skey + "\",\"DeviceID\":\"" + deviceId + "\"}," + msg + "}";
        hc.contentType = "application/json; charset=UTF-8";
        String initResult = hc.post(baseUrl + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + pass_ticket,
                data);
    }

    /**
     * 初始化后可选择性获取好友和群
     */
    public void getFriendAndGroup() {
        long tt = new Date().getTime();
        tt = tt * 10000;
        tt += 1234;
        String from = fromUser;
        String to = fromUser;
        String msg = "\"Type\":3,\",\"FromUserName\":\"" + from + "\",\"ToUserName\":\"" + to + "\",\"ClientMsgId\":\"" + tt + "\"}";
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxuin + "\",\"Sid\":\"" + wxsid + "\",\"Skey\":\"" + skey + "\",\"DeviceID\":\"" + deviceId + "\"}," + msg + "}";
        hc.contentType = "application/json; charset=UTF-8";
        String groupResult = hc.post(baseUrl + "/webwxgetcontact?r=" + System.currentTimeMillis() + "&pass_ticket=" + pass_ticket + "&skey=" + skey, data);
        //System.err.println(groupResult);
        if (!Api.isEmpty(groupResult)) {
            Map<String, Object> resp = JSON.parseObject(groupResult, HashMap.class);
            if (resp != null) {
                List<Map<String, Object>> userList = (List<Map<String, Object>>)resp.get("MemberList");
                if (userList != null) {
                    for (Map<String, Object> tu : userList) {
                        String nm = (String)tu.get("NickName");
                        String um = (String)tu.get("UserName");
                        mapFriendAndGroup.put(nm, um);
                        if (um.startsWith("@@")) {
                            System.out.println("group: " + nm);
                            if (nm.equalsIgnoreCase(kGroupId)) {
                                getGroupMemberList(um);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取群信息
     */
    public void getGroupMemberList(final String groupId) {
        long tt = new Date().getTime();
        //tt = tt / 1000;
        tt = tt * 10000;
        tt += 1234;
        String from = fromUser;
        String to = fromUser;
        String msg = "\"Count\":1,\"List\":[{\"UserName\":\"" + groupId + "\",\"ChatRoomId\":\"\"}]";
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxuin + "\",\"Sid\":\"" + wxsid + "\",\"Skey\":\"" + skey + "\",\"DeviceID\":\"" + deviceId + "\"}," + msg + "}";
        hc.contentType = "application/json; charset=UTF-8";

        String groupResult = hc.post(baseUrl + "/webwxbatchgetcontact?type=ex&r=" + System.currentTimeMillis() + "&pass_ticket=" + pass_ticket, data);
        if (!Api.isEmpty(groupResult)) {
            Map<String, Object> resp = JSON.parseObject(groupResult, HashMap.class);
            if (resp != null) {
                Map<String, Object> contactList = (Map<String, Object>)resp.get("ContactList");
                if (contactList != null) {
                    List<Map<String, Object>> userList = (List<Map<String, Object>>) resp.get("MemberList");
                    if (userList != null) {
                        for (Map<String, Object> tu : userList) {
                            String nm = (String) tu.get("NickName");
                            String um = (String) tu.get("UserName");
                            mapGroupMember.put(nm, um);
                        }
                    }
                }
            }
        }
    }

    /**
     * 同步syncKeys，每次获取到新消息后都要同步
     */
    public void syncKeys(String reslut) {
        initbean = gson.fromJson(reslut, BaseResponeBean.class);
        keyString = "";
        List<SyncKeyEntity.ListEntity> keyList = initbean.getSyncKey().getList();
        for (SyncKeyEntity.ListEntity listEntity : keyList) {
            keyString += listEntity.getKey() + "_" + listEntity.getVal() + "|";
        }
        keyString = keyString.substring(0, keyString.length() - 1);
    }

    //设置各种监听器
    public void setmScanListener(OnScanListener mScanListener) {
        this.mScanListener = mScanListener;
    }

    public void setmNewMsgListener(OnNewMsgListener mNewMsgListener) {
        this.mNewMsgListener = mNewMsgListener;
    }

    public void setmQrCodeListener(OnLoadQrCodeListener mQrCodeListener) {
        this.mQrCodeListener = mQrCodeListener;
    }

    class InitThread extends Thread {

        @Override
        public void run() {
            String data = "{\"BaseRequest\":{\"Uin\":\"" + wxuin + "\",\"Sid\":\"" + wxsid + "\",\"Skey\":\"" + skey + "\",\"DeviceID\":\"" + deviceId + "\"}}";
            hc.contentType = "application/json";
            String initResult = hc.post(baseUrl + "/webwxinit?r=" + System.currentTimeMillis(),
                    data);
            Map<String, Object> resultMap = JSON.parseObject(initResult, HashMap.class);
            if (resultMap != null) {
                Map<String, Object> user = (Map<String, Object>) resultMap.get("User");
                if (user != null) {
                    fromUser = (String) user.get("UserName");
                    nickName = (String) user.get("NickName");
                }
            }
            System.out.println("是否已开启心跳线程");
            if (!isBeat) {
                //同步keys
                syncKeys(initResult);
                //开启心跳线程
                HeartBeatThread heartBeatThread = new HeartBeatThread(WeChatApp.this);
                heartBeatThread.setmNewMsgListener(mNewMsgListener);
                heartBeatThread.start();
                isBeat = true;
            }
        }
    }

}
