package org.hotwheel.ctp.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.ctp.model.StockHistory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 历史数据
 * Created by wangfeng on 2017/3/17.
 * @version 1.0.1
 */
@Service("stockHistory")
public interface IStockHistory {

    /**
     * 捡出某只股票的全部历史数据
     * @param code
     * @return
     * @throws DataAccessException
     */
    public List<StockHistory> selectOne(@Param("code") String code) throws DataAccessException;


    /**
     * 捡出某只股票倒序的部分历史数据
     * @param code
     * @return
     * @throws DataAccessException
     */
    //public List<StockHistory> selectLimit(@Param("code") String code, @Param("limit") long limit) throws DataAccessException;

    /**
     * 查询某只股票某天的历史行情
     * @param code
     * @param day
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

    /**
     * 删除某只股票的全部历史记录
     * @param code
     * @return
     * @throws DataAccessException
     */
    public int deleteOne(@Param("code") String code) throws DataAccessException;

    int insertBatch(List<StockHistory > historyList);
}
