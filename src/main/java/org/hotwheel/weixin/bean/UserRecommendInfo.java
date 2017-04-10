package org.hotwheel.weixin.bean;

/**
 * 名片
 * Created by wangfeng on 2017/4/11.
 */
public class UserRecommendInfo {
    private String UserName;
    private String NickName;
    private String Province;
    private String City;
    private String Signature;
    private String Ticket;
    private int Sex; // 1-男, 2-女
    private int OpCode;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String signature) {
        Signature = signature;
    }

    public String getTicket() {
        return Ticket;
    }

    public void setTicket(String ticket) {
        Ticket = ticket;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int sex) {
        Sex = sex;
    }

    public int getOpCode() {
        return OpCode;
    }

    public void setOpCode(int opCode) {
        OpCode = opCode;
    }
}
