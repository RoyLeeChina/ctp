package org.hotwheel.weixin.bean;

/**
 * 微信请求
 * Created by wangfeng on 2017/4/2.
 * @version 2.0.0
 */
public class BaseRequest {
    private String Uin;
    private String Sid;
    private String Skey;
    private String DeviceID;

    public String getUin() {
        return Uin;
    }

    public void setUin(String uin) {
        Uin = uin;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getSkey() {
        return Skey;
    }

    public void setSkey(String skey) {
        Skey = skey;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }
}
