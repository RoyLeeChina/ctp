package org.hotwheel.ctp.test;

import com.alibaba.fastjson.JSON;
import org.hotwheel.assembly.RegExp;
import org.hotwheel.core.io.DefaultResourceLoader;
import org.hotwheel.core.io.Resource;
import org.hotwheel.ctp.data.MoneyFlowUtils;
import org.hotwheel.ctp.model.StockMoneyFlow;
import org.hotwheel.ctp.util.ExcelApi;
import org.hotwheel.json.JsonAdapter;

import java.io.IOException;

/**
 * 测试数据
 *
 * Created by wangfeng on 2017/3/19.
 */
public class TestData {

    public static void main(String[] args) {

        StockMoneyFlow moneyFlow = MoneyFlowUtils.getOne("sz000789");
        System.out.println(JsonAdapter.get(moneyFlow, false));
        String text = "({r0_in:\"35900722.2600\",r0_out:\"76097226.6100\",r0:\"115435711.8700\",r1_in:\"133697056.4000\",r1_out:\"151195932.8900\",r1:\"292575412.2900\",r2_in:\"67635481.0600\",r2_out:\"74890046.9700\",r2:\"147907606.0300\",r3_in:\"19164795.2600\",r3_out:\"17796232.6200\",r3:\"38082645.8800\",curr_capital:\"30997\",name:\"中船科技\",trade:\"18.8400\",changeratio:\"-0.0253492\",volume:\"31195191.0000\",turnover:\"1006.4\",r0x_ratio:\"-110.535\",netamount:\"-63581384.1100\"})";
        String str = text.substring(1, text.length() - 1);
        Object objJson = JSON.parse(str);
        String fullCode = "000001";
        final String exp = "^[0-9]{6}$";
        if (RegExp.valid(fullCode, exp)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
        System.out.println(RegExp.get(fullCode, exp, ""));

        String filename = "classpath:/stock/china-stock-list.xlsx";
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(filename);
        ExcelApi api = new ExcelApi();
        try {
            String filepath = resource.getFile().getAbsolutePath();
            api.read(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
