package org.hotwheel.stock.exchange.context.jdb.v2;

import org.hotwheel.stock.exchange.context.trade.RepayInfo;

/**
 * Created by wangfeng on 2017/1/9.
 */
public interface RepayContext {

    public boolean parse(RepayInfo repay);
}
