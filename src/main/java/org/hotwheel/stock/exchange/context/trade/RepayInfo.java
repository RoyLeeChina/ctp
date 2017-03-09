package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.hotwheel.stock.exchange.model.Money;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.BeanAlias;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 还款流水(按照uuid的全部还款记录)
 *
 * Created by wangfeng on 16/8/11.
 */
public class RepayInfo extends TradeDataLine{

    // 交易流水号
    @BeanAlias("id")
    public String uuid = ""; // "uuid":"116"
    @BeanAlias("debt_id")
    public String debtId = ""; // "debt_id":"609452352603882891"
    // 债务人客户编号
    public String fromUser = ""; // "fromUser":"538748746452705280"
    // 债权人客户编号
    public String toUser = ""; // "toUser":"-1"
    // bidUuid
    public String bidUuid = ""; // "bidUuid":"6094523526038828810001
    // 实际交易日期
    public Date createTime; // "createTime":"1472574863000"
    // 交易金额
    @BeanAlias("repayAmount")
    public BigDecimal amount; // repayAmount":"3.75"
    // 还本金
    public BigDecimal repayPrincipal; // "repayPrincipal":"0.00"
    // 还利息（利息+逾期利息）
    public BigDecimal repayInterest; // "repayInterest":"0.00"
    // 还逾期利息
    public BigDecimal repayOverdueInterest; // "repayOverdueInterest":"0.00"
    // 还基础逾期管理
    public BigDecimal repayOverduefees; // "repayOverduefees":"0.00"
    // 还特别逾期管理
    public BigDecimal repaySpecialOverduefees; // "repaySpecialOverduefees":"20.00"
    // 还款方式
    public int tradeMethod; // "tradeMethod":"2"
    // 主被动还款标识
    public int subType; // "subType":"2"
    // 是否为虚拟资金
    public int isVirtual = 0; // 1, # 是否为虚拟资金, 1 为是, 0 为不是
    // 还款期间
    public int timeRange = 0; // 1, # 还款期间, 1 为减免期还款, 0 不是

    // 入库时间
    //public Date saveDbTime;
    // 入库后最后修改时间
    public Date lastUpdateTime;

    @Override
    public String toLine() {
        String sRet = null;
        final int  index = 2;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);

        args.put("id", uuid);
        args.put("person_id", fromUser);
        args.put("loan_id", toUser);
        args.put("trade_date", Api.toString(createTime, TradeDataLine.DDF));
        args.put("product_id", bidUuid);
        args.put("trade_time", Api.toString(createTime, TradeDataLine.DDL));
        args.put("trade_method", "1");
        if (toUser != null && toUser.equals("-1")) {
            Money overdueFees =BaseContext.valueOf(repayOverduefees);
            Money specialOverdueFees =BaseContext.valueOf(repaySpecialOverduefees);
            args.put("tranAmt", overdueFees.plus(specialOverdueFees).toMoneyString(2));
        } else {
            args.put("tranAmt", amount.toPlainString());
        }
        // 主被动还款标识
        args.put("sub_type", Api.toString(subType));
        args.put("isVirtual", Api.toString(isVirtual));
        args.put("timeRange", Api.toString(timeRange));

        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);
        return sRet;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[2];
        return csvFilename;
    }
}
