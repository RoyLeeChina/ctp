package org.hotwheel.stock.util;

import org.hotwheel.json.JsonAdapter;

/**
 * 策略执行情况
 *
 * Created by wangfeng on 2017/3/16.
 */
public class Policy {
    /**
     * 第一支撑位
     */
    private boolean support1;
    /**
     * 第一压力位
     */
    private boolean pressure1;
    /**
     * 第二支撑位
     */
    private boolean support2;
    /**
     * 第二压力位
     */
    private boolean pressure2;

    /**
     * 止损位
     */
    private boolean stop;

    /**
     * 阻力位
     */
    private boolean resistance;

    public boolean isSupport1() {
        return support1;
    }

    public void setSupport1(boolean support1) {
        this.support1 = support1;
    }

    public boolean isPressure1() {
        return pressure1;
    }

    public void setPressure1(boolean pressure1) {
        this.pressure1 = pressure1;
    }

    public boolean isSupport2() {
        return support2;
    }

    public void setSupport2(boolean support2) {
        this.support2 = support2;
    }

    public boolean isPressure2() {
        return pressure2;
    }

    public void setPressure2(boolean pressure2) {
        this.pressure2 = pressure2;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isResistance() {
        return resistance;
    }

    public void setResistance(boolean resistance) {
        this.resistance = resistance;
    }

    @Override
    public String toString() {
        return JsonAdapter.get(this);
    }
}
