package org.hotwheel.weixin.bean;

/**
 * 消息体
 * Created by wangfeng on 2017/3/28.
 * @version 1.0.2
 */
public class MessageEntity {
    private String MsgId;
    private String FromUserName;
    private String ToUserName;
    private int MsgType;
    private String Content;
    private int Status;
    private int ImgStatus;
    private int CreateTime;
    private int VoiceLength;
    private int PlayLength;
    private String FileName;
    private String FileSize;
    private String MediaId;
    private String Url;
    private int AppMsgType;
    private int StatusNotifyCode;
    private String StatusNotifyUserName;

    public void setMsgId(String MsgId) {
        this.MsgId = MsgId;
    }

    public void setFromUserName(String FromUserName) {
        this.FromUserName = FromUserName;
    }

    public void setToUserName(String ToUserName) {
        this.ToUserName = ToUserName;
    }

    public void setMsgType(int MsgType) {
        this.MsgType = MsgType;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public void setImgStatus(int ImgStatus) {
        this.ImgStatus = ImgStatus;
    }

    public void setCreateTime(int CreateTime) {
        this.CreateTime = CreateTime;
    }

    public void setVoiceLength(int VoiceLength) {
        this.VoiceLength = VoiceLength;
    }

    public void setPlayLength(int PlayLength) {
        this.PlayLength = PlayLength;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public void setFileSize(String FileSize) {
        this.FileSize = FileSize;
    }

    public void setMediaId(String MediaId) {
        this.MediaId = MediaId;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public void setAppMsgType(int AppMsgType) {
        this.AppMsgType = AppMsgType;
    }

    public void setStatusNotifyCode(int StatusNotifyCode) {
        this.StatusNotifyCode = StatusNotifyCode;
    }

    public void setStatusNotifyUserName(String StatusNotifyUserName) {
        this.StatusNotifyUserName = StatusNotifyUserName;
    }

    public String getMsgId() {
        return MsgId;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public String getToUserName() {
        return ToUserName;
    }

    public int getMsgType() {
        return MsgType;
    }

    public String getContent() {
        return Content;
    }

    public int getStatus() {
        return Status;
    }

    public int getImgStatus() {
        return ImgStatus;
    }

    public int getCreateTime() {
        return CreateTime;
    }

    public int getVoiceLength() {
        return VoiceLength;
    }

    public int getPlayLength() {
        return PlayLength;
    }

    public String getFileName() {
        return FileName;
    }

    public String getFileSize() {
        return FileSize;
    }

    public String getMediaId() {
        return MediaId;
    }

    public String getUrl() {
        return Url;
    }

    public int getAppMsgType() {
        return AppMsgType;
    }

    public int getStatusNotifyCode() {
        return StatusNotifyCode;
    }

    public String getStatusNotifyUserName() {
        return StatusNotifyUserName;
    }
}
