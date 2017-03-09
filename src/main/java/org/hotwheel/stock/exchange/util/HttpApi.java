package org.hotwheel.stock.exchange.util;

import org.hotwheel.stock.exchange.context.ERROR;
import org.hotwheel.stock.exchange.context.wind.WindInnerResult;
import org.hotwheel.stock.exchange.bean.InnerApiResult;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.context.JsonAdapter;
import org.mymmsc.api.io.ActionStatus;
import org.mymmsc.api.io.HttpClient;
import org.mymmsc.api.io.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * http单例接口封装
 *
 * @author wangfeng
 * @date 2016年1月12日 下午1:52:34
 * @since 2.1.0
 */
public final class HttpApi {

	private static Logger logger = LoggerFactory.getLogger(HttpApi.class);

	/**
	 * 组织KV对的字符串
	 * @param params
	 * @return
	 */
	public static String getParams(Map<String, Object> params) {
		String sRet = "{";
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = Api.toString(entry.getValue());
			String str = String.format("&%s=%s", key, value);
			sb.append(str);
		}
		if(sb.length() > 0) {
			sRet += sb.substring(1);
		}
		sRet += "}";
		return sRet;
	}

	/**
	 * HttpClient封装
	 * @param url
	 * @param headers
	 * @param object
	 * @param clazz
	 * @param subClass
	 * @param <T>
	 * @return
	 */
	public static <T> T request(String url, Map<String, String> headers, Map<String, Object> object, Class<T> clazz, Class subClass) {
		T obj = null;
		ActionStatus as = new ActionStatus();
		int errCode = ERROR.SMC;
		String params = getParams(object);
		//logger.debug("request={}, params={}", url, params);
		HttpClient hc = new HttpClient(url, 30);
		HttpResult hRet = hc.post(headers, object);

		logger.debug("request={}, params={}, http-status=[{}], body=[{}], message=[{}]", url, params, hRet.getStatus(), hRet.getBody(), hRet.getError());
		if(hRet == null) {
			as.set(errCode + 0, "调用接口失败");
		} else if(hRet.getStatus() >= 400) {
			as.set(errCode + 1, String.format("调用接口失败: %d, %s", hRet.getStatus(), hRet.getError()));
		} else if(hRet.getStatus() != 200) {
			as.set(errCode + 2, String.format("调用接口成功, 但是: %d, %s", hRet.getStatus(), hRet.getError()));
		} else if(hRet.getBody() == null){
			as.set(errCode + 3, "HTTP接口返回BODY为空");
		} else {
			JsonAdapter json = JsonAdapter.parse(hRet.getBody());
			if(json == null) {
				as.set(errCode + 10, "调用接口失败");
			} else {
				try {
					obj = json.get(clazz, subClass);
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
		logger.debug("request={}, result={}", url, JsonAdapter.get(as, false));
		return obj;
	}

	public static <T> T request(String url, Map<String, String> headers, Map<String, Object> params, Class<T> clazz) {
		return request(url, headers, params, clazz, null);
	}

	public static <T> T request(String url, Map<String, Object> params, Class<T> clazz) {
		return request(url, null, params, clazz, null);
	}

	/**
	 * 内部调用
	 * @param url
	 * @param params
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T innerRequest(String url, final Map<String, Object> params, Class<T> clazz) {
		T tRet = null;
		/*
		Assert.notNull(url);
		long traceId = genTraceId();
		int pos = url.indexOf("?");
		if (pos < 0) {
			url += "?";
		} else {
			url += "&";
		}
		url += "hotWheelTraceId=ht" + traceId;
		*/
		InnerApiResult result = request(url, null, params, InnerApiResult.class, clazz);
		if (result == null) {
			//
		} else if (result.error == null) {
			//
		} else if (result.error.returnCode != 0) {
			logger.error("url={}, params={},errno={}, message={}", url, HttpApi.getParams(params), result.error.returnCode, result.error.returnMessage);
		} else if (result.data == null) {
			//
		} else if (result.data.getClass() == clazz){
			tRet = (T) result.data;
		}

		return tRet;
	}

	/**
	 * 内部调用
	 * @param url
	 * @param params
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T windRequest(String url, final Map<String, Object> params, Class<T> clazz) {
		T tRet = null;
		WindInnerResult result = request(url, null, params, WindInnerResult.class, clazz);
		if (result == null) {
			//
		} else if (result.errno != 0) {
			logger.error("url={}, params={},errno={}, message={}", url, HttpApi.getParams(params), result.errno, result.errmsg);
		} else if (result.data == null) {
			//
		} else if (result.data.getClass() == clazz){
			tRet = (T) result.data;
		}

		return tRet;
	}

	public static <T> T requestForSign(String url, TreeMap<String, Object> params,
									   String appKey, String md5Key,
									   Class<T> clazz, Class subClass) {
		if(params != null) {
			params.put("appKey", appKey);
			long ts = System.currentTimeMillis() / 1000;
			params.put("ts", ts);

			StringBuilder preSign = new StringBuilder();
			for (Map.Entry<String, Object> entry: params.entrySet()) {
				preSign.append(Api.toString(entry.getValue()));
				preSign.append('|');
			}
			String _sign = Api.md5(preSign.append(md5Key).toString());
			params.put("sign", _sign.toLowerCase());
		}
		return request(url, null, params, clazz, subClass);
	}
}
