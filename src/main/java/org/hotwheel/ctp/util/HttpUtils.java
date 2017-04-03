package org.hotwheel.ctp.util;

import org.hotwheel.asio.HttpApi;
import org.hotwheel.assembly.Api;
import org.hotwheel.io.HttpClient;
import org.hotwheel.io.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 工具类
 * Created by wangfeng on 2017/4/3.
 * @version 2.0.1
 */
public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private final static String kUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

    private static String cookies = null;

    public static String getCookies() {
        return cookies;
    }

    /**
     * 请求
     *
     * @param url
     * @param headers
     * @param data
     * @return
     */
    public static String request(String url, Map headers, Map data) {
        String result = null;
        String params = HttpApi.getParams(data);
        HttpClient hc = new HttpClient(url, 10);
        HttpResult hRet = hc.post(headers, data);

        logger.debug("request={}, params={}, http-status=[{}], body=[{}], message=[{}], acrossTime={}", url, params, hRet.getStatus(), hRet.getBody(), hRet.getError(), hRet.getAcrossTime());
        if (hRet == null) {
            //as.set(errCode + 0, "调用接口失败");
        } else if (hRet.getStatus() >= 400) {
            //as.set(errCode + 1, String.format("调用接口失败: %d, %s", hRet.getStatus(), hRet.getError()));
        } else if (hRet.getStatus() != 200) {
            //as.set(errCode + 2, String.format("调用接口成功, 但是: %d, %s", hRet.getStatus(), hRet.getError()));
        } else if (hRet.getBody() == null) {
            //as.set(errCode + 3, "HTTP接口返回BODY为空");
        } else {
            result = hRet.getBody();
            cookies = hRet.getCookies();
        }
        return result;
    }

    /**
     * 请求
     * @param url
     * @param data
     * @return
     */
    public static String request(final String url, Map data) {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("User-agent", kUserAgent);
        return request(url, headers, data);
    }

    /**
     * 请求
     * @param url
     * @param data
     * @return
     */
    public static String request(final String url, final String data, final String cookie) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-agent", kUserAgent);
        headers.put("Content-Type", "application/json; charset=utf-8");
        if (!Api.isEmpty(cookie)) {
            headers.put("Cookie", cookie);
        }
        String result = null;
        HttpClient hc = new HttpClient(url, 10);
        HttpResult hRet = hc.post(headers, data);

        logger.debug("request={}, params=[{}], http-status=[{}], body=[{}], message=[{}], acrossTime={}", url, data, hRet.getStatus(), hRet.getBody(), hRet.getError(), hRet.getAcrossTime());
        if (hRet == null) {
            //as.set(errCode + 0, "调用接口失败");
        } else if (hRet.getStatus() >= 400) {
            //as.set(errCode + 1, String.format("调用接口失败: %d, %s", hRet.getStatus(), hRet.getError()));
        } else if (hRet.getStatus() != 200) {
            //as.set(errCode + 2, String.format("调用接口成功, 但是: %d, %s", hRet.getStatus(), hRet.getError()));
        } else if (hRet.getBody() == null) {
            //as.set(errCode + 3, "HTTP接口返回BODY为空");
        } else {
            result = hRet.getBody();
            cookies = hRet.getCookies();
        }
        return result;
    }

    public static String request(final String url, final String data) {
        return request(url, data, null);
    }
}
