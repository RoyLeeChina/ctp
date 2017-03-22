package org.hotwheel.ctp.dao;

import org.hotwheel.ctp.model.StockMessage;

import java.util.List;

/**
 * 策略消息
 *
 * Created by wangfeng on 2017/3/22.
 * @version 1.0.2
 */
public interface IStockMessage {
    public List<StockMessage> selectAll(final String type);
    public int insert(final StockMessage info);
    public int update(final StockMessage info);
}
