package org.hotwheel.stock.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.stock.model.StockRealTime;
import org.hotwheel.stock.model.StockSubscribe;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * 股票订阅
 *
 * Created by wangfeng on 2017/3/14.
 * @version 1.0.0
 */
@Service("stockSubscribe")
public interface IStockSubscribe {
    /**
     * 查询订阅
     * @param code
     * @return
     */
    public StockSubscribe select(@Param("phone") String phone, @Param("code") String code) throws DataAccessException;

    /**
     * 插入订阅
     * @param info
     * @return
     */
    public int insert(StockSubscribe info) throws DataAccessException;

    /**
     * 更新订阅
     * @param info
     * @return
     */
    public int update(StockSubscribe info) throws DataAccessException;
}
