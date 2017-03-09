package org.hotwheel.stock.exchange.http;

import org.hotwheel.stock.exchange.bean.InnerApiResult;
import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.context.pay.PayInnerResult;
import org.hotwheel.stock.exchange.context.pay.PaySfzData;
import org.hotwheel.stock.exchange.context.trade.*;
import org.hotwheel.stock.exchange.util.DateUtils;
import org.hotwheel.stock.exchange.util.HttpApi;
import org.hotwheel.util.StringUtils;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.redis.IRedisCallback;
import org.mymmsc.api.redis.RedisApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;

import java.util.*;

/**
 * 交易系统接口
 *
 * Created by wangfeng on 16/8/11.
 */
public class TradeInnerApi{
    /**< 日志记录器 */
    private static Logger logger = LoggerFactory.getLogger(TradeInnerApi.class);

    // 定时任务完成情况
    // 债务人是否获取完毕
    private final static int BT_DEBTORS      = 0x0001;
    private final static int BIT_DEBTORS     = 1;
    //overdueBidsInfo.csv
    private final static int BT_BIDSINFO     = 0x0002;
    private final static int BIT_BIDSINFO    = 2;
    //overdueCreditorOrders.csv
    private final static int BT_ORDER        = 0x0004;
    private final static int BIT_ORDER       = 3;
    //overdueAgreements.csv
    private final static int BT_AGREEMENTS   = 0x0008;
    private final static int BIT_AGREEMENTS  = 4;
    //overdueRepayFlow.csv
    private final static int BT_REPAY        = 0x0010;
    private final static int BIT_REPAY       = 5;
    //overdueEntryUuids.csv
    private final static int BT_ENTRY        = 0x0020;
    private final static int BIT_ENTRY       = 6;
    //creditorInfos.csv
    private final static int BT_CREDITORINFO = 0x0040;
    private final static int BIT_CREDITORINFO= 7;
    //overdueHacked.csv
    private final static int BT_HACKED       = 0x0080;
    private final static int BIT_HACKED      = 8;
    //RRC_PAY.in
    private final static int BT_PAY          = 0x0100;
    private final static int BIT_PAY          = 9;
    //overdueDebtorContacts.csv
    private final static int BT_CONTACTS     = 0x0200;
    private final static int BIT_CONTACTS    = 10;
    //phoneList.csv
    private final static int BT_PHONELIST    = 0x0400;
    private final static int BIT_PHONELIST   = 11;
    //friends.ok

    // 防止重复添加
    public final static List<Integer> kShardList = new ArrayList<Integer>(/*Runtime.kShardMax*/);

    static {

        for (int shard = 0; shard < Runtime.kShardMax; shard++) {
            /*Integer value = shard;
            kShardList.set(shard, value);
            */
            kShardList.add(shard);
        }
    }

    /**
     * 业务批量是否完成
     * @return
     */
    public static boolean isServiceOK() {
        RedisApi redisApi = Runtime.getRedisApi();
        String str = redisApi.get(Runtime.kDebtorTask);
        int batchStatus = Api.valueOf(int.class, str);
        int flags = BT_DEBTORS | BT_BIDSINFO | BT_ORDER | BT_AGREEMENTS | BT_REPAY | BT_ENTRY | BT_CREDITORINFO | BT_HACKED | BT_PAY;
        return (batchStatus & flags) > 0;
    }

    /**
     * 联系人和通讯录批量是否完成
     * @return
     */
    public static boolean isFriendsOK() {
        RedisApi redisApi = Runtime.getRedisApi();
        String str = redisApi.get(Runtime.kDebtorTask);
        int batchStatus = Api.valueOf(int.class, str);
        int flags = BT_CONTACTS | BT_PHONELIST;
        return (batchStatus & flags) > 0;
    }

    /**
     * 获取任务节点的状态
     * @param offset
     * @return
     */
    public static boolean getTaskStatus(final long offset) {
        RedisApi redisApi = Runtime.getRedisApi();
        Date now = new Date();
        String date = Api.toString(now, Runtime.kDateFormatOfRedis);
        final String key = String.format("%s_%s", Runtime.kDebtorTask, date);
        Boolean ret = redisApi.command(key, new IRedisCallback<Boolean>() {
            @Override
            public Boolean exec(ShardedJedis jedis, String key) {
                Boolean lRet = jedis.getbit(key, offset);
                return lRet;
            }
        });
        return ret;
    }

