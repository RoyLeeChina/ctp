package org.hotwheel.stock.exchange.controller;

import org.hotwheel.stock.exchange.context.Category;
import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.util.DateUtils;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.redis.IRedisCallback;
import org.mymmsc.api.redis.RedisApi;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.ShardedJedis;

import java.util.Date;

/**
 * ID生成器
 *
 * Created by wangfeng on 2017/1/18.
 * @since 1.0.1
 */
@Controller
@RequestMapping("/id")
public class IdGeneratorController {
    private static long kStart = Long.MAX_VALUE;

    @ResponseBody
    @RequestMapping("/gen")
    public String resetDebtTask(String keywords) {
        String sRet = "-1";
        Date now = new Date();
        RedisApi redisApi = Runtime.getRedisApi();
        final String key = "jdb_collect_id:" + keywords;

        long value = redisApi.command(key, new IRedisCallback<Long>() {
            @Override
            public Long exec(ShardedJedis jedis, String key) {
                jedis.expire(key, DateUtils.getRemainingTime());
                Long tmp = -1L;
                try {
                    tmp = jedis.incr(key);
                } catch (Exception e) {
                    //
                }

                return tmp;
            }
        });
        if (value > 0) {

            // 9223372036854775808, 19位
            // yyyyMMDD
            sRet = Api.toString(now, Category.DateFormat2);
            value += 10000000000L;
            String tmp = String.valueOf(value);
            sRet += tmp.substring(1);
        }

        return sRet;
    }
}
