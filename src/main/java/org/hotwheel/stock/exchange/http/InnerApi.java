package org.hotwheel.stock.exchange.http;

import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.bean.InnerApiResult;
import org.hotwheel.stock.exchange.context.Category;
import org.hotwheel.stock.exchange.context.wind.WindInnerResult;
import org.hotwheel.asio.HttpContext;
import org.hotwheel.util.CollectionUtils;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.context.JsonAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内部API参数通用方法
 *
 * Created by wangfeng on 2016/11/27.
 * @since 3.0.3 增加短信预警功能
 */
public class InnerApi {
    private static Logger logger = LoggerFactory.getLogger(InnerApi.class);

    private static volatile long sn = 0;
    private static volatile long timestamp = 0;

    private static final String kPrefixTraceId = Api.getLocalIp();
    private static AtomicLong atomicLong = new AtomicLong(0);
    private static final long kTraceMax = 1000000L;

    /**
     * 接口请求的跟踪标识
     * @return
     */
    public static String genTraceId() {
        StringBuffer sb = new StringBuffer();
        long tm = System.currentTimeMillis() / Category.MSEC_PER_SEC;
        if (timestamp < tm) {
            timestamp = tm;
            atomicLong.getAndSet(0);
        }
        Date now = new Date();
        sb.append(kPrefixTraceId).append('.');
        sb.append(Api.toString(now, Category.DDL));
        sn = atomicLong.getAndIncrement();
        String tmp = String.valueOf(kTraceMax + sn);
        sb.append(tmp.substring(1));
        return sb.toString();
    }

    /**
     * 预警短信
     * @param message
     */
    public static void notify(final String message) {
        if (Runtime.hasNotify) {
            Runtime.smcApi.notify(message);
        }
    }

    /**
     * 计算速度
     * @param num
     * @param ums
     * @return
     */
    public static long speed(long num, long ums) {
        if (ums == 0) {
            ums = 1;
        }
        BigInteger utotal = BigInteger.valueOf(num);
        utotal = utotal.multiply(BigInteger.valueOf(1000));
        long speed = utotal.divide(BigInteger.valueOf(ums)).longValue();
        return speed;
    }

    /**
     * 按照appKey和MD5Key签名
     * @param params
     * @param appKey
     * @param md5Key
     * @return
     */
    public static String sign(TreeMap<String, Object> params, String appKey, String md5Key) {
        return sign(false, false, params, appKey, md5Key);
    }

    /**
     * 默认为交易验签模式
     * @param params
     * @return
     */
    public static String sign(TreeMap<String, Object> params) {
        return sign(params, Runtime.tradeAppkey, Runtime.tradeMd5Key);
    }

    /**
     * 交易逾期状态验签, 只用md5Key
     * @param params
     * @return
     */
    public static String signForState(TreeMap<String, Object> params) {
        return sign(params, null, Runtime.tradeAppkey);
    }

    /**
     * 债务系统签名
     * @param params
     * @return
     */
    public static String signForAccount(TreeMap<String, Object> params) {
        return sign(params, null, Runtime.accountAppKey);
    }

    /**
     * 验签
     *
     * @param tsUnderline ts是否有下划线
     * @param signUnderline sign字段是否有下划线
     * @param params
     * @param md5Key
     * @return
     */
    public static String sign(final boolean tsUnderline, final boolean signUnderline,
                              final TreeMap<String, Object> params, final String appKey, final String md5Key) {
        String sRet = null;
        if(!CollectionUtils.isEmpty(params)) {
            if (!Api.isEmpty(appKey)) {
                params.put("appKey", appKey);
            }
            String prefix = tsUnderline ? "_" : "";
            long ts = System.currentTimeMillis() / 1000;
            params.put(prefix + "ts", ts);

            StringBuilder preSign = new StringBuilder();
            for (Map.Entry<String, Object> entry: params.entrySet()) {
                preSign.append(Api.toString(entry.getValue()));
                preSign.append('|');
            }
            sRet = Api.md5(preSign.append(md5Key).toString());
            prefix = signUnderline ? "_" : "";
            params.put(prefix + "sign", sRet.toLowerCase());
        }
        return sRet;
    }

    public static <T> T parse(HttpContext context, Class<T> clazz) {
        T obj = null;
        String body = context.getBody().toString();
        int status = context.getStatus();
        String message = "";
        if(status != 200) {
            logger.error("调用接口成功, 但是: {}, {}, from {}", status, message, context.getParams());
        } else if(body == null){
            logger.error("HTTP接口返回BODY为空");
        } else {
            JsonAdapter json = JsonAdapter.parse(body);
            if(json == null) {
                logger.error("调用接口失败");
            } else {
                try {
                    InnerApiResult<T> result = json.get(InnerApiResult.class, clazz);
                    if (result == null) {
                        logger.error("接口返回内容不能匹配");
                    } else if (result.error == null) {
                        //
                    } else if (result.error.returnCode != 0) {
                        logger.error("url={}, params={}, errno={}, message={}", context.getUrl(), context.getParams(), result.error.returnCode, result.error.returnMessage);
                    } else if (result.data == null ) {
                        logger.error("url={}, params={}, errno={}, message={}, but body is empty", context.getUrl(), context.getParams(), result.error.returnCode, result.error.returnMessage);
                    } else if (result.data != null) {
                        obj = result.data;
                    }
                } catch (Exception e) {
                    logger.error("json plus exception: ", e);
                } finally {
                    json.close();
                }
            }
        }
        return obj;
    }

    public static <T> T parseWind(HttpContext context, Class<T> clazz) {
        T obj = null;
        String body = context.getBody().toString();
        int status = context.getStatus();
        String message = "";
        if(status != 200) {
            logger.error("调用接口成功, 但是: {}, {}, from {}", status, message, context.getParams());
        } else if(body == null){
            logger.error("HTTP接口返回BODY为空");
        } else {
            JsonAdapter json = JsonAdapter.parse(body);
            if(json == null) {
                logger.error("调用接口失败");
            } else {
                try {
                    WindInnerResult<T> result = json.get(WindInnerResult.class, clazz);
                    if (result == null) {
                        logger.error("接口返回内容不能匹配");
                    } else if (result.errno != 0) {
                        logger.error("url={}, params={}, errno={}, message={}", context.getUrl(), context.getParams(), result.errno, result.errmsg);
                    } else if (result.data == null ) {
                        logger.error("url={}, params={}, errno={}, message={}, but body is empty", context.getUrl(), context.getParams(), result.errno, result.errmsg);
                    } else if (result.data != null) {
                        obj = result.data;
                    }
                } catch (Exception e) {
                    logger.error("json plus exception: ", e);
                } finally {
                    json.close();
                }
            }
        }
        return obj;
    }


    public static String signForPay(TreeMap<String, Object> params, String appKey) {
        String sRet = null;
        if(!CollectionUtils.isEmpty(params)) {
            boolean tsUnderline = false;
            long ts = System.currentTimeMillis() / 1000;
            params.put("ts", ts);

            StringBuilder preSign = new StringBuilder();
            for (Map.Entry<String, Object> entry: params.entrySet()) {
                preSign.append('|');
                preSign.append(Api.toString(entry.getValue()));
            }
            sRet = Api.md5(appKey + preSign.toString());
            params.put("sign", sRet.toLowerCase());
        }
        return sRet;
    }
}
