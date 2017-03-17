package org.hotwheel.ctp.util;

import org.hotwheel.ctp.model.StockHistory;

import java.util.List;

/**
 * 测试历史数据接口
 * Created by wangfeng on 2017/3/12.
 */
public class TestHistoryData {

    public static void main(String[] args) {
        String code = "sz000088";

        List<StockHistory> sh = StockApi.getHistory(code);
        //List<StockRealTime> srl = StockApi.getRealTime(code);
        System.out.println(sh);

    }
}
