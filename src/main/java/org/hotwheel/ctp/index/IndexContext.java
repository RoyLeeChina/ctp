package org.hotwheel.ctp.index;

import org.hotwheel.ctp.model.StockHistory;

import java.util.List;

/**
 * 指标上下文接口
 * Created by wangfeng on 2017/3/18.
 */
public interface IndexContext {

    public double getWeight(int days);

    /**
     * 计算
     */
    public void compute(List<StockHistory> data);
}
