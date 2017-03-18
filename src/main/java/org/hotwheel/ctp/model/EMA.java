package org.hotwheel.ctp.model;

/**
 * EMA指标数据
 * Created by wangfeng on 2017/3/18.
 * @version 1.0.1
 */
public class EMA {
    private int cycle;
    private double ema1;
    private double ema2;

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public double getEma1() {
        return ema1;
    }

    public void setEma1(double ema1) {
        this.ema1 = ema1;
    }

    public double getEma2() {
        return ema2;
    }

    public void setEma2(double ema2) {
        this.ema2 = ema2;
    }
}
