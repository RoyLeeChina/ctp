package org.hotwheel.stock.dao;

import org.apache.ibatis.annotations.Param;
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

    /**
     * 捡出全部监控策略
     * @return
     */
    public List<StockMonitor> queryAll();

    /**
     * 查询 当天策略
     *
     * @param code
     * @return
     */
    public StockMonitor query(@Param("code") String code);

    public int insert(StockMonitor info);
    public int update(StockMonitor info);
}
