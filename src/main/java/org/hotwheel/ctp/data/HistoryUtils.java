package org.hotwheel.ctp.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.util.StockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 对新浪财经查询股票历史数据API的封装<br>
 * API：http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData<br>
 * 封装的参数：symbol，scale，datalen<br>
 *
 * @version 1.0.0
 */
public class HistoryUtils {
    private final static Logger logger = LoggerFactory.getLogger(HistoryUtils.class);

    /**
     * 获取股票历史数据<br>
     * 例子：<br>
     * String result = HistoryData.getKLineData("sz000001", HistoryData.FIVE_MINUTES, "20");<br>
     *
     * @param code    股票代码
     * @param scale   时间跨度  可选[FIVE_MINUTES,FIFTEEN_MINUTES,THIRTY_MINUTES,ONE_HOUR,ONE_DAY,ONE_WEEK]
     * @param datalen 数据量
     * @return json字符串
     */
    public static String getKLineData(String code, String scale, String datalen) {
        String url = createURL(code, scale, datalen);
        return StockApi.httpGet(url, "UTF-8");
    }

    /**
     * 获取股票历史数据
     * 例子：<br>
     * List&lt;HistoryDataPOJO&gt; result = HistoryData.getKLineDataObjects("sz000001", HistoryData.FIVE_MINUTES, "20");<br>
     *
     * @param code    股票代码
     * @param scale   时间跨度 可选[FIVE_MINUTES,FIFTEEN_MINUTES,THIRTY_MINUTES,ONE_HOUR,ONE_DAY,ONE_WEEK]
     * @param datalen 数据量
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> getKLineDataObjects(String code, String scale, String datalen) {
        List<StockHistory> result = new ArrayList<>();
        try {
            String jsonText = getKLineData(code, scale, datalen);
            JSONArray jsonarray = JSON.parseArray(jsonText);
            int lengh = jsonarray.size();
            for (int i = 0; i < lengh; i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String dayString = jsonobject.getString("day");
                Date day = StockApi.string2LocalDateTime(dayString);
                double open = Double.parseDouble(jsonobject.getString("open"));
                double high = Double.parseDouble(jsonobject.getString("high"));
                double low = Double.parseDouble(jsonobject.getString("low"));
                double close = Double.parseDouble(jsonobject.getString("close"));
                double volume = Double.parseDouble(jsonobject.getString("volume"));
                double MA5 = jsonobject.getDoubleValue("ma_price5");
                double MA5Volume = jsonobject.getDoubleValue("ma_volume5");
                double MA10 = jsonobject.getDoubleValue("ma_price10");
                double MA10Volume = jsonobject.getDoubleValue("ma_volume10");
                double MA30 = jsonobject.getDoubleValue("ma_price30");
                double MA30Volume = jsonobject.getDoubleValue("ma_volume30");
                StockHistory pojo;
                if (!Double.isNaN(MA30) && !Double.isNaN(MA30Volume)) {
                    pojo = new StockHistory(day, open, high, low, close, volume, MA5, MA5Volume, MA10, MA10Volume, MA30, MA30Volume);
                } else if (!Double.isNaN(MA10) && !Double.isNaN(MA10Volume)) {
                    pojo = new StockHistory(day, open, high, low, close, volume, MA5, MA5Volume, MA10, MA10Volume);
                } else if (!Double.isNaN(MA5) && !Double.isNaN(MA5Volume)) {
                    pojo = new StockHistory(day, open, high, low, close, volume, MA5, MA5Volume);
                } else {
                    pojo = new StockHistory(day, open, high, low, close, volume);
                }
                pojo.setCode(code);
                result.add(pojo);
            }
        } catch (Exception e) {
            logger.error("解析日线历史数据异常", e);
        }
        return result;
    }

    private static String createURL(String code, String scale, String datalen) {
        String url = String.format("http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?"
                + "symbol=%s&scale=%s&datalen=%s", code, scale, datalen);
        return url;
    }

    /**
     * 获取股票历史数据<br>
     * 实际调用getKLineData(String code, String scale, HistoryData.DEFAULT_DATALEN)<br>
     * 例子：<br>
     * String result = HistoryData.getKLineData("sz000001", HistoryData.FIVE_MINUTES);<br>
     *
     * @param code  股票代码
     * @param scale 时间跨度 可选[FIVE_MINUTES,FIFTEEN_MINUTES,THIRTY_MINUTES,ONE_HOUR,ONE_DAY,ONE_WEEK]
     * @return json字符串
     */
    public static String getKLineData(String code, String scale) {
        return getKLineData(code, scale, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.FIVE_MINUTES, datalen)
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return json字符串
     */
    public static String get5MKlineData(String code, String datalen) {
        return getKLineData(code, StockOptions.FIVE_MINUTES, datalen);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.FIVE_MINUTES, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return json字符串
     */
    public static String get5MKlineData(String code) {
        return getKLineData(code, StockOptions.FIVE_MINUTES, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.FIFTEEN_MINUTES, datalen)
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return json字符串
     */
    public static String get15MKlineData(String code, String datalen) {
        return getKLineData(code, StockOptions.FIFTEEN_MINUTES, datalen);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.FIFTEEN_MINUTES, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return json字符串
     */
    public static String get15MKlineData(String code) {
        return getKLineData(code, StockOptions.FIFTEEN_MINUTES, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.THIRTY_MINUTES, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return json字符串
     */
    public static String get30MKlineData(String code, String datalen) {
        return getKLineData(code, StockOptions.THIRTY_MINUTES, datalen);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.THIRTY_MINUTES, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return json字符串
     */
    public static String get30MKlineData(String code) {
        return getKLineData(code, StockOptions.THIRTY_MINUTES, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.ONE_HOUR, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return json字符串
     */
    public static String get1HKlineData(String code, String datalen) {
        return getKLineData(code, StockOptions.ONE_HOUR, datalen);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.ONE_HOUR, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return json字符串
     */
    public static String get1HKlineData(String code) {
        return getKLineData(code, StockOptions.ONE_HOUR, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.ONE_DAY, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return json字符串
     */
    public static String get1DKlineData(String code, String datalen) {
        return getKLineData(code, StockOptions.ONE_DAY, datalen);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.ONE_DAY, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return json字符串
     */
    public static String get1DKlineData(String code) {
        return getKLineData(code, StockOptions.ONE_DAY, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.ONE_WEEK, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return json字符串
     */
    public static String get1WKlineData(String code, String datalen) {
        return getKLineData(code, StockOptions.ONE_WEEK, datalen);
    }

    /**
     * 实际调用：getKLineData(code, HistoryData.ONE_WEEK, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return json字符串
     */
    public static String get1WKlineData(String code) {
        return getKLineData(code, StockOptions.ONE_WEEK, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineDataObjects(code, scale,HistoryData.DEFAULT_DATALEN);
     *
     * @param code  股票代码
     * @param scale 时间跨度
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> getKLineDataObjects(String code, String scale) {
        return getKLineDataObjects(code, scale, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.FIVE_MINUTES, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get5MKLineDataObjects(String code, String datalen) {
        return getKLineDataObjects(code, StockOptions.FIVE_MINUTES, datalen);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.FIVE_MINUTES, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get5MKlineDataObjects(String code) {
        return getKLineDataObjects(code, StockOptions.FIVE_MINUTES, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.FIFTEEN_MINUTES, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get15MKlineDataObjects(String code, String datalen) {
        return getKLineDataObjects(code, StockOptions.FIFTEEN_MINUTES, datalen);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.FIFTEEN_MINUTES, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get15MKlineDataObjects(String code) {
        return getKLineDataObjects(code, StockOptions.FIFTEEN_MINUTES, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.THIRTY_MINUTES, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get30MKlineDataObjects(String code, String datalen) {
        return getKLineDataObjects(code, StockOptions.THIRTY_MINUTES, datalen);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.THIRTY_MINUTES, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get30MKlineDataObjects(String code) {
        return getKLineDataObjects(code, StockOptions.THIRTY_MINUTES, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.ONE_HOUR, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get1HKlineDataObjects(String code, String datalen) {
        return getKLineDataObjects(code, StockOptions.ONE_HOUR, datalen);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.ONE_HOUR, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get1HKlineDataObjects(String code) {
        return getKLineDataObjects(code, StockOptions.ONE_HOUR, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.ONE_DAY, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get1DKlineDataObjects(String code, String datalen) {
        return getKLineDataObjects(code, StockOptions.ONE_DAY, datalen);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.ONE_DAY, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get1DKlineDataObjects(String code) {
        return getKLineDataObjects(code, StockOptions.ONE_DAY, StockOptions.DEFAULT_DATALEN);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.ONE_WEEK, datalen);
     *
     * @param code    股票代码
     * @param datalen 数据量
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get1WKlineDataObjects(String code, String datalen) {
        return getKLineDataObjects(code, StockOptions.ONE_WEEK, datalen);
    }

    /**
     * 实际调用：getKLineDataObjects(code, HistoryData.ONE_WEEK, HistoryData.DEFAULT_DATALEN);
     *
     * @param code 股票代码
     * @return 一个{@link List}，里面是{@link StockHistory}对象
     */
    public static List<StockHistory> get1WKlineDataObjects(String code) {
        return getKLineDataObjects(code, StockOptions.ONE_WEEK, StockOptions.DEFAULT_DATALEN);
    }
}