    /**
     * 获取任务节点的状态
     * @param offset
     * @return
     */
    public static void setTaskStatus(final long offset, final boolean status) {
        RedisApi redisApi = Runtime.getRedisApi();
        Date now = new Date();
        String date = Api.toString(now, Runtime.kDateFormatOfRedis);
        final String key = String.format("%s_%s", Runtime.kDebtorTask, date);
        Boolean ret = redisApi.command(key, new IRedisCallback<Boolean>() {
            @Override
            public Boolean exec(ShardedJedis jedis, String key) {
                Boolean lRet = jedis.setbit(key, offset, status);
                jedis.expire(key, DateUtils.getRemainingTime());
                return lRet;
            }
        });
    }

    public static void finishedBidInfo() {
        setTaskStatus(BIT_BIDSINFO, true);
    }

    public static boolean getStatusBidInfo() {
        return getTaskStatus(BIT_BIDSINFO);
    }

    /**
     * 设置 债务人列表 完成状态
     */
    public static void finishedDebtors() {
        setTaskStatus(BIT_DEBTORS, true);
    }

    public static boolean getStatusDebtors() {
        return getTaskStatus(BIT_DEBTORS);
    }

    /**
     * 从redis 取出一个债务人id
     * @return
     */
    public static String popDebtor() {
        RedisApi redisApi = Runtime.getRedisApi();
        String memberId = redisApi.command(Runtime.kDebtorList, new IRedisCallback<String>() {
            @Override
            public String exec(ShardedJedis jedis, String key) {
                return jedis.lpop(key);
            }
        });
        return memberId;
    }

    /**
     * 获取当日交易逾期数据状态
     * @return
     */
    public static OverdueJobState getJobState() {
        TreeMap<String, Object> params = InnerParams.overdueJobState();
        return HttpApi.request(Runtime.urlOverdueJobState + "?htTraceId=ht" + InnerApi.genTraceId(), null,params, OverdueJobState.class);
    }

    /**
     * 分页获取债务人idList
     * @param startId
     * @param shard
     * @return
     * @remark 最大每页500
     */
    public static TradeDebtorList getDebtorList(final long startId, final int shard) {
        TreeMap<String, Object> params = InnerParams.debtorId(startId, shard);
        return HttpApi.innerRequest(Runtime.urlDebtorIdList + "?htTraceId=ht" + InnerApi.genTraceId(), params, TradeDebtorList.class);
    }

    /**
     * 获取产品id列表
     * @param startId
     * @param shard
     * @return
     * @see <url>http://api.jdb-dev.com/innerapi/rrc/getUserProductByDebtorList.html</url>
     */
    public static TradeProductIdList getProductList(String memberId, long startId, int shard) {
        TreeMap<String, Object> params = InnerParams.productId(memberId, startId, shard);
        return HttpApi.innerRequest(Runtime.urlProductIdList + "?htTraceId=ht" + InnerApi.genTraceId(), params, TradeProductIdList.class);
    }

    /**
     * 债务列表
     * @param productId
     * @param startId
     * @param shard
     * @return
     */
    public static DebtInfoList getDebtList(String productId, long startId, int shard, boolean isAll) {
        TreeMap<String, Object> params = InnerParams.debtInfo(productId, startId, shard, isAll);
        return HttpApi.innerRequest(Runtime.urlDebtList + "?htTraceId=ht" + InnerApi.genTraceId(), params, DebtInfoList.class);
    }

    /**
     * 还款明细列表
     * @param debtId
     * @param startId
     * @param shard
     * @return
     */
    public static RepayInfoList getRepayList(String debtId, long startId, int shard) {
        TreeMap<String, Object> params = InnerParams.repayInfo(debtId, startId, shard);
        return HttpApi.innerRequest(Runtime.urlRepayList + "?htTraceId=ht" + InnerApi.genTraceId(), params, RepayInfoList.class);
    }

    /**
     * 批量获取用户信息
     * @param uuids 最大200个
     * @see <url>http://api.jdb-dev.com/passportapi/inner/getulist.html</url>
     */
    public static UserInfo[] getUserList(List<String> uuids) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("app_id", Runtime.userAppId);
        params.put("app_key", Runtime.userAppKey);
        params.put("user_id_list", StringUtils.collectionToCommaDelimitedString(uuids));
        params.put("fields", "base,ext,yft_id");
        params.put("result_type", "1");

