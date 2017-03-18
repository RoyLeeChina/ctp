package org.hotwheel.ctp.index;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.model.EMA;
import org.hotwheel.ctp.model.StockHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * EMA（Exponential Moving Average），指数平均数指标。也叫EXPMA指标，它也是一种趋向类指标，指数平均数指标是以指数式递减加权的移动平均。
 *
 * Created by wangfeng on 2017/3/18.
 * @version 1.0.1
 * @see <url>http://baike.baidu.com/item/EMA/12646151?sefr=cr</url>
 */
public class EMAIndex extends AbstractIndex implements IndexContext{
    //private int cycle = 12;
    //private int weight = 13;
    private List<EMA> listEma;

    @Override
    public double getWeight(int days) {
        double dRet = 2;
        //加权因子=2/(N+1);
        // Weighting factor
        return dRet / (days + 1);
    }

    @Override
    public void compute(List<StockHistory> data) {
        if (listEma == null) {
            listEma = new ArrayList<>(data.size());
        }
        EMA currentEma = null;
        EMA frontEma = null;

        for (int i = 0; i < data.size(); i++) {
            StockHistory today = data.get(i);
            EMA ema = new EMA();
            if (i == 0) {
                ema.setEma1(today.getClose());
                ema.setEma2(today.getClose());
                currentEma = ema;
                frontEma = currentEma;
            } else {
                double weight = getWeight(ema.getCycle1());
                // 加权因子 * 当天的收盘价 +（1-加权因子）* 昨天的EMA
                double value =  weight * today.getClose() + (1 - weight) * frontEma.getEma1();
                ema.setEma1(value);
                weight = getWeight(ema.getCycle2());
                value = weight * today.getClose() + (1 - weight) * frontEma.getEma2();
                ema.setEma2(value);
                frontEma = ema;
            }
            ema.setDay(Api.toString(today.getDay(), StockOptions.DateFormat));
            //listEma.set(i, ema);
            listEma.add(ema);
        }
    }

    public List<EMA> getListEma() {
        return listEma;
    }

    public void setListEma(List<EMA> listEma) {
        this.listEma = listEma;
    }
}
