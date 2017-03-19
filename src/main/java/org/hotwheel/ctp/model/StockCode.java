package org.hotwheel.ctp.model;

/**
 * 股票代码数据
 *
 * Created by wangfeng on 2017/3/19.
 * @version 1.0.1
 */
public class StockCode {
    private String flag; // CHAR    (2)    BINARY DEFAULT '00' COMMENT '标志($): 00-禁止检测,01-正常检测',
    private String code; // CHAR    (32)   BINARY NOT NULL     COMMENT '股票代码',
    private String full_code; // CHAR    (32)   BINARY NOT NULL     COMMENT '完整的股票代码',
    private String name; // CHAR    (128)  BINARY NOT NULL     COMMENT '股票名称(?$)',
    private String operator;// VARCHAR (50)   BINARY NOT NULL     COMMENT '操作人(?$)',
    private String createTime; // DATETIME          DEFAULT NULL     COMMENT '创建时间($)',
    private long id;// INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY  /* 记录标号 */

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

    public String getFull_code() {
        return full_code;
    }

    public void setFull_code(String full_code) {
        this.full_code = full_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
