package org.hotwheel.stock.util;

import org.hotwheel.asio.HttpApi;
import org.hotwheel.stock.StockOptions;
import org.hotwheel.stock.data.HistoryData;
import org.hotwheel.stock.model.StockHistory;

import java.util.List;

/**
 * 股票接口
 *
 * Created by wangfeng on 2017/3/12.
 */
public final class StockApi {

    public static List<StockHistory> getHistory(final String code) {
        //return HttpApi.request(StockOptions.urlHistory, null, StockOptions.historyParams(code), StockHistory[].class);
        List<StockHistory> result = HistoryData.getKLineDataObjects(code, StockOptions.ONE_DAY);
        return result;
    }
}
