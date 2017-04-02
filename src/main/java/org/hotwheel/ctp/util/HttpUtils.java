package org.hotwheel.ctp.util;

import org.hotwheel.asio.HttpApi;
import org.hotwheel.io.HttpClient;
import org.hotwheel.io.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * HTTP 工具类
 * Created by wangfeng on 2017/4/3.
 * @version 2.0.1
 */
public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 请求
     *
     * @param url
     * @param headers
     * @param object
     * @return
     */
    public static String request(String url, Map headers, Map object) {
        String result = null;
        long tm = System.currentTimeMillis();
        String params = HttpApi.getParams(object);
        logger.debug("request={}, params={}", url, params);
        HttpClient hc = new HttpClient(url, 10);
        HttpResult hRet = hc.post(headers, object);

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
        }
        return result;
    }
}
