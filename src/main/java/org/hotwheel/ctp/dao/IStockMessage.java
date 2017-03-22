package org.hotwheel.ctp.dao;

import org.apache.ibatis.annotations.Param;
import org.hotwheel.ctp.model.StockMessage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 策略消息
 *
 * Created by wangfeng on 2017/3/22.
 * @version 1.0.2
 */
@Service("stockMessage")
public interface IStockMessage {
    public int cleanAll();
    public List<StockMessage> selectAll(final String type);
    public int insert(final StockMessage info);
    public int update(final StockMessage info);
    public int updateType(@Param("id") long id, @Param("type") String type);
}
