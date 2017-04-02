package org.hotwheel.weixin;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import org.hotwheel.assembly.Api;
import org.hotwheel.weixin.DownLoadQrCodeThread.OnloadQrCodeFinnishListener;
import org.hotwheel.weixin.HeartBeatThread.OnNewMsgListener;
import org.hotwheel.weixin.WaitScanAndLoginThread.OnScanListener;
import org.hotwheel.weixin.bean.BaseResponeBean;
import org.hotwheel.weixin.bean.SyncKeyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 微信主程序
 *
 * @version 1.0.2
 */
public class WeChat {
    private static Logger logger = LoggerFactory.getLogger(WeChat.class);
    private final static String kGroupId = "股友会";
    boolean isBeat = false;
    //************** 一些变量
    public String uuid;
    public String baseUrl;
    public String wxSkey;
    public String wxSid;
    public String pass_ticket;
    public String wxUin;
    public String syncKey;
    public BaseResponeBean initbean;
    public String wxDeviceId;
    public String kFromUser;
    public String kNickName;

    // 好友列表, 昵称-用户名
    public Map<String, String> mapNickToUser = new HashMap<>();
    // 好友列表, 用户名-昵称
    public Map<String, String> mapUserToNick = new HashMap<>();
    public Map<String, String> mapGroupMember = new HashMap<>();

    //***************监听接口

    public Gson gson = new Gson();
    private WxHttpClient httpClient = WxHttpClient.getInstance();
    private StringSub ss = new StringSub();
    private OnScanListener mScanListener;
    private OnNewMsgListener mNewMsgListener;
    private OnLoadQrCodeListener mQrCodeListener;

    /**
     * 构造方法
     */
    public WeChat() {
        long randomId = System.nanoTime();
        String str = "" + System.currentTimeMillis() + "" + randomId;
        wxDeviceId = "e" + str.substring(2, 17);
    }

    public boolean isRunning() {
        return mapGroupMember.size() > 0;
    }

    private static volatile long sn = 0;
    private static volatile long timestamp = 0;

    private static AtomicLong atomicLong = new AtomicLong(0);

    /**
     * 生成消息id
     * @return
     */
    public static long genMsgId() {
        long lRet = 0;
        long tm = System.currentTimeMillis();
        if (timestamp < tm) {
            timestamp = tm;
            atomicLong.getAndSet(0);
        }
        sn = atomicLong.getAndIncrement();
        lRet = timestamp * 10000 + sn;
        return lRet;
    }

