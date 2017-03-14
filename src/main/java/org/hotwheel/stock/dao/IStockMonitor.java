package org.hotwheel.stock.dao;

import org.hotwheel.stock.model.StockMonitor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 策略监控接口
 * Created by wangfeng on 2017/3/15.
 * @version 1.0.0
 */
@Service("stockMonitor")
public interface IStockMonitor {
    public List<StockMonitor> queryAll();
}
