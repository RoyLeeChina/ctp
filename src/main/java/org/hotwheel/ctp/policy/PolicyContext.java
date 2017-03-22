package org.hotwheel.ctp.policy;

import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.model.StockMonitor;

import java.util.List;

/**
 * 策略上下文
 * Created by wangfeng on 2017/3/23.
 */
public interface PolicyContext {
    /**
     * 计算
     */
    public StockMonitor compute(List<StockHistory> data);
}
