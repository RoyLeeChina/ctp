package org.hotwheel.stock.exchange.http;

import com.google.common.collect.Lists;
import org.hotwheel.stock.exchange.bean.InnerApiResult;
import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.context.jdb.v2.BidRequest;
import org.hotwheel.stock.exchange.context.jdb.v2.OrderRequest;
import org.hotwheel.stock.exchange.context.jdb.v2.RepayRequest;
import org.hotwheel.stock.exchange.context.trade.UserInfo;
import org.hotwheel.stock.exchange.model.DHBid;
import org.hotwheel.stock.exchange.model.DHOrder;
import org.hotwheel.stock.exchange.model.DHRepay;
import org.hotwheel.stock.exchange.util.DateUtils;
import org.hotwheel.stock.exchange.util.HttpApi;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.context.JsonAdapter;
import org.mymmsc.api.redis.IRedisCallback;
import org.mymmsc.api.redis.RedisApi;
import redis.clients.jedis.ShardedJedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * 债务系统接口
 * Created by wangfeng on 2017/1/9.
 */
public class DebtApi {

    private final static RedisApi redisApi = Runtime.getRedisApi();

    public static void init() {
        Date now = new Date();
        String date = Api.toString(now, Runtime.kDateFormatOfRedis);
        final String key = String.format("%s_%s", Runtime.kDebtorTask, date);
        redisApi.delete(key);
        redisApi.delete(Runtime.kDebtorList);
        //redisApi.delete(Runtime.kDebtorBak);
    }

    public static void pushDebtFinished() {
        redisApi.set(Runtime.kDebtFinished, DateUtils.getRemainingTime(), "1");
    }

    public static void push(List<String> uuids) {
        for (final String debtor : uuids) {
            long ret = redisApi.command(Runtime.kDebtorList, new IRedisCallback<Long>() {
                @Override
                public Long exec(ShardedJedis jedis, String key) {
                    jedis.rpush(Runtime.kDebtorBak, debtor);
                    jedis.expire(Runtime.kDebtorBak, DateUtils.getRemainingTime());
                    long lRet = jedis.rpush(Runtime.kDebtorList, debtor);
                    jedis.expire(Runtime.kDebtorList, DateUtils.getRemainingTime());
                    return lRet;
                }
            });
        }
    }

    public static void push(String[] uuids) {
        push(Lists.newArrayList(uuids));
    }

    /**
     * 提交标的
     * @param bid
     * @return
     */
    public static InnerApiResult submit(DHBid bid) {
        BidRequest req = new BidRequest();
        req.bidDtos = new ArrayList<>();
        req.bidDtos.add(bid);
        String json = JsonAdapter.get(req, false);
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("bidWraperDto", json);

        InnerApiResult result = HttpApi.request(Runtime.debtBidUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, InnerApiResult.class);
        return result;
    }

    public static InnerApiResult submit(DHOrder order) {
        OrderRequest req = new OrderRequest();
        req.debtDtos = new ArrayList<>();
        req.debtDtos.add(order);
        String json = JsonAdapter.get(req, false);
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("debtWraperDto", json);

        InnerApiResult result = HttpApi.request(Runtime.debtOrderUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, InnerApiResult.class);
        return result;
    }

    public static InnerApiResult submit(DHRepay info) {
        RepayRequest req = new RepayRequest();
        req.repayDtos = new ArrayList<>();
        req.repayDtos.add(info);
        String json = JsonAdapter.get(req, false);
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("repayWraperDto", json);

        InnerApiResult result = HttpApi.request(Runtime.debtRepayUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, InnerApiResult.class);
        return result;
    }

    public static InnerApiResult verifyBid(long num) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("bidNum", num);

        InnerApiResult result = HttpApi.request(Runtime.verifyBidUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, InnerApiResult.class);
        return result;
    }

    public static InnerApiResult verifyOrder(long num) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("debtNum", num);

        InnerApiResult result = HttpApi.request(Runtime.verifyOrderUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, InnerApiResult.class);
        return result;
    }

    public static InnerApiResult verifyRepay(long num) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("repayNum", num);

        InnerApiResult result = HttpApi.request(Runtime.verifyRepayUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, InnerApiResult.class);
        return result;
    }

    /**
     * 提交债务人
     *
     * @param info
     * @return
     */
    public static InnerApiResult submitDebtor(UserInfo info) {
        TreeMap<String, Object> params = new TreeMap<>();
        //memberID	yes	bigint	债务人JDBID
        params.put("memberID", info.user_id);
        //name	yes	string	债务人姓名
        params.put("name", info.user_name);
        //phone	yes	string	债务人手机号
        params.put("phone", info.phone_num);
        //identityNo	yes	string	债务人证件号码
        params.put("identityNo", info.ext.identity_no);
        //identityType	yes	int	证件类型，1身份证,2,军官证,3护照
        params.put("identityType", 1);
        //emergencyContactName	yes	string	紧急联系人姓名
        params.put("emergencyContactName", info.ext.emergency_name);
        //emergencyContactPhone	yes	string	紧急联系人手机号
        params.put("emergencyContactPhone", info.ext.emergency_phone_num);
        //marriageStatus	yes	int	婚姻状态,0 未婚,1 已婚
        params.put("marriageStatus", 0);
        //identityFraud	yes	int	停催标示,1冒用,0正常
        params.put("identityFraud", 0);

        params.put("appId", Runtime.accountAppId);
        InnerApi.signForAccount(params);

        InnerApiResult result = HttpApi.request(Runtime.debtorUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, InnerApiResult.class);
        return result;
    }

    /**
     * 提交债务人
     *
     * @param info
     * @return
     */
    public static InnerApiResult submitCreditor(UserInfo info) {
        TreeMap<String, Object> params = new TreeMap<>();
        //jdb_id	债权人jdbid	是
        params.put("jdb_id", info.user_id);
        //identity_id	债权人证件号码	是
        params.put("identity_id", info.ext.identity_no);
        //name	债权人姓名	是
        params.put("name", info.user_name);
        //phone	债权人手机号	是
        params.put("phone", info.phone_num);
        //identity_type	债务人证件类型，1:身份证，2:军官证，3:护照	是
        params.put("identity_type", 1);
        //sex	性别1,男性,2女性,3未知	是
        params.put("sex", info.ext.sex);

        params.put("appId", Runtime.accountAppId);
        InnerApi.signForAccount(params);

        InnerApiResult result = HttpApi.request(Runtime.creditorUrl+ "?htTraceId=ht" + InnerApi.genTraceId(), params, InnerApiResult.class);
        return result;
    }
}
