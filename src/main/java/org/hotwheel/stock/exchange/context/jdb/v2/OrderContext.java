package org.hotwheel.stock.exchange.context.jdb.v2;

import org.hotwheel.stock.exchange.context.trade.DebtInfo;

/**
 * Created by wangfeng on 2017/1/9.
 */
public interface OrderContext {

    /**
     * 解析订单信息
     * @param debt
     * @return
     */
    public boolean parse(DebtInfo debt);
}
