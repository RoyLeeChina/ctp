package org.hotwheel.ctp.index;

import org.hotwheel.ctp.model.StockHistory;

/**
 * EMA（Exponential Moving Average），指数平均数指标。也叫EXPMA指标，它也是一种趋向类指标，指数平均数指标是以指数式递减加权的移动平均。
 *
 * Created by wangfeng on 2017/3/18.
 * @version 1.0.1
 * @see <url>http://baike.baidu.com/item/EMA/12646151?sefr=cr</url>
 */
public class EMAIndex extends AbstractIndex implements IndexContext{
    private int weight = 13;

    @Override
    public void compute(StockHistory data) {

    }
}
