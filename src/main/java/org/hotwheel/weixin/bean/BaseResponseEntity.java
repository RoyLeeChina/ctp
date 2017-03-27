package org.hotwheel.weixin.bean;

/**
 * Created by wangfeng on 2017/3/27.
 * @version 1.0.2
 */
public class BaseResponseEntity {
    private int Ret;
    private String ErrMsg;

    public void setRet(int Ret) {
        this.Ret = Ret;
    }

    public void setErrMsg(String ErrMsg) {
        this.ErrMsg = ErrMsg;
    }

    public int getRet() {
        return Ret;
    }

    public String getErrMsg() {
        return ErrMsg;
    }
}
