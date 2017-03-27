package org.hotwheel.weixin.bean;

/**
 * 微信响应
 *
 * Created by wangfeng on 2017/3/26.
 * @version 1.0.2
 */
public class BaseResponeBean {
    /**
     * Ret : 0
     * ErrMsg :
     */

    private BaseResponseEntity BaseResponse;


    private int Count;
    /**
     * Count : 4
     * List : [{"Key":1,"Val":641466310},{"Key":2,"Val":641466407},{"Key":3,"Val":641466406},{"Key":1000,"Val":1458264441}]
     */

    private SyncKeyEntity SyncKey;


    private UserEntity User;
    private String ChatSet;
    private String SKey;
    private int ClientVersion;
    private int SystemTime;
    private int GrayScale;
    private int InviteStartCount;
    private int MPSubscribeMsgCount;
    private int ClickReportInterval;

    public void setBaseResponse(BaseResponseEntity BaseResponse) {
        this.BaseResponse = BaseResponse;
    }

    public void setCount(int Count) {
        this.Count = Count;
    }

    public void setSyncKey(SyncKeyEntity SyncKey) {
        this.SyncKey = SyncKey;
    }

    public void setUser(UserEntity User) {
        this.User = User;
    }

    public void setChatSet(String ChatSet) {
        this.ChatSet = ChatSet;
    }

    public void setSKey(String SKey) {
        this.SKey = SKey;
    }

    public void setClientVersion(int ClientVersion) {
        this.ClientVersion = ClientVersion;
    }

    public void setSystemTime(int SystemTime) {
        this.SystemTime = SystemTime;
    }

    public void setGrayScale(int GrayScale) {
        this.GrayScale = GrayScale;
    }

    public void setInviteStartCount(int InviteStartCount) {
        this.InviteStartCount = InviteStartCount;
    }

    public void setMPSubscribeMsgCount(int MPSubscribeMsgCount) {
        this.MPSubscribeMsgCount = MPSubscribeMsgCount;
    }

    public void setClickReportInterval(int ClickReportInterval) {
        this.ClickReportInterval = ClickReportInterval;
    }

    public BaseResponseEntity getBaseResponse() {
        return BaseResponse;
    }

    public int getCount() {
        return Count;
    }

    public SyncKeyEntity getSyncKey() {
        return SyncKey;
    }

    public UserEntity getUser() {
        return User;
    }

    public String getChatSet() {
        return ChatSet;
    }

    public String getSKey() {
        return SKey;
    }

    public int getClientVersion() {
        return ClientVersion;
    }

    public int getSystemTime() {
        return SystemTime;
    }

    public int getGrayScale() {
        return GrayScale;
    }

    public int getInviteStartCount() {
        return InviteStartCount;
    }

    public int getMPSubscribeMsgCount() {
        return MPSubscribeMsgCount;
    }

    public int getClickReportInterval() {
        return ClickReportInterval;
    }

}
