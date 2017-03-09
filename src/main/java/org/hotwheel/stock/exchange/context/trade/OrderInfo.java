package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v1.DebtContext;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.hotwheel.stock.exchange.model.Money;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mymmsc.api.assembly.Api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 订单详情(包括逾期和未逾期)
 * DebtInfo
 * Created by wangfeng on 16/8/11.
 */
public class OrderInfo extends TradeDataLine implements DebtContext {
    public final static String filename = "overdueCreditorOrders.csv";
    // tbidUuid
    public String tbidUuid = "";
    // 债权人客户编号
    public String fromUser = "";
    public String toUser = "";
    // bidUuid
    public String bidUuid = "";
    // 流水id, 订单号, （push用的订单号）--和支付系统的资金流水用
    public String laundryUuid;
    // uuid, repay_flow.uuid)(诉讼用的协议号) – 也是债务id=链接id
    public String uuid = "";
    // lawUuid, uuid+‘03’, 委托诉讼协议编号
    public String lawUuid = "";
    // 产品编码 productCode varchar 255
    // table: product.productcode
    public String productCode = "";
    // 本金, 合同金额( 出借金额(不变，合同金额)) 对应交易的principal 本金
    public BigDecimal principal = new BigDecimal(0.00);
    // 未收本息(除去逾期管理费), 未收本息，对应交易的(total_amount-repay_amount)
    public BigDecimal noRepayAmount = new BigDecimal(0.00);
    // 未收总金额（包括逾期管理费）, noRepayAmount+订单未还逾期管理费
    public BigDecimal noRepayTotalAmount = new BigDecimal(0.00);
    // 已还本息, 对应repay_amount字段
    public BigDecimal repayAmount = new BigDecimal(0.00);
    // 计划出借天数, table: product.term
    public int term = 0;
    // 实际出借天数, table: product.term+逾期天数
    public int realTerm = 0;
    // 出借启始日期, table: repay_flow.createtime
    public Date createTime;
    // 利息合计, 本金利息+逾期利息
    public BigDecimal totalInterest = new BigDecimal(0.00);
    // 利率, table: repay_flow.rate
    public BigDecimal rate = new BigDecimal(0.00);
    // 未还本金, transaction_flow not_yet_principal未还本金
    public BigDecimal notYetPrincipal = new BigDecimal(0.00);
    // 未还利息, 逾期利息（交易transaction_flow not_yet_interest 未还利息(利息+逾期利息)）
    public BigDecimal notYetInterest = new BigDecimal(0.00);
    // 本金利息, 本金利息金额(本金*原利息*逾期天数/365)(对应交易的repay_flow interest利息)
    public BigDecimal interest = new BigDecimal(0.00);
    public BigDecimal overdueInterest = new BigDecimal(0.00);
    // 逾期天数, realTerm - term
    public int overdueDays;
    // 订单基本逾期管理费
    public BigDecimal overdueBasefee = new BigDecimal(0.00);
    // 订单特殊逾期管理费
    public BigDecimal overdueSpecfee = new BigDecimal(0.00);
    // 入库时间
    public Date saveDbTime;
    // 入库后最后修改时间
    public Date lastUpdateTime;
    // 债务人赚利差
    public int is_earn_as_from_user = 0;
    // 债权人
    public int is_earn_as_to_user = 0;

    @Override
    public String toLine() {
        String sRet = null;
        final int index = 1;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);

        Money totalAmount = BaseContext.valueOf(this.noRepayTotalAmount);
        Money repayAmount = BaseContext.valueOf(this.repayAmount);
        Money lendAmount = BaseContext.valueOf(principal);
        Money interest = BaseContext.valueOf(this.interest);
        // ??
        Money overdueInterest = BaseContext.valueOf(this.overdueInterest);
        Money sumFees = BaseContext.valueOf(0);
        Money repaySumFees = BaseContext.valueOf(0);
        Money notYetInterest = BaseContext.valueOf(this.notYetInterest);
        Money notYetPrincipal = BaseContext.valueOf(this.notYetPrincipal);
        Money baseManageFee = BaseContext.valueOf(this.overdueBasefee);
        Money specManageFee = BaseContext.valueOf(this.overdueSpecfee);
        String orderId = BaseContext.toShowId(createTime, tbidUuid);
        //String productCode = "";
        String realName = "";
        String mobile = "";
        String uuids = uuid;
        String totalInterest = BaseContext.valueOf(this.totalInterest).toMoneyString(2);
        String isEarn = is_earn_as_to_user > 0 ? "0": "1";

        // 累加基础逾期管理费和特殊逾期管理费
        sumFees = sumFees.plus(baseManageFee).plus(specManageFee);

