package org.hotwheel.ctp.model;

import java.util.Date;

/**
 * 监控阀值
 *
 * Created by wangfeng on 2017/3/15.
 * @version 1.0.0
 */
public class StockMonitor {
    /**
     * 检测标志
     */
    private String flag;
    /**
     * 股票代码
     */
    private String code;

    /**
     * 策略日期
     */
    private Date day;

    /**
     * 第一支撑位
     */
    private String support1 = "0.000";
    /**
     * 第一压力位
     */
    private String pressure1 = "0.000";
    /**
     * 第二支撑位
     */
    private String support2 = "0.000";
    /**
     * 第二压力位
     */
    private String pressure2 = "0.000";

    /**
     * 止损位
     */
    private String stop = "0.000";

    /**
     * 阻力位
     */
    private String resistance = "0.000";

    private String remark = "";

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作人
     */
    private String operator;

    private long id;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public String getSupport1() {
        return support1;
    }

    public void setSupport1(String support1) {
        this.support1 = support1;
    }

    public String getPressure1() {
        return pressure1;
    }

    public void setPressure1(String pressure1) {
        this.pressure1 = pressure1;
    }

    public String getSupport2() {
        return support2;
    }

    public void setSupport2(String support2) {
        this.support2 = support2;
    }

    public String getPressure2() {
        return pressure2;
    }

    public void setPressure2(String pressure2) {
        this.pressure2 = pressure2;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getResistance() {
        return resistance;
    }

    public void setResistance(String resistance) {
        this.resistance = resistance;
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
