package org.hotwheel.ctp.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.model.StockMoneyFlow;
import org.hotwheel.ctp.util.StockApi;

/**
 * 资金流向
 * <p>
 * Created by wangfeng on 2017/9/11.
 *
 * @version 2.1.0
 * @see <url>http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj?daima=600001</url>
 */
public class MoneyFlowUtils {
    private final static String urlMoneyFlow = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj";

    public static StockMoneyFlow getOne(String code) {
        StockMoneyFlow data = null;
        String url = urlMoneyFlow + "?daima=" + code;
        String response = StockApi.httpGet(url, "GBK");
        if (Api.isEmpty(response) || response.length() < 10) {
            //
        } else {
            String str = response;
            if (str.charAt(0) == '(') {
                // 单个信息
                str = str.substring(1, str.length() - 1);
            } else {
                // 多个不用处理, 按照数组来解析
            }
            try {
                Object obj = JSON.parse(str);
                if (obj != null) {
                    if (obj instanceof JSONObject) {
                        JSONObject jsonObj = (JSONObject) obj;
                        data = jsonObj.toJavaObject(StockMoneyFlow.class);
                    } else if (obj instanceof JSONArray) {
                        //
                    }
                }
            } catch (Exception e) {
                //
            }
        }
        return data;
    }
}
