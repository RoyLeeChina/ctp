package org.hotwheel.ctp.model;

/**
 * EMA指标数据
 *
 * Created by wangfeng on 2017/3/18.
 * @version 1.0.1
 */
public class EMA {
    // 日期
    private String day;
    // 周期1
    private int cycle1 = 12;
    // 周期2
    private int cycle2 = 26;

    private double ema1 = 0.00;
    private double ema2 = 0.00;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getCycle1() {
        return cycle1;
    }

    public void setCycle1(int cycle1) {
        this.cycle1 = cycle1;
    }

    public int getCycle2() {
        return cycle2;
    }

    public void setCycle2(int cycle2) {
        this.cycle2 = cycle2;
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
