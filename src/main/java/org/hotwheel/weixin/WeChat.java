package org.hotwheel.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.util.HttpUtils;
import org.hotwheel.weixin.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信网页版实现
 *
 * Created by wangfeng on 2017/4/2.
 * @version 2.0.1
 */
public class WeChat {
    private static Logger logger = LoggerFactory.getLogger(WeChat.class);

    private final static String kAppId = "wx782c26e4c19acffb";
    private final static String kLoginUrl = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
    private final static String kDeviceId;
    private static String uuid = null;
    private String wxSkey;
    private String wxSid;
    private String wxUin;
    private String syncKey;
    private SyncKeyEntity jsonSyncKey;
    private String pass_ticket;

    //private WxHttpClient httpClient = WxHttpClient.getInstance();
    private WechatListener wechatListener = new WechatListener();
    private int tip = 1;
    private String redirect_uri = null;
    private String base_uri = null;

    private String cookies = null;

    // 自己的用户名
    public String kFromUser;
    // 自己的昵称
    public String kNickName;

    // 好友列表, 昵称-用户名
    private Map<String, String> mapNickToUser = new HashMap<>();
    // 好友列表, 用户名-昵称
    private Map<String, String> mapUserToNick = new HashMap<>();
    private Map<String, String> mapGroupMember = new HashMap<>();
    private Map<String, String> mapGroupFull = new HashMap<>();
    private final static String kGroupId = "股友会";
    //private final static String kGroupId = "CTP内测";
    private final static String kGroupList = "CTP内测|股友会|长江长江，我是黄河";
    public final static long kHeartSleep = 20 * 1000;

    static {
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");

        long randomId = System.nanoTime();
        String str = "" + System.currentTimeMillis() + "" + randomId;
        kDeviceId = "e" + str.substring(2, 17);
        //TypeUtils.compatibleWithJavaBean = true;
    }

    /**
     * 构造方法
     */
    public WeChat() {

    }

    public boolean isRunning() {
        return mapGroupMember.size() > 0;
    }
    /**
     * 匹配字符串
     * @param str
     * @param exp
     * @param defaultValue
     * @return
     */
    public static String match(String str, String exp, final String defaultValue){
        Pattern pattern = Pattern.compile(exp);
        Matcher m = pattern.matcher(str);
        if(m.find()){
            return m.group(1);
        }
        return defaultValue;
    }

    public static String match(String exp, String str) {
        return match(str, exp, "");
    }

