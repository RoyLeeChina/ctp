package org.hotwheel.ctp.util;

import com.alibaba.fastjson.JSON;
import org.hotwheel.asio.HttpApi;
import org.hotwheel.assembly.Api;
import org.hotwheel.assembly.RegExp;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.data.HistoryUtils;
import org.hotwheel.ctp.data.RealTimeUtils;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.model.StockRealTime;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.io.HttpClient;
import org.hotwheel.io.HttpResult;
import org.hotwheel.json.JsonAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 股票接口
 * <p>
 * Created by wangfeng on 2017/3/12.
 */
public final class StockApi {
    private static Logger logger = LoggerFactory.getLogger(StockApi.class);

    // 正则: 完整的股票代码
    private final static String expFullCode = "^s[hz]{1}[0-9]{6}$";
    // 正则: 股票代码
    private final static String expCode = "^[0-9]{6}$";

    /**
     * 价格转字符串
     *
     * @param price
     * @return
     */
    public static String toPrice(final double price) {
        return String.format("%.3f", price);
    }

    /**
     * 补全证券代码
     * @param stockCode
     * @return
     * @since 2.0.1
     */
    private static String fillCode(final String stockCode) {
        String code = null;
        // 如果是纯数字
        if (stockCode.startsWith("6")) {
            code = "sh" + stockCode;
        } else {
            code = "sz" + stockCode;
        }

        return code;
    }

    /**
     * 规范 代码
     *
     * @param fullCode
     * @return 不合规范的代码 返回null
     * @since 2.0.1
     */
    public static String fixCode(final String fullCode) {
        String code = null;
        if (RegExp.valid(fullCode, expFullCode)) {
            // 8位完整的证券代码
            code = fullCode;
        } else if (RegExp.valid(fullCode, expCode)) {
            // 6位代码, 需要补全
            code = fillCode(fullCode);
        }

        return code;
    }

    /**
     * 规范 代码
     *
     * @param fullcode
     * @return 不合规范的代码 返回null
     */
    public static String fixCodeV1(final String fullcode) {
        String code = fullcode.toLowerCase().trim();
        if (code.startsWith("sh") || code.startsWith("sz")) {
            // 代码前缀正确
            code = code.substring(2);
        } else if (code.length() == 6) {
            // 6位数字
        } else {
            // 非sh或sz开头或长度不等于6
            code = null;
        }

        if (!Api.isInteger(code)) {
            // 非数字
            code = null;
        } else {
            // 如果是纯数字
            if (code.startsWith("6")) {
                code = "sh" + code;
            } else {
                code = "sz" + code;
            }
        }
        return code;
    }

    public static <T> T request(String url, Map headers, Map object, Class<T> clazz, Class subClass) {
        T obj = null;
        ActionStatus as = new ActionStatus();
        int errCode = 900;
        String params = HttpApi.getParams(object);
        //System.out.println(params);
        logger.info("request={}, params={}", url, params);
        HttpClient hc = new HttpClient(url, 30);
        HttpResult hRet = hc.post(headers, object);

        logger.info("http-status=[" + hRet.getStatus() + "], body=[" + hRet.getBody() + "], message="
                + hRet.getError());
        if (hRet == null) {
            as.set(errCode + 0, "调用接口失败");
        } else if (hRet.getStatus() >= 400) {
            as.set(errCode + 1, String.format("调用接口失败: %d, %s", hRet.getStatus(), hRet.getError()));
        } else if (hRet.getStatus() != 200) {
            as.set(errCode + 2, String.format("调用接口成功, 但是: %d, %s", hRet.getStatus(), hRet.getError()));
        } else if (hRet.getBody() == null) {
            as.set(errCode + 3, "HTTP接口返回BODY为空");
        } else if (clazz == List.class && subClass != null) {
            //subClass = clazz.getComponentType();
            List list = JSON.parseArray(hRet.getBody(), subClass);
            if (list != null) {
                obj = (T) list.toArray();
            }
        } else if (clazz.isArray() && subClass == null) {
            subClass = clazz.getComponentType();
            List list = JSON.parseArray(hRet.getBody(), subClass);
            if (list == null) {
                as.set(errCode + 11, "接口返回内容不能匹配");
            } else {
                obj = (T) list.toArray();
                as.set(0, "接口成功");
            }
        } else {
            JsonAdapter json = JsonAdapter.parse(hRet.getBody());
            if (json == null) {
                as.set(errCode + 10, "调用接口失败");
            } else {
                try {
                    obj = (T) json.get(clazz, subClass);
                    if (obj == null) {
                        as.set(errCode + 11, "接口返回内容不能匹配");
                    } else {
                        as.set(0, "接口成功");
                    }
                } catch (Exception e) {
                    logger.error("", e);
                } finally {
                    json.close();
                }
            }
        }
        logger.info("request={}, result={}", url, JsonAdapter.get(as, false));
        return obj;
    }

    public static List<StockHistory> getHistory(final String code) {
        List<StockHistory> result;
        //result = request(StockOptions.urlHistory, null, StockOptions.historyParams(code), List.class, StockHistory.class);
        result = HistoryUtils.getKLineDataObjects(code, StockOptions.ONE_DAY);
        return result;
    }

    public static List<StockHistory> getHistory(final String code, final long days) {
        List<StockHistory> result;
        //result = request(StockOptions.urlHistory, null, StockOptions.historyParams(code), List.class, StockHistory.class);
        result = HistoryUtils.getKLineDataObjects(code, StockOptions.ONE_DAY, "" + days);
        return result;
    }

    public static List<StockRealTime> getRealTime(final List<String> listCode) {
        List<StockRealTime> result = null;
        String[] codes = listCode.toArray(new String[]{});
        result = RealTimeUtils.getRealTimeDataObjects(codes);

        return result;
    }

    /**
     * 发送get请求，返回内容字符串
     *
     * @param url         请求urll
     * @param charsetName 字符码
     * @return 响应内容字符串
     */
    public static String httpGet(String url, String charsetName) {
        String result = "";
        HttpClient hc = new HttpClient(url, charsetName);
        HttpResult hRet = hc.post(null, null);
        if (hRet != null && hRet.getStatus() == 200) {
            result = hRet.getBody();
        }
        return result;
    }

    /**
     * 将如"2017-01-07 14:07:35"或"2017-01-07"这样的字符串转换为LocalDateTime对象
     *
     * @param time 时间字符串
     * @return {@link Date}对象
     */
    public static Date string2LocalDateTime(String time) {
        Date result;
        if (time.length() > 10) {
            result = Api.toDate(time, StockOptions.TimeFormat);
        } else {
            result = Api.toDate(time, StockOptions.DateFormat);
        }
        return result;
    }
}
