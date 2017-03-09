package org.hotwheel.stock.exchange.http;

import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.util.DateUtils;
import org.hotwheel.stock.exchange.context.Category;
import org.mymmsc.api.assembly.Api;

import java.util.Date;
import java.util.TreeMap;

/**
 * 接口参数
 *
 * Created by wangfeng on 2016/11/27.
 */
public class InnerParams {
    // 最大页尺寸
    public final static int MaxPageSize = Integer.MAX_VALUE;
    // 默认初始页码0
    public final static int DefaultPageNo = 0;

    /**
     * 获取每日逾期任务的状态
     * @return
     * @see <url>http://api.jdb-dev.com/billing/debt/getOverdueJobState.html</url>
     */
    public static TreeMap<String, Object> overdueJobState() {
        TreeMap<String, Object> params = new TreeMap<>();
        Date today = new Date();
        params.put("queryDate", Api.toString(today, Category.DateFormat));
        InnerApi.signForState(params);

        return params;
    }

    /**
     * 获取全部债务人ID参数
     *
     * @param startId
     * @param shard
     * @return
     * @see <url>http://api.jdb-dev.com/innerapi/rrc/getOverdueDebtorList.html</url>
     */
    public static TreeMap<String, Object> debtorId(final long startId, final int shard) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("startID", startId);
        params.put("pageSize", Runtime.batchSize);
        params.put("shard", shard);

        InnerApi.sign(params);

        return params;
    }

    /**
     * 获取债务人的产品ID列表
     * @param memberId
     * @param startId
     * @param shard
     * @return
     * @see <url>http://api.jdb-dev.com/innerapi/rrc/getUserProductByDebtorList.html</url>
     */
    public static TreeMap<String, Object> productId(final String memberId, final long startId, final int shard) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("startID", startId);
        params.put("pageSize", 1000);
        params.put("shard", shard);
        params.put("debtorList", memberId);

        InnerApi.sign(params);

        return params;
    }

    /**
     * 获取债务项
     * @return
     * @see <url>http://api.jdb-dev.com/innerapi/rrc/getDebtInfoByProductList.html</url>
     */
    public static TreeMap<String, Object> debtInfo(final String productId, final long startId, final int shard, boolean isAll) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("startID", startId);
        params.put("pageSize", 1000);
        params.put("shard", shard);
        params.put("productList", productId);
        long time = DateUtils.getDateZero().getTime();
        int days = 3;
        if (isAll) {
            days = 1000;
        }
        time -= (Category.MillisecondsOfDay * days);
        params.put("minClearTime", time /1000);

        InnerApi.sign(params);

        return params;
    }

    /**
     * 债务id获取债务还款数据
     * @return
     * @see <url>http://api.jdb-dev.com/innerapi/rrc/getRepayInfoByDebtList.html</url>
     */
    public static TreeMap<String, Object> repayInfo(final String debtId, final long startId, final int shard) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("startID", startId);
        params.put("pageSize", 500);
        params.put("shard", shard);
        params.put("debtList", debtId);

        InnerApi.sign(params);

        return params;
    }

    /**
     * 债务人-逾期productId参数
     *
     * @param memberId
     * @return 返回http接口参数KV map
     * @deprecated
     */
    public static TreeMap<String, Object> overdueProductId(final String memberId) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("pageNo", DefaultPageNo);
        params.put("pageSize", MaxPageSize);
        params.put("debtorID", memberId);

        InnerApi.sign(params);
        return params;
    }

    /**
     * 债务人-结清productId参数
     *
     * @param memberId
     * @return 返回http接口参数KV map
     * @remark 设定三天内结清
     */
    public static TreeMap<String, Object> cleanedProductId(final String memberId) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("pageNo", DefaultPageNo);
        params.put("pageSize", MaxPageSize);
        params.put("debtorID", memberId);
        long now = System.currentTimeMillis();
        long today = now - (now % Category.MillisecondsOfDay);
        long yestoday = today - (Category.MillisecondsOfDay * 3);
        today = today -1;
        params.put("startTime", yestoday);
        params.put("endTime", today);

        InnerApi.sign(params);
        return params;
    }

    /**
     * 债务人-标的详情参数
     *
     * @param productId
     * @return 返回http接口参数KV map
     */
    public static TreeMap<String, Object> bidInfo(final String productId) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("productID", productId);

        InnerApi.sign(params);
        return params;
    }

    /**
     * 债务人-订单详情参数
     *
     * @param productId
     * @return 返回http接口参数KV map
     */
    public static TreeMap<String, Object> orderInfo(final String productId) {
        TreeMap<String, Object> params = new TreeMap<>();
        // 状态值 1计息中（未还，包括逾期） 2部分还款 3已还款 0全部
        params.put("status", 0);
        params.put("pageNo", DefaultPageNo);
        params.put("pageSize", MaxPageSize);
        params.put("productID", productId);
        InnerApi.sign(params);
        return params;
    }

    /**
     * 债务人-还款详情参数
     *
     * @param orderId
     * @return 返回http接口参数KV map
     */
    public static TreeMap<String, Object> repayInfo(final String orderId) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("debtID", orderId);
        InnerApi.sign(params);
        return params;
    }

    public static TreeMap<String, Object> userMapping(final String memberId) {
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("app_key", Runtime.payAppId);
        params.put("jdb_id", memberId);
        params.put("needPtpId","1");
        params.put("track_id", String.valueOf(System.currentTimeMillis()));

        InnerApi.sign(params, null, Runtime.payAppKey);

        return params;
    }

    /**
     * 风控接口-停催标识
     *
     * @param memberId
     * @return
     */
    public static TreeMap<String,Object> hackedInfo(String memberId) {
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("memberId", memberId);
        InnerApi.sign(params, null, Runtime.hackedAppKey);

        return params;
    }

    /**
     * 用户信息
     * @param memberId
     * @return
     */
    public static TreeMap<String,Object> entryInfo(String memberId) {
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("app_id", Runtime.userAppId);
        params.put("app_key", Runtime.userAppKey);
        params.put("user_id_list", memberId);
        params.put("fields", "base,ext,yft_id");
        params.put("result_type", "1");

        return params;
    }

    /**
     * 借贷宝账号和支付账号关系映射
     *
     * @param memberId
     * @return
     */
    public static TreeMap<String, Object> payInfo(String memberId) {
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("appId", Runtime.sfzAppId);
        params.put("userId", memberId);
        params.put("needIdentificationInfo", "1");
        params.put("traceId", String.valueOf(System.currentTimeMillis()));
        InnerApi.signForPay(params, Runtime.sfzAppKey);
        return params;
    }
}
