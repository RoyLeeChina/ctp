package org.hotwheel.weixin;

import org.hotwheel.weixin.bean.AddMsgListEntity;

import java.util.List;

/**
 * 消息
 *
 * Created by hua on 2016/3/19.
 * @version 2.0.0
 */
public class MsgBean {
    /**
     * MsgId : 8820250664544778582
     * FromUserName : @56d03149f1b5527c892fea59983532586fb8139c9b0307e2eb4d82f4b6dece2c
     * ToUserName : @8bdd60651b561cfe3403df660d0b4f96
     * MsgType : 1
     * Content : 12345678910
     * Status : 3
     * ImgStatus : 1
     * CreateTime : 1458318602
     * VoiceLength : 0
     * PlayLength : 0
     * FileName :
     * FileSize :
     * MediaId :
     * Url :
     * AppMsgType : 0
     * StatusNotifyCode : 0
     * StatusNotifyUserName :
     * RecommendInfo : {"UserName":"","NickName":"","QQNum":0,"Province":"","City":"","Content":"","Signature":"","Alias":"","Scene":0,"VerifyFlag":0,"AttrStatus":0,"Sex":0,"Ticket":"","OpCode":0}
     * ForwardFlag : 0
     * AppInfo : {"AppID":"","Type":0}
     * HasProductId : 0
     * Ticket :
     * ImgHeight : 0
     * ImgWidth : 0
     * SubMsgType : 0
     * NewMsgId : 8820250664544778582
     */

    private List<AddMsgListEntity> AddMsgList;

    public void setAddMsgList(List<AddMsgListEntity> AddMsgList) {
        this.AddMsgList = AddMsgList;
    }

    public List<AddMsgListEntity> getAddMsgList() {
        return AddMsgList;
    }
}
