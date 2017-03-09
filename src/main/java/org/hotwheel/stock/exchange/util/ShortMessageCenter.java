package org.hotwheel.stock.exchange.util;

import org.hotwheel.stock.exchange.bean.InnerApiResult;
import org.hotwheel.stock.exchange.context.Category;
import org.mymmsc.api.assembly.Api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 消息中心
 *
 * Created by wangfeng on 2016/12/15.
 * @since 3.0.2
 */
public final class ShortMessageCenter {
    private ResourceBundle rb;
    private String envFlag = null;
    private String smscUrl;
    private String username; //D177dd
    private String password; //errOJ04e
    private String traceId;
    private int msgType = 1; // 信息类型，1短信2彩信
    private int notifyTemplayeId;
    private String notifyMobiles;

    private ShortMessageCenter(ResourceBundle resourceBundle) {
        rb = resourceBundle;
        envFlag = rb.getString("env.name");
        smscUrl = rb.getString("smsc.url");
        username = rb.getString("smsc.username");
        password = rb.getString("smsc.password");
        traceId = rb.getString("smsc.traceId");

        String tmpValue = rb.getString("smsc.notify.templateId");
        notifyTemplayeId = Api.valueOf(int.class, tmpValue);
        notifyMobiles = rb.getString("smsc.notify.phones");
    }

    public static ShortMessageCenter getInstance(ResourceBundle resourceBundle) {
        ShortMessageCenter message = new ShortMessageCenter(resourceBundle);
        return message;
    }

    // http://sms.jiedaibao.com/sms/smsController/sendSms?
    // templateId=1&mobiles=18612451879&username=3M5Tln&password=e2ZFo9zD&traceId=1&sendTime=&msgType=1
    // ${timestamp}采集交易批量(${envflag})提醒：${message}
    private boolean sendSms(int templateId, String mobiles, Map<String, Object> userData) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 固定参数
        params.put("username", username);
        params.put("password", password);
        params.put("msgType", msgType);
        params.put("sendTime", "");

        // 跟踪信息
        params.put("traceId", traceId + System.nanoTime());

        // 短信模板
        params.put("templateId", templateId);
        // 手机号码
        params.put("mobiles", mobiles);
        params.putAll(userData);

        InnerApiResult result = HttpApi.request(smscUrl, null, params, InnerApiResult.class);
        return true;
    }

    public boolean notify(String mobiles, String message) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("envflag", envFlag);
        Date now = new Date();
        params.put("timestamp", "[" + Api.toString(now, Category.TimeStamp) + "] ");
        params.put("message", message);
        return sendSms(notifyTemplayeId, mobiles, params);
    }

    public boolean notify(String message) {
        return notify(notifyMobiles, message);
    }
}
