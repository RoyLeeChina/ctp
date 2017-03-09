package org.hotwheel.stock.exchange.context.trade;

import org.mymmsc.api.assembly.BeanAlias;

/**
 * 分页获取交易所有逾期用户ID的List
 *
 * Created by wangfeng on 16/8/11.
 */
public class TradeDebtorList extends AbstractDebtData {
    @BeanAlias("debtorList")
    public String[] infoList;
}
