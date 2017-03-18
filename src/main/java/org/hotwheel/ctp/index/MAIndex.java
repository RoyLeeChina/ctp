package org.hotwheel.ctp.index;

/**
 * 移动平均/均线-MA
 *
 * Created by wangfeng on 2017/3/18.
 * @version 1.0.1
 */
public class MAIndex extends AbstractIndex{
    private double ma5;
    private double ma10;
    private double ma20;
    private double ma30;
    private double ma60;
    private double ma120;
    private double ma250;

    public double getMa5() {
        return ma5;
    }

    public void setMa5(double ma5) {
        this.ma5 = ma5;
    }

    public double getMa10() {
        return ma10;
    }

    public void setMa10(double ma10) {
        this.ma10 = ma10;
    }

    public double getMa20() {
        return ma20;
    }

    public void setMa20(double ma20) {
        this.ma20 = ma20;
    }

    public double getMa30() {
        return ma30;
    }

    public void setMa30(double ma30) {
        this.ma30 = ma30;
    }

    public double getMa60() {
        return ma60;
    }

    public void setMa60(double ma60) {
        this.ma60 = ma60;
    }

    public double getMa120() {
        return ma120;
    }

    public void setMa120(double ma120) {
        this.ma120 = ma120;
    }

    public double getMa250() {
        return ma250;
    }

    public void setMa250(double ma250) {
        this.ma250 = ma250;
    }
}
