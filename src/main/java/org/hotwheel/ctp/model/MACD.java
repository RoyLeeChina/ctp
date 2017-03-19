package org.hotwheel.ctp.model;

/**
 * MACD, 指数平滑异动移动平均线
 * Created by wangfeng on 2017/3/19.
 * @version 1.0.1
 * @see <url>http://baike.baidu.com/item/MACD指标?fromtitle=MACD&fromid=3334786&type=syn&sefr=cr</url>
 * DIF=EMA(close，12）-EMA（close，26）
 */
public class MACD {
    // 短周期
    private int fast = 12;
    // 长周期
    private int slow = 26;

    private double dif;
    private double dea;
    private double macd;

    public int getFast() {
        return fast;
    }

    public void setFast(int fast) {
        this.fast = fast;
    }

    public int getSlow() {
        return slow;
    }

    public void setSlow(int slow) {
        this.slow = slow;
    }

    public double getDif() {
        return dif;
    }

    public void setDif(double dif) {
        this.dif = dif;
    }

    public double getDea() {
        return dea;
    }

    public void setDea(double dea) {
        this.dea = dea;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }
}
