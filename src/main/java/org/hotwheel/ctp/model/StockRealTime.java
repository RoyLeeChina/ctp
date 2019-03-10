package org.hotwheel.ctp.model;

import java.util.Date;

/**
 * 实时数据的对象
 *
 * @version 1.0.0
 */
public class StockRealTime {
    /**
     * 数据类型为指数
     */
    public static final int INDEX = 1;
    /**
     * 数据类型为股票
     */
    public static final int STOCK = 2;

    private int type;
    private String fullCode;
    private String name;
    private double open;
    private double close;
    private double now;
    private double high;
    private double low;
    private double buyPrice;
    private double sellPrice;
    private long volume;
    private double volumePrice;
    private long buy1Num;
    private double buy1Price;
    private long buy2Num;
    private double buy2Price;
    private long buy3Num;
    private double buy3Price;
    private long buy4Num;
    private double buy4Price;
    private long buy5Num;
    private double buy5Price;
    private long sell1Num;
    private double sell1Price;
    private long sell2Num;
    private double sell2Price;
    private long sell3Num;
    private double sell3Price;
    private long sell4Num;
    private double sell4Price;
    private long sell5Num;
    private double sell5Price;
    private Date date;
    private Date time;
    private double riseAndFall;
    private double riseAndFallPercent;

    /**
     * 无参数构造方法
     */
    public StockRealTime() {
        //
    }

    /**
     * 指数的构造方法
     *
     * @param type               数据类型   INDEX
     * @param fullCode           指数代码 如s_sh000001
     * @param name               指数名称
     * @param now                当前价
     * @param volume             成交量
     * @param volumePrice        成就金额
     * @param riseAndFall        涨跌额
     * @param riseAndFallPercent 涨跌百分比
     */
    public StockRealTime(int type, String fullCode, String name, double now, long volume, double volumePrice,
                         double riseAndFall, double riseAndFallPercent) {
        this.type = type;
        this.fullCode = fullCode;
        this.name = name;
        this.now = now;
        this.volume = volume;
        this.volumePrice = volumePrice;
        this.riseAndFall = riseAndFall;
        this.riseAndFallPercent = riseAndFallPercent;
    }

    /**
     * 股票的构造方法
     *
     * @param type        应为STOCK
     * @param fullCode    股票代码 如sz000001
     * @param name        股票名称
     * @param open        今日开盘价
     * @param close       昨日收盘价
     * @param now         当前价
     * @param high        最高价
     * @param low         最低价
     * @param buyPrice    竞买价
     * @param sellPrice   竞卖价
     * @param volume      成交量
     * @param volumePrice 成交总金额
     * @param buy1Num     买一申请数
     * @param buy1Price   买一报价
     * @param buy2Num     买二申请数
     * @param buy2Price   买二报价
     * @param buy3Num     买三申请数
     * @param buy3Price   买三报价
     * @param buy4Num     买四申请数
     * @param buy4Price   买四报价
     * @param buy5Num     买五申请数
     * @param buy5Price   买五报价
     * @param sell1Num    卖一申请数
     * @param sell1Price  卖一报价
     * @param sell2Num    卖二申请数
     * @param sell2Price  卖二报价
     * @param sell3Num    卖三申请数
     * @param sell3Price  卖三报价
     * @param sell4Num    卖四申请数
     * @param sell4Price  卖四报价
     * @param sell5Num    卖五申请数
     * @param sell5Price  卖五报价
     * @param date        日期
     * @param time        时间
     */
    public StockRealTime(int type, String fullCode, String name, double open, double close, double now, double high, double low,
                         double buyPrice, double sellPrice, long volume, double volumePrice,
                         long buy1Num, double buy1Price,
                         long buy2Num, double buy2Price,
                         long buy3Num, double buy3Price,
                         long buy4Num, double buy4Price,
                         long buy5Num, double buy5Price,
                         long sell1Num, double sell1Price,
                         long sell2Num, double sell2Price,
                         long sell3Num, double sell3Price,
                         long sell4Num, double sell4Price,
                         long sell5Num, double sell5Price, Date date, Date time) {
        this.type = type;
        this.fullCode = fullCode;
        this.name = name;
        this.open = open;
        this.close = close;
        this.now = now;
        this.high = high;
        this.low = low;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.volume = volume;
        this.volumePrice = volumePrice;
        this.buy1Num = buy1Num;
        this.buy1Price = buy1Price;
        this.buy2Num = buy2Num;
        this.buy2Price = buy2Price;
        this.buy3Num = buy3Num;
        this.buy3Price = buy3Price;
        this.buy4Num = buy4Num;
        this.buy4Price = buy4Price;
        this.buy5Num = buy5Num;
        this.buy5Price = buy5Price;
        this.sell1Num = sell1Num;
        this.sell1Price = sell1Price;
        this.sell2Num = sell2Num;
        this.sell2Price = sell2Price;
        this.sell3Num = sell3Num;
        this.sell3Price = sell3Price;
        this.sell4Num = sell4Num;
        this.sell4Price = sell4Price;
        this.sell5Num = sell5Num;
        this.sell5Price = sell5Price;
        this.date = date;
        this.time = time;
    }

