package org.hotwheel.stock.exchange.context.jdb.v2;

import org.hotwheel.stock.exchange.context.trade.ProductInfo;

/**
 * 贷后数据输入
 * Created by wangfeng on 2017/1/9.
 */
public interface BidContext {

    /**
     * 解析产品信息
     * @param product
     * @return
     */
    public boolean parse(ProductInfo product);
}
