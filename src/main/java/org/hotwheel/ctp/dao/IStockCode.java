package org.hotwheel.ctp.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.ctp.model.StockCode;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 股票代码DAO
 * Created by wangfeng on 2017/3/19.
 *
 * @version 1.0.1
 */
@Service("stockCode")
public interface IStockCode {

    public List<String> getAll();

    /**
     * 查询一个股票代码
     *
     * @param code
     * @param fullCode
     * @return
     */
    public StockCode select(@Param("code") String code, @Param("full_code") String fullCode);

    /**
     * 根据证券名称查询一只股票是否存在
     *
     * @param name
     * @return
     * @throws DataAccessException
     */
    public StockCode selectByName(@Param("name") String name) throws DataAccessException;

    /**
     * 创建一个新用户
     *
     * @param info
     * @return
     */
    public int insert(final StockCode info);

    public int update(final StockCode info);
}
