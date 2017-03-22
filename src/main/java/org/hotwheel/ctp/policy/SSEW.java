package org.hotwheel.ctp.policy;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.model.StockMonitor;
import org.hotwheel.ctp.util.DateUtils;
import org.hotwheel.ctp.util.StockApi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 顺势而为, 指标SSEW
 *
 * Created by wangfeng on 2017/3/23.
 * @version 1.0.2
 */
public class SSEW implements PolicyContext {
    // 策略名称
    private final static String name = "ctp.policy.ssew";


    @Override
    public StockMonitor compute(List<StockHistory> data) {
        StockMonitor result = null;
        if (data == null || data.size() < 1) {
            // 无历史记录, 无法进行
        } else {
            int size = data.size();
            int lastIndex = size - 1;
            result = new StockMonitor();
            result.setPolicy(name);
            StockHistory sh = data.get(lastIndex);
            result.setCode(sh.getCode());
            // 判断日期
            Date lastDay = DateUtils.getZero(sh.getDay());
            Date today = DateUtils.getZero(new Date());
            if (lastDay.getTime() == today.getTime()) {
                // 当天的历史数据已经获取
                today = Api.addDate(today, Calendar.DAY_OF_MONTH, 1);
                today = Api.addDate(today, Calendar.HOUR_OF_DAY, 6);
            }
            result.setDay(today);
            result.setCreateTime(new Date());
            if (size < 2) {
                // 如果只有一条记录, 无法进行策略
                result.setFlag(StockOptions.kNullState);
            } else {
                result.setFlag(StockOptions.kNormalState);
                double CLOSE = sh.getClose();
                //ZLW0:=IF((CLOSE > 200),(CLOSE * 1.01),(CLOSE * 1.07));
                double zlw0 = CLOSE > 200 ? CLOSE * 1.01 : CLOSE * 1.07;
                //ZLW1:=IF((CLOSE < 10),(CLOSE * 1.05),ZLW0);
                double zlw1 = CLOSE < 10 ? CLOSE * 1.05 : zlw0;
                //ZSW0:=IF((CLOSE > 200),(CLOSE * 0.99),(CLOSE * 0.93));
                double zsw0 = CLOSE > 200 ? CLOSE * 0.99 : CLOSE * 0.93;
                //ZSW1:=IF((CLOSE < 10),(CLOSE * 0.95),ZSW0);
                double zsw1 = CLOSE < 10 ? CLOSE * 0.95 : zsw0;

                //ZSW:ZSW1;
                result.setStop(StockApi.toPrice(zsw1));
                //ZLW:ZLW1;
                result.setResistance(StockApi.toPrice(zlw1));

                StockHistory sh1 = data.get(lastIndex - 1);
                //PT:=REF(HIGH,1)-REF(LOW,1);
                double pt = sh1.getHigh() - sh1.getLow();
                //ZX:(HIGH + LOW + CLOSE)/3;
                double zx = (sh.getHigh() + sh.getLow() + sh.getClose()) / 3;
                //YL1:2*ZX-LOW;
                double yl1 = 2 * zx - sh.getLow();
                //YL2:ZX + PT;
                double yl2 = zx + pt;
                //ZC1:2*ZX-HIGH;
                double zc1 = 2 * zx - sh.getHigh();
                //ZC2:ZX-PT;
                double zc2 = zx - pt;
                if (zc1 < zc2) {
                    double tmp = zc1;
                    zc1 = zc2;
                    zc2 = tmp;
                }
                result.setSupport1(StockApi.toPrice(zc1));
                result.setSupport2(StockApi.toPrice(zc2));
                if (yl1 > yl2) {
                    double tmp = yl1;
                    yl1 = yl2;
                    yl2 = tmp;
                }
                result.setPressure1(StockApi.toPrice(yl1));
                result.setPressure2(StockApi.toPrice(yl2));
            }
        }
        return result;
    }
}
