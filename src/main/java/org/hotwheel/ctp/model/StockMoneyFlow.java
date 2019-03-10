package org.hotwheel.ctp.model;

/**
 * 资金流向
 * Created by wangfeng on 2017/9/11.
 *
 * @version 2.1.0
 */
public class StockMoneyFlow {
    /**
     * 多个代码
     * [{r0_in:"0.0000",r0_out:"0.0000",r0:"0.0000",r1_in:"0.0000",r1_out:"0.0000",r1:"0.0000",r2_in:"0.0000",r2_out:"0.0000",r2:"0.0000",r3_in:"0.0000",r3_out:"0.0000",r3:"0.0000",curr_capital:"281646",name:"邯郸钢铁",trade:"0.0000",changeratio:"0",volume:"0.0000",turnover:"0",r0x_ratio:"0",netamount:"0.0000",symbol:"sh600001"},{r0_in:"34580182.3400",r0_out:"49017432.8000",r0:"83597615.1400",r1_in:"35256879.4300",r1_out:"45030455.4600",r1:"80287334.8900",r2_in:"30471277.2000",r2_out:"43688967.2900",r2:"74160244.4900",r3_in:"18208068.9400",r3_out:"20718684.8200",r3:"38928153.7600",curr_capital:"2203883",name:"包钢股份",trade:"2.8000",changeratio:"-0.0070922",volume:"98474545.0000",turnover:"44.6823",r0x_ratio:"-97.7462",netamount:"-39939132.4600",symbol:"sh600010"}]
     */
    /**
     * 单个代码
     * ({r0_in:"0.0000",r0_out:"0.0000",r0:"0.0000",r1_in:"0.0000",r1_out:"0.0000",r1:"0.0000",r2_in:"0.0000",r2_out:"0.0000",r2:"0.0000",r3_in:"0.0000",r3_out:"0.0000",r3:"0.0000",curr_capital:"281646",name:"邯郸钢铁",trade:"0.0000",changeratio:"0",volume:"0.0000",turnover:"0",r0x_ratio:"0",netamount:"0.0000"})
     */
    // 特大单 流入
    public double r0_in;
    // 特大单 流出
    public double r0_out;
    // 特大单 成交金额
    public double r0;
    // 大单 流入
    public double r1_in;
    // 大单 流出
    public double r1_out;
    // 大单 成交量
    public double r1;
    // 中单 流入
    public double r2_in;
    // 中单 流出
    public double r2_out;
    // 中单 成交量
    public double r2;
    // 散单 流入
    public double r3_in;
    // 散单 流出
    public double r3_out;
    // 散单 成交量
    public double r3;
}
