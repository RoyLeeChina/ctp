package org.hotwheel.stock.exchange.context.jdb.v1;

import org.hotwheel.stock.exchange.context.trade.DebtInfo;

/**
 * 债务接口定义
 * Created by wangfeng on 2016/12/11.
 * @since 3.0.2
 */
public interface DebtContext {
    public void plus(DebtInfo debt);
}
