package org.hotwheel.stock.util;

import org.hotwheel.assembly.Api;
import org.hotwheel.json.JsonAdapter;

/**
 * 策略工具类
 * Created by wangfeng on 2017/3/15.
 * @version 1.0.0
 */
public class PolicyApi {

    public static Policy get(final String str) {
        Policy result = null;
        if (!Api.isEmpty(str)) {
            JsonAdapter json = JsonAdapter.parse(str);
            if (json != null) {
                try {
                    result = json.get(Policy.class);
                } finally {
                    json.close();
                }
            }
        }
        return result;
    }
}