    private TreeMap<String, Object> initParams() {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("Uin", wxUin);
        params.put("Sid", wxSid);
        params.put("Skey", wxSkey);
        params.put("DeviceID", wxDeviceId);

        return params;
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
        String result = httpClient.post("https://login.weixin.qq.com/jslogin",
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
                WaitScanAndLoginThread loginThread = new WaitScanAndLoginThread(uuid, WeChat.this);
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
        for (int i = 0; i < 1; i++) {//开5个线程去初始化
            new InitThread().start();
        }
    }

    public String sendGroupMessage(String nickName, String message) {
        return sendGroupMessage(kGroupId, nickName, message);
    }

    public String sendGroupMessage(String groupId, String userId, String context) {
        String result = "";
        try {
            long tt = genMsgId();
            String from = kFromUser;
            String to = mapNickToUser.get(groupId);
            StringBuffer sb = new StringBuffer();
            sb.append("@");
            if (Api.isEmpty(userId)) {
                sb.append("all");
            } else {
                sb.append(userId.trim());
            }
            String message = sb.toString() + ' ' + context;
            //String msg = "\"Msg\":{\"Type\":1,\"Content\":\"要发送的消息\",\"FromUserName\":\""+wxUin+"\",\"ToUserName\":\""+wxUin+"\",\"LocalID\":\""+tt+"\",\"ClientMsgId\":\""+tt+"\"}";
            String msg = "\"Msg\":{\"Type\":1,\"Content\":\"" + message + "\",\"FromUserName\":\"" + from + "\",\"ToUserName\":\"" + to + "\",\"LocalID\":\"" + tt + "\",\"ClientMsgId\":\"" + tt + "\"}";
            //String data="{\"BaseRequest\":{\"Uin\":\""+wxUin+"\",\"Sid\":\""+wxSid+"\",\"Skey\":\""+wxSkey+"\",\"DeviceID\":\"" + wxDeviceId + "\"},"+msg+",\"Scene\":0}";
            //e110854731714634
            String data = "{\"BaseRequest\":{\"Uin\":\"" + wxUin + "\",\"Sid\":\"" + wxSid + "\",\"Skey\":\"" + wxSkey + "\",\"DeviceID\":\"" + wxDeviceId + "\"}," + msg + "}";
            httpClient.contentType = "application/json; charset=UTF-8";
            result = httpClient.post(baseUrl + "/webwxsendmsg?pass_ticket=" + pass_ticket, data);
        } catch (Exception e) {
            //
        }
        return result;
    }

    /**
     * 发送消息 webwxsendmsg?pass_ticket=xxx
     *
     * @param message
     */
    public void sendMessage(String userId, String message) {
        //statusNotify();
        long tt = genMsgId();
        String from = kFromUser;
        String to = mapNickToUser.get(userId);
        if (!Api.isEmpty(to)) {
            //String msg = "\"Msg\":{\"Type\":1,\"Content\":\"要发送的消息\",\"FromUserName\":\""+wxUin+"\",\"ToUserName\":\""+wxUin+"\",\"LocalID\":\""+tt+"\",\"ClientMsgId\":\""+tt+"\"}";
            String msg = "\"Msg\":{\"Type\":1,\"Content\":\"" + message + "\",\"FromUserName\":\"" + from + "\",\"ToUserName\":\"" + to + "\",\"LocalID\":\"" + tt + "\",\"ClientMsgId\":\"" + tt + "\"}";
            //String data="{\"BaseRequest\":{\"Uin\":\""+wxUin+"\",\"Sid\":\""+wxSid+"\",\"Skey\":\""+wxSkey+"\",\"DeviceID\":\"" + wxDeviceId + "\"},"+msg+",\"Scene\":0}";
            //e110854731714634
            String data = "{\"BaseRequest\":{\"Uin\":\"" + wxUin + "\",\"Sid\":\"" + wxSid + "\",\"Skey\":\"" + wxSkey + "\",\"DeviceID\":\"" + wxDeviceId + "\"}," + msg + "}";
            httpClient.contentType = "application/json; charset=UTF-8";
            String initResult = httpClient.post(baseUrl + "/webwxsendmsg?pass_ticket=" + pass_ticket, data);
        }
    }

    /**
     * 同步消息 /webwxstatusnotify?lang=zh_CN&pass_ticket=
     */
    public void statusNotify() {
        long tt = genMsgId();
        String from = kFromUser;
        String to = kFromUser;
        String msg = "\"Code\":3,\"FromUserName\":\"" + from + "\",\"ToUserName\":\"" + to + "\",\"ClientMsgId\":\"" + tt + "\"";
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxUin + "\",\"Sid\":\"" + wxSid + "\",\"Skey\":\"" + wxSkey + "\",\"DeviceID\":\"" + wxDeviceId + "\"}," + msg + "}";
        httpClient.contentType = "application/json; charset=UTF-8";
        String initResult = httpClient.post(baseUrl + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + pass_ticket,
                data);
    }

    /**
     * 初始化后可选择性获取好友和群
     */
    public void getFriendAndGroup() {
        long tt = new Date().getTime();
        tt = tt * 10000;
        tt += 1234;
        String from = kFromUser;
        String to = kFromUser;
        String msg = "\"Type\":3,\",\"FromUserName\":\"" + from + "\",\"ToUserName\":\"" + to + "\",\"ClientMsgId\":\"" + tt + "\"}";
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxUin + "\",\"Sid\":\"" + wxSid + "\",\"Skey\":\"" + wxSkey + "\",\"DeviceID\":\"" + wxDeviceId + "\"}," + msg + "}";
        httpClient.contentType = "application/json; charset=UTF-8";
        String groupResult = httpClient.post(baseUrl + "/webwxgetcontact?r=" + System.currentTimeMillis() + "&pass_ticket=" + pass_ticket + "&wxSkey=" + wxSkey, data);
        //System.err.println(groupResult);
        if (!Api.isEmpty(groupResult)) {
            Map<String, Object> resp = JSON.parseObject(groupResult, HashMap.class);
            if (resp != null) {
                List<Map<String, Object>> userList = (List<Map<String, Object>>)resp.get("MemberList");
                if (userList != null) {
                    for (Map<String, Object> tu : userList) {
                        String nm = (String)tu.get("NickName");
                        String um = (String)tu.get("UserName");
                        String rm = (String)tu.get("RemarkName");
                        if (!Api.isEmpty(rm)) {
                            nm = rm;
                        }
                        long verifyFlag = (int)tu.get("VerifyFlag");
                        if (verifyFlag != 0) {
                            // # 公众号/服务号
                            logger.info("公众或服务号: {}, {}", nm, um);
                        } else if (um.charAt(0) != '@') {
                            // 特殊账号
                            logger.info("特殊账号: {}", um);
                        } else {
                            mapNickToUser.put(nm, um);
                            mapUserToNick.put(um, nm);
                            if (um.startsWith("@@")) {
                                logger.info("group: " + nm);
                                if (nm.equalsIgnoreCase(kGroupId)) {
                                    getGroupMemberList(um);
                                }
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
        tt = tt * 10000;
        tt += 1234;
        String from = kFromUser;
        String to = kFromUser;
        String msg = "\"Count\":1,\"List\":[{\"UserName\":\"" + groupId + "\",\"ChatRoomId\":\"\"}]";
        String data = "{\"BaseRequest\":{\"Uin\":\"" + wxUin + "\",\"Sid\":\"" + wxSid + "\",\"Skey\":\"" + wxSkey + "\",\"DeviceID\":\"" + wxDeviceId + "\"}," + msg + "}";
        httpClient.contentType = "application/json; charset=UTF-8";

        String groupResult = httpClient.post(baseUrl + "/webwxbatchgetcontact?type=ex&r=" + System.currentTimeMillis() + "&pass_ticket=" + pass_ticket, data);
        if (!Api.isEmpty(groupResult)) {
            Map<String, Object> resp = JSON.parseObject(groupResult, HashMap.class);
            if (resp != null) {
                List<Map<String, Object>> contactList = (List<Map<String, Object>>)resp.get("ContactList");
                if (contactList != null && contactList.size() == 1) {
                    Map<String, Object> contact = contactList.get(0);
                    List<Map<String, Object>> userList = (List<Map<String, Object>>) contact.get("MemberList");
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
    public void syncKeys(final String reslut) {
        BaseResponeBean tmpBean = gson.fromJson(reslut, BaseResponeBean.class);
        if (tmpBean != null) {
            initbean = tmpBean;
        }
        String tmpSyncKey = "";
        if (initbean != null && initbean.getSyncKey() != null) {
            List<SyncKeyEntity.ListEntity> keyList = initbean.getSyncKey().getList();
            if (keyList != null && keyList.size() > 0) {
                for (SyncKeyEntity.ListEntity listEntity : keyList) {
                    tmpSyncKey += "|" + listEntity.getKey() + "_" + listEntity.getVal();
                }
                syncKey = tmpSyncKey.substring(1);
            }
        }
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
            String data = "{\"BaseRequest\":{\"Uin\":\"" + wxUin + "\",\"Sid\":\"" + wxSid + "\",\"Skey\":\"" + wxSkey + "\",\"DeviceID\":\"" + wxDeviceId + "\"}}";
            httpClient.contentType = "application/json";
            String initResult = httpClient.post(baseUrl + "/webwxinit?r=" + System.currentTimeMillis(),
                    data);
            Map<String, Object> resultMap = JSON.parseObject(initResult, HashMap.class);
            if (resultMap != null) {
                Map<String, Object> user = (Map<String, Object>) resultMap.get("User");
                if (user != null) {
                    kFromUser = (String) user.get("UserName");
                    kNickName = (String) user.get("NickName");
                }
                statusNotify();
                getFriendAndGroup();
            }
            logger.info("是否已开启心跳线程");
            if (!isBeat) {
                //同步keys
                syncKeys(initResult);
                //开启心跳线程
                HeartBeatThread heartBeatThread = new HeartBeatThread(WeChat.this);
                heartBeatThread.setmNewMsgListener(mNewMsgListener);
                heartBeatThread.start();
                isBeat = true;
            }
        }
    }

}