        return HttpApi.innerRequest(Runtime.usersListUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, UserInfo[].class);
    }

    /**
     * 获得好友列表(关注)
     *
     * @param memberId
     * @return
     * @see <url>http://api.jdb-dev.com/friendapi/follow_inner/getFollowList.html</url>
     *  value	意义	说明
     *  0	其他	-
     *  1	我的手机通讯录	已废弃
     *  2	借贷宝扫一扫	-
     *  3	手机号搜索	-
     *  4	我/TA的手机通讯录	type=1时为我的，type=2时为TA的
     *  5	活动邀请	-
     *  6	姓名搜索	-
     *  7	可能认识的人	-
     *  9	企业同事	-
     *  10	借贷宝名片	-
     *  11	对发布的借款感兴趣	-
     *  12	好友推荐
     */
    public static TradeContactList getFriendsList(String memberId) {
        TradeContactList contactList = null;
        TreeMap<String, Object> params = new TreeMap<>();
        //params.put("appid", Runtime.friendAppId);
        params.put("memberID", memberId);
        params.put("isOnlyTwoWay", 1);
        params.put("returnType", 4);
        //params.put("source", "0,1");
        InnerApi.sign(true, false, params, null, Runtime.friendAppKey);
        InnerApiResult result = HttpApi.request(Runtime.friendsListUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), null, params, InnerApiResult.class, TradeContactList.class);
        //InnerApiResult result = requestForSignByFriend(true, Runtime.friendsListUrl, params, Runtime.friendAppKey, InnerApiResult.class, TradeContactList.class);
        if(result == null) {
            contactList = null;
        } else if(result.data != null) {
            contactList = (TradeContactList)result.data;
        }
        return contactList;
    }

    /**
     * 从支付网关获取数据
     * @param memberId
     * @return
     */
    public static UserMapping getUserMaping(String memberId) {
        UserMapping userMapping = null;
        TreeMap<String, Object> params = new TreeMap<String, Object>();
        params.put("app_key", Runtime.payAppId);
        params.put("jdb_id", memberId);
        params.put("needPtpId","1");
        params.put("track_id", String.valueOf(System.currentTimeMillis()));

        InnerApiResult<UserMapping> result = requestForSignByMd5key(false, Runtime.payListUrl/*+ "?htTraceId=ht" + InnerApi.genTraceId()*/, params, Runtime.payAppKey, InnerApiResult.class, UserMapping.class);
        if(result != null && result.data != null) {
            userMapping = result.data;
        }

        return userMapping;
    }

    /**
     * 是否下划线标识ts和sign进行post请求
     *
     * @param bUnderline
     * @param url
     * @param params
     * @param secretKey
     * @param clazz
     * @param subClass
     * @param <T>
     * @return
     */
    public static <T> T requestForSignByMd5key(boolean bUnderline, String url, TreeMap<String, Object> params,
                                               String secretKey, Class<T> clazz, Class subClass) {
        if(params != null) {
            String prefix = bUnderline ? "_" : "";
            long ts = System.currentTimeMillis() / 1000;
            params.put(prefix + "ts", ts);

            StringBuilder preSign = new StringBuilder();
            for (Map.Entry<String, Object> entry: params.entrySet()) {
                preSign.append(Api.toString(entry.getValue()));
                preSign.append('|');
            }
            String _sign = Api.md5(preSign.append(secretKey).toString());
            params.put(prefix + "sign", _sign.toLowerCase());
        }
        return HttpApi.request(url, null, params, clazz, subClass);
    }

    public static PhoneInfoList getPhoneList(String memberId) {
        PhoneInfoList phoneInfoList = null;
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("uuids", memberId);

        InnerApiResult result = requestForSignByMd5key(true, Runtime.phoneListUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, Runtime.phoneAppKey, InnerApiResult.class, PhoneInfoList.class);
        if(result != null && result.data != null) {
            phoneInfoList = (PhoneInfoList) result.data;
        }
        return phoneInfoList;
    }

    /**
     * 获取停催标识
     * @param memberId
     * @return
     */
    public static HackedInfo getHackedInfo(String memberId) {
        HackedInfo result = null;
        TreeMap<String, Object> params = InnerParams.hackedInfo(memberId);
        result = HttpApi.windRequest(Runtime.hackedUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, HackedInfo.class);
        //WindInnerResult<HackedInfo> result = requestForSignByFriend(false, Runtime.hackedUrl, params, Runtime.hackedAppKey, WindInnerResult.class, HackedInfo.class);
        if(result != null) {
            result.memberId = memberId;
        }

        return result;
    }

    /**
     * 获取用户身份证号码
     * @param memberId
     * @return
     */
    public static String getIdentity(String memberId) {
        String sRet = null;
        TreeMap<String, Object> params = InnerParams.payInfo(memberId);
        PayInnerResult<PaySfzData> result = HttpApi.request(Runtime.sfzListUrl, null, params, PayInnerResult.class, PaySfzData.class);
        if (result != null || result.data != null) {
            PaySfzData data = result.data;
            if (data.identificationInfo != null && data.identificationInfo.hasIdentified) {
                sRet = data.identificationInfo.identity;
            }
        }
        return sRet;
    }
}
