package org.hotwheel.stock.exchange.context.trade;

import org.mymmsc.api.assembly.BeanAlias;

import java.util.List;

/**
 * 还款明细列表
 *
 * Created by wangfeng on 2016/12/7.
 * @since 3.0.1
 */
public class RepayInfoList extends AbstractDebtData{
    @BeanAlias("list")
    public List<RepayInfo> infoList;
}
