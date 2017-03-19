package org.hotwheel.ctp.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.ctp.model.StockHistory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 历史数据
 * Created by wangfeng on 2017/3/17.
 * @version 1.0.1
 */
@Service("stockHistory")
public interface IStockHistory {
    /**
     * 查询历史行情
     * @param code
     * @return
     */
    public StockHistory select(@Param("code") String code, @Param("day")String day) throws DataAccessException;

    /**
     * 插入历史行情
     * @param srt
     * @return
     */
    public int insert(StockHistory srt) throws DataAccessException;

    /**
     * 更新实时行情
     * @param srt
     * @return
     */
    public int update(StockHistory srt) throws DataAccessException;

    /**
     * 获得历史数据的最后一天
     * @param code
     * @return
     * @throws DataAccessException
     */
    public Date getLastDate(String code) throws DataAccessException;
}
