package org.hotwheel.stock.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.stock.model.StockRealTime;
import org.springframework.stereotype.Repository;

/**
 * 实时行情接口
 *
 * Created by wangfeng on 2017/3/13.
 * @version 1.0.0
 */
@Repository("stockRealTime")
public interface IStockRealTime {

    /**
     * 查询实时行情
     * @param code
     * @return
     */
    public StockRealTime select(@Param("code") String code);

    /**
     * 插入实时行情
     * @param srt
     * @return
     */
    public int insert(StockRealTime srt);

    /**
     * 更新实时行情
     * @param srt
     * @return
     */
    public int update(StockRealTime srt);
}