        // 债权人客户号
        args.put("cust_id", toUser);
        // 订单号（合同号）
        args.put("order_id", orderId);
        // 标的编号
        args.put("productid", productCode);
        // 债权人姓名
        args.put("creditor_name", realName);
        // 联系方式
        args.put("creditor_phone", mobile);
        // 出借金额(不变，合同金额)
        args.put("lend_principal", lendAmount.toMoneyString(2));
        // 余额合计
        args.put("lend_bal", totalAmount.toMoneyString(2));
        // 已还金额
        args.put("payment_amt", repayAmount.toMoneyString(2));
        // 计划出借天数
        args.put("lend_days", String.valueOf(term));
        // 实际出借天数
        args.put("lend_real_days", String.valueOf(realTerm));
        // 出借起始日期
        args.put("lend_date", Api.toString(createTime, DDF));
        // 利息（历史最高）
        args.put("lend_penaty", totalInterest);
        // 年利率
        args.put("lend_rate", Api.toString(rate));
        // 是否赚利差: 1是赚利差 0不是赚利差
        args.put("isEarn", isEarn);
        // 逾期本金
        args.put("overDuePrincipal", notYetPrincipal.toMoneyString(2));
        // 订单利息
        args.put("orderInterest", interest.toMoneyString(2));
        // 逾期利息
        args.put("overDueInterest", overdueInterest.toMoneyString(2));
        // 逾期罚息
        args.put("overDuePenaty", "0.00");
        // 订单平台管理费（逾期管理费+特殊管理费）
        args.put("orderManagefee", sumFees.minus(repaySumFees).toMoneyString(2));
        // 基础逾期管理费
        args.put("overdue_basefee", baseManageFee.toMoneyString(2));
        // 特殊逾期管理费
        args.put("overdue_specfee", specManageFee.toMoneyString(2));

        args.put("bid_uuid", bidUuid);
        args.put("tbid_uuid", tbidUuid);

        // 增加laundry_uuid
        args.put("real_orderid", laundryUuid);
        // 增加组合的uuid, @todo接口未实现
        args.put("uuids", uuids);

        BaseContext.fillDefault(args, infos);
        //writeToFile(newFile, new StrSubstitutor(args).replace(valueTemp[2]));
        sRet = new StrSubstitutor(args).replace(valueTemp);
        return sRet;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[1];
        return csvFilename;
    }

    @Override
    public void plus(DebtInfo debt) {
        boolean bOverdue = debt.isOverdue();
        // tbidUuid
        tbidUuid = debt.tbid_uuid;
        // 债权人客户编号
        fromUser = debt.fromUser;
        toUser = debt.toUser;
        // bidUuid
        bidUuid = debt.bid_uuid;
        // 流水id, 订单号, （push用的订单号）--和支付系统的资金流水用
        laundryUuid = debt.laundryUuid;
        // uuid, repay_flow.uuid)(诉讼用的协议号) – 也是债务id=链接id
        uuid = debt.uuid;
        productCode = debt.product_code;
        // lawUuid, uuid+‘03’, 委托诉讼协议编号
        //lawUuid = debt.lawStatus;
        // 本金, 合同金额( 出借金额(不变，合同金额)) 对应交易的principal 本金
        principal = debt.principal;
        // 未收本息(除去逾期管理费), 未收本息，对应交易的(total_amount-repay_amount)
        if(bOverdue) noRepayAmount = debt.total_amount.subtract(debt.repay_amount);
        // 已还本息, 对应repay_amount字段
        if(bOverdue) repayAmount = debt.repay_amount;
        // 计划出借天数, table: product.term
        //term = ;
        term = (int)Api.diffDays(debt.start_time, debt.end_time);
        // 实际出借天数, table: product.term+逾期天数
        //realTerm = term + overdueDays;
        // 出借启始日期, table: repay_flow.createtime
        createTime = debt.create_time;
        // 利息合计, 本金利息+逾期利息
        totalInterest = debt.interest.add(debt.original_interest);
        // 利率, table: repay_flow.rate
        rate = debt.rate;
        // 未还本金, transaction_flow not_yet_principal未还本金
        Money tmp = BaseContext.valueOf(debt.principal);
        tmp = tmp.minus(BaseContext.valueOf(debt.repay_principal));
        if(bOverdue) notYetPrincipal = notYetPrincipal.add(tmp.toStoreDecimal());
        // 未还利息, 逾期利息（交易transaction_flow not_yet_interest 未还利息(利息+逾期利息)）
        tmp = BaseContext.valueOf(debt.interest);
        tmp = tmp.minus(BaseContext.valueOf(debt.repay_interest));
        if(bOverdue) notYetInterest = notYetInterest.add(tmp.toStoreDecimal());
        // 本金利息, 本金利息金额(本金*原利息*逾期天数/365)(对应交易的repay_flow interest利息)
        interest = debt.interest;
        overdueInterest = debt.overdue_interest;
        // 逾期天数, realTerm - term
        overdueDays = debt.overdue_day;
        realTerm = term + overdueDays;
        // 订单基本逾期管理费
        overdueBasefee = debt.overdue_base_fee;
        // 订单特殊逾期管理费
        overdueSpecfee = debt.overdue_spec_fee;
        // 未收总金额（包括逾期管理费）, noRepayAmount+订单未还逾期管理费
        tmp = Money.of(noRepayAmount);
        /*tmp = tmp.plus(overdueBasefee);
        tmp = tmp.minus(debt.overdue_base_fee_payed);
        tmp = tmp.plus(overdueSpecfee);
        tmp = tmp.minus(debt.overdue_spec_fee_payed);
        */
        if(bOverdue) noRepayTotalAmount = tmp.toStoreDecimal();
        // 入库时间
        saveDbTime = debt.create_time;
        // 入库后最后修改时间
        lastUpdateTime = debt.update_time;
        // 债务人赚利差
        is_earn_as_from_user = debt.is_earn_as_from_user;
        // 债权人
        is_earn_as_to_user = debt.is_earn_as_to_user;
    }
}
