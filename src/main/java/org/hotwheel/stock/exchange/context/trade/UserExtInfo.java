package org.hotwheel.stock.exchange.context.trade;

import java.util.Date;

/**
 * 用户扩展信息
 *
 * Created by wangfeng on 2016/11/18.
 */
public class UserExtInfo {
    public String age = ""; // 极少部分历史数据没有该值，返回-1，返回-1的一定大于18岁，请业务注意。
    public String sex = ""; // int 性别 1-男，2-女，0-未知
    public String address = ""; // string   所在地
    public String school = ""; // string   毕业学校
    public String education = ""; // string   学历
    public String major = ""; // string   专业
    public String work_area = ""; // string   工作地址
    public String job = ""; // string   职业
    public String identity_no = "000000000000000000"; //"612324********0058",
    public String postal_address = ""; // string   通讯地址
    public String email = ""; //"",
    public Date  birthday; //"1986-11-21",
    public String emergency_name = ""; //"",
    public String emergency_phone_num = ""; //"",
    public String id_encrypt = ""; //"f7b6cf9937dde643a3587e477868859e"
}
