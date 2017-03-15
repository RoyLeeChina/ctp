package org.hotwheel.stock;

import java.util.Map;
import java.util.TreeMap;

/**
 * 股票数据常量
 * Created by wangfeng on 2017/3/12.
 * @version 1.0.0
 */
public final class StockOptions {
    /** number of microseconds per second */
    public final static long USEC_PER_SEC = 1000000L;
    /** number of milliseconds per second */
    public final static long MSEC_PER_SEC = 1000L;
    /** 一天的秒数 */
    public final static long SecondOfDay = 24L * 60L * 60L;
    /** 一天的毫秒数 */
    public final static long MillisecondsOfDay = SecondOfDay * MSEC_PER_SEC;

    /**
     * 实时数据间隔时间, 单位毫秒
     */
    public final static long kRealTimenterval = 5 * 1000;

    /**
     * 五分钟, 字符串"5"
     */
    public static final String FIVE_MINUTES = "5";

    /**
     * 十五分钟, 字符串"15"
     */
    public static final String FIFTEEN_MINUTES = "15";

    /**
     * 三十分钟, 字符串"30"
     */
    public static final String THIRTY_MINUTES = "30";

    /**
     * 一小时, 字符串"60"
     */
    public static final String ONE_HOUR = "60";

    /**
     * 一天, 字符串"240"
     */
    public static final String ONE_DAY = "240";

    /**
     * 一周, 字符串"1680"
     */
    public static final String ONE_WEEK = "1680";

    /**
     * 全部数据, 字符串"1000000"
     */
    public static final String DEFAULT_DATALEN = "1000000";

    /**
     * 正常状态, 字符串"01"
     */
    public final static String kNormalState = "01";

    /**
     * 时间格式
     */
    public final static String TimeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式
     */
    public final static String DateFormat = "yyyy-MM-dd";
    public final static String DateFormat2 = "yyyyMMdd";

    public final static String urlHistory = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData";

    public static Map<String, Object> historyParams(final String code) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("symbol", code);
        params.put("scale", ONE_DAY);
        params.put("datalen", DEFAULT_DATALEN);

        return params;
    }
}
