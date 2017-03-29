package org.hotwheel.ctp.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.ctp.model.StockSubscribe;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 股票订阅
 *
 * Created by wangfeng on 2017/3/14.
 * @version 1.0.0
 */
@Service("stockSubscribe")
public interface IStockSubscribe {

    /**
     * 捡出全部有效的订阅
     * @return
     */
    public List<StockSubscribe> queryAll();

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

    /**
     * 查询一只股票的所有订阅用户
     * @param code
     * @return
     * @throws DataAccessException
     */
    public List<StockSubscribe> queryByCode(@Param("code") String code) throws DataAccessException;

    /**
     * 捡出全部有效的股票代码
     * @return
     * @throws DataAccessException
     */
    public List<String> checkoutAllCode() throws DataAccessException;

    /**
     * 查询一个用户订阅的全部股票代码
     * @param phone
     * @return
     * @throws DataAccessException
     */
    public List<String> checkoutByPhone(@Param("phone") String phone) throws DataAccessException;
}
