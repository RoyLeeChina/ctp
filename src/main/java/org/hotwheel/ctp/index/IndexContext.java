package org.hotwheel.ctp.index;

import org.hotwheel.ctp.model.StockHistory;

/**
 * 指标上下文接口
 * Created by wangfeng on 2017/3/18.
 */
public interface IndexContext {

    /**
     * 计算
     */
    public void compute(StockHistory data);
}
