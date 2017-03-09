package org.hotwheel.stock.exchange.context.trade;

import org.mymmsc.api.assembly.BeanAlias;

/**
 * Created by wangfeng on 16/8/11.
 * @since 3.0.1
 */
public class TradeProductIdList extends AbstractDebtData {
     @BeanAlias("list")
     public String[] infoList;// ["5840896618198549960001"], //标的ID列表

}
