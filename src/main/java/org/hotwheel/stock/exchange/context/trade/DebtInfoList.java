package org.hotwheel.stock.exchange.context.trade;

import org.mymmsc.api.assembly.BeanAlias;

import java.util.List;

/**
 * 债务项列表
 * Created by wangfeng on 2016/12/7.
 * @since 3.0.1
 */
public class DebtInfoList extends AbstractDebtData {
    @BeanAlias("list")
    public List<DebtInfo> infoList;
}