    /**
     * @return the fullCode
     */
    public String getFullCode() {
        return fullCode;
    }

    /**
     * @param fullCode the fullCode to set
     */
    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }

    /**
     * @return the riseAndFall
     */
    public double getRiseAndFall() {
        return riseAndFall;
    }

    /**
     * @param riseAndFall the riseAndFall to set
     */
    public void setRiseAndFall(double riseAndFall) {
        this.riseAndFall = riseAndFall;
    }

    /**
     * @return the riseAndFallPercent
     */
    public double getRiseAndFallPercent() {
        return riseAndFallPercent;
    }

    /**
     * @param riseAndFallPercent the riseAndFallPercent to set
     */
    public void setRiseAndFallPercent(double riseAndFallPercent) {
        this.riseAndFallPercent = riseAndFallPercent;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the open
     */
    public double getOpen() {
        return open;
    }

    /**
     * @param open the open to set
     */
    public void setOpen(double open) {
        this.open = open;
    }

    /**
     * @return the close
     */
    public double getClose() {
        return close;
    }

    /**
     * @param close the close to set
     */
    public void setClose(double close) {
        this.close = close;
    }

    /**
     * @return the now
     */
    public double getNow() {
        return now;
    }

    /**
     * @param now the now to set
     */
    public void setNow(double now) {
        this.now = now;
    }

    /**
     * @return the high
     */
    public double getHigh() {
        return high;
    }

    /**
     * @param high the high to set
     */
    public void setHigh(double high) {
        this.high = high;
    }

    /**
     * @return the low
     */
    public double getLow() {
        return low;
    }

    /**
     * @param low the low to set
     */
    public void setLow(double low) {
        this.low = low;
    }

    /**
     * @return the buyPrice
     */
    public double getBuyPrice() {
        return buyPrice;
    }

    /**
     * @param buyPrice the buyPrice to set
     */
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    /**
     * @return the sellPrice
     */
    public double getSellPrice() {
        return sellPrice;
    }

    /**
     * @param sellPrice the sellPrice to set
     */
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    /**
     * @return the volume
     */
    public long getVolume() {
        return volume;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(long volume) {
        this.volume = volume;
    }

    /**
     * @return the volumePrice
     */
    public double getVolumePrice() {
        return volumePrice;
    }

    /**
     * @param volumePrice the volumePrice to set
     */
    public void setVolumePrice(double volumePrice) {
        this.volumePrice = volumePrice;
    }

    /**
     * @return the buy1Num
     */
    public long getBuy1Num() {
        return buy1Num;
    }

    /**
     * @param buy1Num the buy1Num to set
     */
    public void setBuy1Num(long buy1Num) {
        this.buy1Num = buy1Num;
    }

    /**
     * @return the buy1Price
     */
    public double getBuy1Price() {
        return buy1Price;
    }

    /**
     * @param buy1Price the buy1Price to set
     */
    public void setBuy1Price(double buy1Price) {
        this.buy1Price = buy1Price;
    }

    /**
     * @return the buy2Num
     */
    public long getBuy2Num() {
        return buy2Num;
    }

    /**
     * @param buy2Num the buy2Num to set
     */
    public void setBuy2Num(long buy2Num) {
        this.buy2Num = buy2Num;
    }

    /**
     * @return the buy2Price
     */
    public double getBuy2Price() {
        return buy2Price;
    }

    /**
     * @param buy2Price the buy2Price to set
     */
    public void setBuy2Price(double buy2Price) {
        this.buy2Price = buy2Price;
    }

    /**
     * @return the buy3Num
     */
    public long getBuy3Num() {
        return buy3Num;
    }

    /**
     * @param buy3Num the buy3Num to set
     */
    public void setBuy3Num(long buy3Num) {
        this.buy3Num = buy3Num;
    }

    /**
     * @return the buy3Price
     */
    public double getBuy3Price() {
        return buy3Price;
    }

    /**
     * @param buy3Price the buy3Price to set
     */
    public void setBuy3Price(double buy3Price) {
        this.buy3Price = buy3Price;
    }

    /**
     * @return the buy4Num
     */
    public long getBuy4Num() {
        return buy4Num;
    }

    /**
     * @param buy4Num the buy4Num to set
     */
    public void setBuy4Num(long buy4Num) {
        this.buy4Num = buy4Num;
    }

    /**
     * @return the buy4Price
     */
    public double getBuy4Price() {
        return buy4Price;
    }

    /**
     * @param buy4Price the buy4Price to set
     */
    public void setBuy4Price(double buy4Price) {
        this.buy4Price = buy4Price;
    }

    /**
     * @return the buy5Num
     */
    public long getBuy5Num() {
        return buy5Num;
    }

    /**
     * @param buy5Num the buy5Num to set
     */
    public void setBuy5Num(long buy5Num) {
        this.buy5Num = buy5Num;
    }

    /**
     * @return the buy5Price
     */
    public double getBuy5Price() {
        return buy5Price;
    }

    /**
     * @param buy5Price the buy5Price to set
     */
    public void setBuy5Price(double buy5Price) {
        this.buy5Price = buy5Price;
    }

    /**
     * @return the sell1Num
     */
    public long getSell1Num() {
        return sell1Num;
    }

    /**
     * @param sell1Num the sell1Num to set
     */
    public void setSell1Num(long sell1Num) {
        this.sell1Num = sell1Num;
    }

    /**
     * @return the sell1Price
     */
    public double getSell1Price() {
        return sell1Price;
    }

    /**
     * @param sell1Price the sell1Price to set
     */
    public void setSell1Price(double sell1Price) {
        this.sell1Price = sell1Price;
    }

    /**
     * @return the sell2Num
     */
    public long getSell2Num() {
        return sell2Num;
    }

    /**
     * @param sell2Num the sell2Num to set
     */
    public void setSell2Num(long sell2Num) {
        this.sell2Num = sell2Num;
    }

    /**
     * @return the sell2Price
     */
    public double getSell2Price() {
        return sell2Price;
    }

    /**
     * @param sell2Price the sell2Price to set
     */
    public void setSell2Price(double sell2Price) {
        this.sell2Price = sell2Price;
    }

    /**
     * @return the sell3Num
     */
    public long getSell3Num() {
        return sell3Num;
    }

    /**
     * @param sell3Num the sell3Num to set
     */
    public void setSell3Num(long sell3Num) {
        this.sell3Num = sell3Num;
    }

    /**
     * @return the sell3Price
     */
    public double getSell3Price() {
        return sell3Price;
    }

    /**
     * @param sell3Price the sell3Price to set
     */
    public void setSell3Price(double sell3Price) {
        this.sell3Price = sell3Price;
    }

    /**
     * @return the sell4Num
     */
    public long getSell4Num() {
        return sell4Num;
    }

    /**
     * @param sell4Num the sell4Num to set
     */
    public void setSell4Num(long sell4Num) {
        this.sell4Num = sell4Num;
    }

    /**
     * @return the sell4Price
     */
    public double getSell4Price() {
        return sell4Price;
    }

    /**
     * @param sell4Price the sell4Price to set
     */
    public void setSell4Price(double sell4Price) {
        this.sell4Price = sell4Price;
    }

    /**
     * @return the sell5Num
     */
    public long getSell5Num() {
        return sell5Num;
    }

    /**
     * @param sell5Num the sell5Num to set
     */
    public void setSell5Num(long sell5Num) {
        this.sell5Num = sell5Num;
    }

    /**
     * @return the sell5Price
     */
    public double getSell5Price() {
        return sell5Price;
    }

    /**
     * @param sell5Price the sell5Price to set
     */
    public void setSell5Price(double sell5Price) {
        this.sell5Price = sell5Price;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }


}