    public void start(WeChatContext context) {
        while (!waitForLogin()) {
            Api.sleep(2000);
        }
        login();
        logger.info("微信登录成功");

        logger.info("微信初始化...");
        wxInit();
        logger.info("微信初始化成功");

        logger.info("开启状态通知...");
        openStatusNotify();
        logger.info("开启状态通知成功");

        logger.info("获取联系人...");
        getContact();
        logger.info("获取联系人成功");
        //logger.info("共有 {} 位联系人", Constant.CONTACT.getContactList().size());

        // 监听消息
        wechatListener.start(this, context);
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

    /**
     * 获取UUID
     */
    public String getUuid() {
        String sRet = null;
        String url = "https://login.weixin.qq.com/jslogin";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("appid", kAppId);
        params.put("lang", "zh_CN");
        params.put("fun", "new");
        params.put("_", System.currentTimeMillis());

        String result = HttpUtils.request(url, params);
        if (!Api.isEmpty(result)) {
            String code = match(result, "window.QRLogin.code = (\\d+);", "");
            if (null != code) {
                if (code.equals("200")) {
                    sRet = match(result, "window.QRLogin.uuid = \"(.*)\";", "");
                }
            }
        }
        if (!Api.isEmpty(sRet)) {
            uuid = new String(sRet);
        }
        return uuid;
    }

    /**
     * 下载登录二维码
     * @return
     */
    public byte[] downloadQrCode() {
        byte[] baImage = null;
        String qrCodeUrl = "https://login.weixin.qq.com/qrcode/" + uuid + "?t=webwx&_=";
        URL url;
        DataInputStream bfin = null;
        try {
            url = new URL(qrCodeUrl);
            bfin = new DataInputStream(url.openStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = bfin.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bfin.close();
            baImage = bos.toByteArray();
            bos.close();
        } catch (MalformedURLException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }

        if (baImage == null) {
            logger.info("二维码下载失败");
        } else {
            logger.info("二维码已生成");
        }
        return baImage;
    }

    /**
     * 扫码登录
     * @return
     */
    public boolean waitForLogin() {
        boolean bRet = false;
        TreeMap<String, Object> params = new TreeMap<>();
        // 0-已扫描,1-未扫描
        params.put("tip", tip);
        params.put("uuid", uuid);
        params.put("_", System.currentTimeMillis());

        logger.info("等待登录...");
        String result = HttpUtils.request(kLoginUrl, params);
        if (!Api.isEmpty(result)) {
            String code = match("window.code=(\\d+);", result);
            if (!Api.isEmpty(code)) {
                if (code.equals("201")) {
                    logger.info("成功扫描,请在手机上点击确认以登录");
                    tip = 0;
                } else if (code.equals("200")) {
                    logger.info("正在登录...");
                    String pm = match("window.redirect_uri=\"(\\S+?)\";", result);
                    redirect_uri = pm + "&fun=new";

                    base_uri = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"));

                    logger.debug("redirect_uri={}", redirect_uri);
                    logger.debug("base_uri={}", base_uri);
                } else if (code.equals("408")) {
                    //throw new WechatException("登录超时");
                    logger.error("登录超时");
                } else {
                    logger.info("扫描code={}", code);
                }
                bRet = code.equals("200");
            }
        }

        return bRet;
    }

    /**
     * 重新登录
     */
    public void login() {
        String url = redirect_uri;
        TreeMap<String, Object> params = new TreeMap<>();
        String result = HttpUtils.request(url, params);
        cookies = null;
        //wechatMeta.setCookie(CookieUtil.getCookie(request));
        //request.disconnect();
        if (!Api.isEmpty(result)) {
            wxSkey = match("<skey>(\\S+)</skey>", result);
            wxSid = match("<wxsid>(\\S+)</wxsid>", result);
            wxUin = match("<wxuin>(\\S+)</wxuin>", result);
            pass_ticket = match("<pass_ticket>(\\S+)</pass_ticket>", result);

            logger.debug("skey [{}]", wxSkey);
            logger.debug("wxsid [{}]", wxSid);
            logger.debug("wxuin [{}]", wxUin);
            logger.debug("pass_ticket [{}]", pass_ticket);
            cookies = HttpUtils.getCookies();
        }
    }

    /**
     * 重新加载SyncKey
     * @param objSyncKey
     */
    private void reloadSyncKey(final JSONObject objSyncKey) {
        StringBuffer sb = new StringBuffer();
        try {
            JSONArray list = objSyncKey.getJSONArray("List");
            jsonSyncKey = objSyncKey.toJavaObject(SyncKeyEntity.class);
            for (int i = 0, len = list.size(); i < len; i++) {
                JSONObject item = (JSONObject) list.get(i);
                sb.append("|" + item.getIntValue("Key") + "_" + item.getIntValue("Val"));
            }
            if (sb.length() > 3) {
                syncKey = sb.substring(1);
            }
        } catch (Exception e) {
            //
        }
    }

    /**
     * 初始化
     */
    private void wxInit() {
        String url = base_uri + "/webwxinit";
        url += "?r=" + System.currentTimeMillis();
        url += "&pass_ticket" + Api.urlEncode(pass_ticket);
        url += "&skey" + Api.urlEncode(wxSkey);

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.Uin = wxUin;
        baseRequest.Sid = wxSid;
        baseRequest.Skey = wxSkey;
        baseRequest.DeviceID = kDeviceId;

        TreeMap<String, Object> params = new TreeMap<>();
        params.put("BaseRequest", baseRequest);

        String body = JSON.toJSONString(params);
        String result = HttpUtils.request(url, body);
        if (!Api.isEmpty(result)) {
            try {
                JSONObject resp = JSON.parseObject(result);
                if (resp != null && resp.size() > 0) {
                    JSONObject baseResp = resp.getJSONObject("BaseResponse");
                    if (null != baseResp) {
                        int ret = baseResp.getIntValue("Ret");
                        if (ret == 0) {
                            JSONObject user = resp.getJSONObject("User");
                            if (user != null) {
                                kFromUser = (String) user.get("UserName");
                                kNickName = (String) user.get("NickName");
                                mapNickToUser.put(kNickName, kFromUser);
                                mapUserToNick.put(kFromUser, kNickName);
                            }
                            JSONObject objSyncKey = resp.getJSONObject("SyncKey");
                            reloadSyncKey(objSyncKey);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("解析json失败", e);
            }
        }
    }

    /**
     * 开启状态同步
     */
    private void openStatusNotify() {
        String url = base_uri + "/webwxstatusnotify";
        url += "?lang=zh_CN&pass_ticket=" + Api.urlEncode(pass_ticket);

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.Uin = wxUin;
        baseRequest.Sid = wxSid;
        baseRequest.Skey = wxSkey;
        baseRequest.DeviceID = kDeviceId;

        TreeMap<String, Object> params = new TreeMap<>();
        params.put("BaseRequest", baseRequest);
        params.put("Code", 3);
        params.put("FromUserName", kFromUser);
        params.put("ToUserName", kFromUser);
        params.put("ClientMsgId", genMsgId());

        String body = JSON.toJSONString(params);
        String result = HttpUtils.request(url, body);
        if (!Api.isEmpty(result)) {
            try {
                JSONObject response = JSON.parseObject(result);
                if (response != null) {
                    BaseResponseEntity baseResponse = response.getObject("BaseResponse", BaseResponseEntity.class);
                    if (baseResponse.getRet() != 0) {
                        logger.info("状态通知开启失败，ret：" + baseResponse.getRet());
                    }
                }

            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    /**
     * 群成员缓存昵称的key
     * @param groupId
     * @param memberId
     * @return
     */
    private String keyMemberOfGroup(final String groupId, final String memberId) {
        return String.format("%s|%s", groupId, memberId);
    }

    /**
     * 获取好友昵称
     * @param userId
     * @return
     */
    public String getkNickName(final String userId) {
        return mapUserToNick.get(userId);
    }

    /**
     * 获得用户昵称
     * @param groupId
     * @param userId
     * @return
     */
    public String getkNickName(final String groupId, final String userId) {
        String sRet = null;
        if (Api.isEmpty(groupId)) {
            // 好友
            sRet = mapUserToNick.get(userId);
        } else {
            String groupName = mapUserToNick.get(groupId);
            String userName = getkNickNameByGroupMember(groupId, userId);
            if (!Api.isEmpty(groupId) && !Api.isEmpty(userName)) {
                sRet = userName + '@' + groupName;
            }
        }
        return sRet;

    }


    private void getContact() {
        String url = base_uri + "/webwxgetcontact";
        url += "?r=" + System.currentTimeMillis();
        url += "&pass_ticket=" + Api.urlEncode(pass_ticket);
        url += "&skey=" + Api.urlEncode(wxSkey);
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.Uin = wxUin;
        baseRequest.Sid = wxSid;
        baseRequest.Skey = wxSkey;
        baseRequest.DeviceID = kDeviceId;

        TreeMap<String, Object> params = new TreeMap<>();
        params.put("BaseRequest", baseRequest);
        //params.put("Code", 3);
        //params.put("FromUserName", kFromUser);
        //params.put("ToUserName", kNickName);
        //params.put("ClientMsgId", genMsgId());

        String result = HttpUtils.request(url, JSON.toJSONString(params));

        if (!Api.isEmpty(result)) {
            Map<String, Object> resp = JSON.parseObject(result, HashMap.class);
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
                                if (kGroupList.indexOf(nm)>=0) {
                                    getGroupMemberList(um, nm);
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
    public void getGroupMemberList(final String groupId, final String groupName) {
        String url = base_uri + "/webwxbatchgetcontact";
        url += "?type=ex";
        url += "&r=" + System.currentTimeMillis();
        url += "&pass_ticket=" + Api.urlEncode(pass_ticket);

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.Uin = wxUin;
        baseRequest.Sid = wxSid;
        baseRequest.Skey = wxSkey;
        baseRequest.DeviceID = kDeviceId;

        Map<String, Object> group = new HashMap<>();
        group.put("UserName", groupId);
        group.put("ChatRoomId", "");

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(group);

        TreeMap<String, Object> params = new TreeMap<>();
        params.put("BaseRequest", baseRequest);
        params.put("Count", 1);
        params.put("List", list);

        String groupResult = HttpUtils.request(url, JSON.toJSONString(params));
        if (!Api.isEmpty(groupResult)) {
            Map<String, Object> resp = JSON.parseObject(groupResult, HashMap.class);
            if (resp != null) {
                List<Map<String, Object>> contactList = (List<Map<String, Object>>)resp.get("ContactList");
                if (contactList != null && contactList.size() == 1) {
                    Map<String, Object> contact = contactList.get(0);
                    List<Map<String, Object>> userList = (List<Map<String, Object>>) contact.get("MemberList");
                    if (userList != null) {
                        for (Map<String, Object> tu : userList) {
                            String nickName = (String) tu.get("NickName");
                            String userName = (String) tu.get("UserName");
                            String remarkName = (String)tu.get("RemarkName");
                            if (!Api.isEmpty(remarkName)) {
                                nickName = remarkName;
                            }
                            String key = keyMemberOfGroup(groupId, userName);
                            mapGroupMember.put(key, nickName);
                            mapGroupFull.put(nickName + '@' + groupName, key);

                        }
                    }
                }
            }
        }
    }

    private static final String[] SYNC_HOST = {
            "webpush.weixin.qq.com",
            "webpush2.weixin.qq.com",
            "webpush.wechat.com",
            "webpush1.wechat.com",
            "webpush2.wechat.com",
            "webpush1.wechatapp.com"
    };

    private String urlWebPush = null;

    public void choiceSyncLine() {
        boolean enabled = false;
        for(String syncUrl : SYNC_HOST){
            SyncResponse syncResponse = this.syncCheck(syncUrl);
            if(syncResponse.retcode == 0){
                String url = "https://" + syncUrl + "/cgi-bin/mmwebwx-bin";
                urlWebPush = url;
                logger.info("选择线路：[{}]", syncUrl);
                enabled = true;
                break;
            }
        }
        if(!enabled){
            //throw new WechatException("同步线路不通畅");
        }
    }

    /**
     * 获取指定长度的随机数字组成的字符串
     *
     * @param size
     * @return
     */
    public static String getRandomNumber(int size) {
        String num = "";
        for (int i = 0; i < size; i++) {
            double a = Math.random() * 9;
            a = Math.ceil(a);
            int randomNum = new Double(a).intValue();
            num += randomNum;
        }
        return num;
    }

    /**
     * 检测心跳
     */
    public SyncResponse syncCheck(final String uri) {
        SyncResponse response = new SyncResponse();
        String url;
        if(null == uri){
            url = urlWebPush + "/synccheck";
        } else{
            url = "https://" + uri + "/cgi-bin/mmwebwx-bin/synccheck";
        }

        url += "?skey=" + Api.urlEncode(wxSkey);
        url += "&sid=" + Api.urlEncode(wxSid);
        url += "&uin=" + Api.urlEncode(wxUin);
        url += "&deviceid=" + Api.urlEncode(kDeviceId);
        url += "&synckey=" + Api.urlEncode(syncKey);
        url += "&r=" + System.currentTimeMillis();
        url += "&_=" + System.currentTimeMillis();

        String res = HttpUtils.request(url, "", cookies);
        int[] arr = new int[]{-1, -1};
        if (!Api.isEmpty(res)) {
            String retcode = match("retcode:\"(\\d+)\",", res);
            String selector = match("selector:\"(\\d+)\"}", res);
            if (null != retcode && null != selector) {
                response.retcode = Api.valueOf(int.class, retcode);
                response.selector = Api.valueOf(int.class, selector);
            }
            //cookies = HttpUtils.getCookies();
        }
        return response;
    }

    public WxMessage webwxsync() {
        String url = base_uri + "/webwxsync";

        url += "?sid=" + Api.urlEncode(wxSid);
        url += "&skey=" + Api.urlEncode(wxSkey);
        url += "&pass_ticket=" + Api.urlEncode(pass_ticket);

        WxMessage message = null;

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.Uin = wxUin;
        baseRequest.Sid = wxSid;
        baseRequest.Skey = wxSkey;
        baseRequest.DeviceID = kDeviceId;

        TreeMap<String, Object> params = new TreeMap<>();
        params.put("BaseRequest", baseRequest);
        params.put("SyncKey", jsonSyncKey);
        long tm = System.currentTimeMillis() / 1000;
        params.put("rr", ~tm);

        String result = HttpUtils.request(url, JSON.toJSONString(params), cookies);
        if (!Api.isEmpty(result)) {
            try {
                JSONObject resp = JSON.parseObject(result);
                if (resp != null) {
                    JSONObject objSync = resp.getJSONObject("SyncKey");
                    reloadSyncKey(objSync);
                    JSONArray msgList = resp.getJSONArray("AddMsgList");
                    if (msgList != null && msgList.size() > 0) {
                        message = new WxMessage();
                        List<MessageEntity> list = new ArrayList<>();
                        for (Object obj : msgList) {
                            JSONObject tmp = (JSONObject) obj;
                            list.add(tmp.toJavaObject(MessageEntity.class));
                        }
                        message.setAddMsgList(list);
                        cookies = HttpUtils.getCookies();
                    }
                }
            } catch (Exception e) {
                //
            }
        }
        return message;
    }

    /**
     * 发送 群消息
     * @param nickName
     * @param message
     * @return
     */
    public void sendGroupMessage(String nickName, String message) {
        sendGroupMessage(kGroupId, null, message);
    }

    public String getkNickNameByGroupMember(final String groupId, final String toUserId) {
        String key = keyMemberOfGroup(groupId, toUserId);
        String nickName = mapGroupMember.get(key);
        return nickName;
    }

    /**
     * 发送 群消息
     * @param groupId 群用户名
     * @param toUserId 目标用户名, toUserId为null时群全员
     * @param context 文本消息
     * @return
     */
    public void sendGroupMessage(final String groupId, final String toUserId, final String context) {
        String key = keyMemberOfGroup(groupId, toUserId);
        String nickName = mapGroupMember.get(key);
        if (Api.isEmpty(toUserId)) {
            // 群全员
            nickName = "所有人";
        } else if (Api.isEmpty(nickName) && toUserId.equals(kFromUser)) {
            // 给自己发
            nickName = "王布衣";
        }
        if (!Api.isEmpty(nickName)) {
            StringBuffer sb = new StringBuffer();
            sb.append("@" + nickName);
            String message = sb.toString() + ' ' + context;
            sendMessageByUserId(groupId, message);
        }
    }

    /**
     * 发送 消息
     * @param toUserId 用户名
     * @param message 消息文本
     */
    public void sendMessageByUserId(final String toUserId, final String message) {
        long msgId = genMsgId();
        String fromUserId = kFromUser;
        if (!Api.isEmpty(toUserId)) {
            String url = base_uri + "/webwxsendmsg";
            url += "?pass_ticket=" + Api.urlEncode(pass_ticket);
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.Uin = wxUin;
            baseRequest.Sid = wxSid;
            baseRequest.Skey = wxSkey;
            baseRequest.DeviceID = kDeviceId;

            Map<String, Object> body = new HashMap<>();
            body.put("Type", WxMsgType.TEXT);
            body.put("Content", message);
            body.put("FromUserName", fromUserId);
            body.put("ToUserName", toUserId);
            body.put("LocalID", msgId);
            body.put("ClientMsgId", msgId);

            TreeMap<String, Object> params = new TreeMap<>();
            params.put("BaseRequest", baseRequest);
            params.put("Msg", body);
            HttpUtils.request(url, JSON.toJSONString(params), cookies);
        }
    }

    /**
     * 发送 消息
     * @param fullName 用户昵称
     * @param message 消息文本
     */
    public void sendMessage(final String fullName, final String message) {
        String[] args = fullName.split("@");
        String nickName = args.length > 0 ? args[0] : fullName;
        String groupName = args.length > 1 ? args[1] : "";

        String groupId = null;
        String toUserId = null;
        if (!Api.isEmpty(groupName)) {
            groupId = mapNickToUser.get(groupName);
            String key = mapGroupFull.get(fullName);
            if (!Api.isEmpty(key)) {
                String[] gu = key.split("|");
                if (gu.length == 2) {
                    toUserId = gu[1];
                }
            }
        } else {
            toUserId = mapNickToUser.get(nickName);
        }
        if (!Api.isEmpty(toUserId) && !Api.isEmpty(groupId)) {
            sendGroupMessage(groupId, toUserId, message);
        } else {
            sendMessageByUserId(toUserId, message);
        }
    }
}
