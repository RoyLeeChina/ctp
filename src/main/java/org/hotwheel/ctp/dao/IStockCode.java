package org.hotwheel.ctp.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.ctp.model.StockCode;
import org.springframework.stereotype.Service;

/**
 * 股票代码DAO
 * Created by wangfeng on 2017/3/19.
 * @version 1.0.1
 */
@Service("stockCode")
public interface IStockCode {

    public StockCode select(@Param("code") String phone, @Param("full_code")String fullCode);

    /**
     * 创建一个新用户
     * @param info
     * @return
     */
    public int insert(StockCode info);

    public int update(StockCode info);
}
