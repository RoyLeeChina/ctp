package org.hotwheel.ctp.model;

import java.util.Date;

/**
 * 用户策略消息
 * Created by wangfeng on 2017/3/22.
 *
 * @version 1.0.2
 */
public class StockMessage {
    //CHAR    (2)    BINARY DEFAULT '00'     COMMENT '标志($): 00-未发送,01-正常发送,97-丢弃的策略消息'
    private String flag = "00";
    //CHAR    (32)   BINARY NOT NULL         COMMENT '客户ID'
    private String phone = "";
    //CHAR    (32)   BINARY NOT NULL         COMMENT '股票代码'
    private String code = "";
    //VARCHAR (512)  BINARY DEFAULT  ''      COMMENT '策略'
    private String policy = "";
    //CHAR    (20)   BINARY DEFAULT  ''      COMMENT '交易价格'
    private String price = "";
    //TEXT           BINARY                  COMMENT '策略命中备注'
    private String remark = "";
    //DATETIME          DEFAULT NULL         COMMENT '创建时间($)'
    private Date createTime;
    //DATE              DEFAULT NULL         COMMENT '发送日期'
    private Date sendDate;
    //VARCHAR (50)   BINARY DEFAULT 'system' COMMENT '操作人(?$)',
    private String operator = "system";
    private long id;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
