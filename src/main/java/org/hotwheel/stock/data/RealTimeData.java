package org.hotwheel.stock.data;


import org.hotwheel.assembly.Api;
import org.hotwheel.stock.model.StockRealTime;
import org.hotwheel.stock.util.StockApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对新浪财经查询股票实时数据API的封装<br>
 * API：http://hq.sinajs.cn/<br>
 *
 * @version 1.0.0
 */
public class RealTimeData {
    /**
     * 获取股票历史数据
     * 例子：<br>
     * String[] codes = {"sz000002","sz000001"};<br>
     * List&lt;RealTimeDataPOJO&gt; result = RealTimeData.getRealTimeDataObjects(codes);<br>
     *
     * @param codes 股票代码数组 例如 {"sz000002","sz000001"}
     * @return 一个{@link List}，里面是{@link StockRealTime}对象
     */
    public static List<StockRealTime> getRealTimeDataObjects(String[] codes) {
        String indexPatternString = "var hq_str_s_(\\w{8})=\"(.+)\"";
        String stockPatterString = "var hq_str_(\\w{8})=\"(.+)\"";
        Pattern indexPatter = Pattern.compile(indexPatternString);
        Pattern stockPatter = Pattern.compile(stockPatterString);
        List<StockRealTime> result = new ArrayList<>();
        String url = createURL(codes);
        String response = StockApi.sendHTTPGET(url, "GBK");
        String[] responses = response.split(";");
        for (int i = 0; i < responses.length; i++) {
            String reresponseString = responses[i];
            Matcher stockMatcher = stockPatter.matcher(reresponseString);
            if (stockMatcher.find()) {
                StockRealTime obj = new StockRealTime();
                obj.setType(StockRealTime.STOCK);
                obj.setFullCode(stockMatcher.group(1));
                String[] array = stockMatcher.group(2).split(",");
                obj.setName(array[0]);
                obj.setOpen(Api.valueOf(double.class, array[1]));
                obj.setClose(Api.valueOf(double.class, array[2]));
                obj.setNow(Api.valueOf(double.class, array[3]));
                obj.setHigh(Api.valueOf(double.class, array[4]));
                obj.setLow(Api.valueOf(double.class, array[5]));
                obj.setBuyPrice(Api.valueOf(double.class, array[6]));
                obj.setSellPrice(Api.valueOf(double.class, array[7]));
                obj.setVolume(Api.valueOf(long.class, array[8]));
                obj.setVolumePrice(Api.valueOf(double.class, array[9]));
                obj.setBuy1Num(Api.valueOf(long.class, array[10]));
                obj.setBuy1Price(Api.valueOf(double.class, array[11]));
                obj.setBuy2Num(Api.valueOf(long.class, array[12]));
                obj.setBuy2Price(Api.valueOf(double.class, array[13]));
                obj.setBuy3Num(Api.valueOf(long.class, array[14]));
                obj.setBuy3Price(Api.valueOf(double.class, array[15]));
                obj.setBuy4Num(Api.valueOf(long.class, array[16]));
                obj.setBuy4Price(Api.valueOf(double.class, array[17]));
                obj.setBuy5Num(Api.valueOf(long.class, array[18]));
                obj.setBuy5Price(Api.valueOf(double.class, array[19]));
                obj.setSell1Num(Api.valueOf(long.class, array[20]));
                obj.setSell1Price(Api.valueOf(double.class, array[21]));
                obj.setSell2Num(Api.valueOf(long.class, array[22]));
                obj.setSell2Price(Api.valueOf(double.class, array[23]));
                obj.setSell3Num(Api.valueOf(long.class, array[24]));
                obj.setSell3Price(Api.valueOf(double.class, array[25]));
                obj.setSell4Num(Api.valueOf(long.class, array[26]));
                obj.setSell4Price(Api.valueOf(double.class, array[27]));
                obj.setSell5Num(Api.valueOf(long.class, array[28]));
                obj.setSell5Price(Api.valueOf(double.class, array[29]));
                Date ldt = StockApi.string2LocalDateTime(array[30] + " " + array[31]);
                obj.setDate(ldt);
                obj.setTime(ldt);
                result.add(obj);
            } else {
                Matcher indexMatcher = indexPatter.matcher(reresponseString);
                if (indexMatcher.find()) {
                    StockRealTime obj = new StockRealTime();
                    obj.setType(StockRealTime.INDEX);
                    obj.setFullCode(indexMatcher.group(1));
                    String[] array = indexMatcher.group(2).split(",");
                    obj.setName(array[0]);
                    obj.setNow(Api.valueOf(double.class, array[1]));
                    obj.setRiseAndFall(Api.valueOf(double.class, array[2]));
                    obj.setRiseAndFallPercent(Api.valueOf(double.class, array[3]));
                    obj.setVolume(Api.valueOf(long.class, array[4]));
                    obj.setVolumePrice(Api.valueOf(double.class, array[5]));
                    Date ldt = new Date();
                    obj.setDate(ldt);
                    obj.setTime(ldt);
                    result.add(obj);
                }
            }
        }
        return result;
    }

    private static String createURL(String[] codes) {
        String codelist = "";
        for (int i = 0; i < codes.length; i++) {
            codelist += codes[i];
            if (i != codes.length - 1) {
                codelist += ",";
            }
        }
        String url = String.format("http://hq.sinajs.cn/list=%s", codelist);
        return url;
    }
}
