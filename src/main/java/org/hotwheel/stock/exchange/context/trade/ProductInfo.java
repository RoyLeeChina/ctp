package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.DebtContext;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.hotwheel.stock.exchange.model.Money;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.BeanAlias;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 标的(逾期和未逾期通用)
 *
 * Created by wangfeng on 16/8/11.
 */
public class ProductInfo extends TradeDataLine implements DebtContext {
    public final static String filename = "overdueBidsInfo.csv";
    //bidUuid
    @BeanAlias("uuid")
    public String bidUuid = ""; // bigint 30 主键
    // 债务人客户编号 fromUser bigint    20
    @BeanAlias("userID")
    public String fromUser = "";
    // 产品编码 productCode varchar 255
    // table: product.productcode
    public String productCode = "";
    // 利差层数 layer tinyint 3 债务人处在第几层利差
    @BeanAlias("earnSpreadLevel")
    public int layer;
    // 借款年利率 rate decimal(4,2) 年利率
    // table: product.rate
    public BigDecimal rate = new BigDecimal(0.00);
    // 金额 planAmount decimal(14,2) 金额
    // (原标的计划借款金额)
    // table: product.currentamount(???)
    public BigDecimal planAmount = new BigDecimal(0.00);
    // 实际借到金额 realAmount decimal(14,2)
    // 实际上借到的金额
    // table: product.currentamount-product.avaliable(???)
    public BigDecimal realAmount = new BigDecimal(0.00);
    // 借款用途 remark varchar(n) 255 默认为''
    public String remark = "临时周转";
    // 约定还款日 endTime Timestamp
    // table: product.endtime
    public Date endTime;
    // 逾期天数 maxOverdueDay Smallint 最大逾期天数
    public int maxOverdueDay = 0;
    public int maxOverdueDayRaw = 0;
    // 逾期起始日 overdueStartDay Timestamp
    // table: product.endtime+1
    public Date overdueStartDay;
    // 逾期总额 overdueTotalAmount decimal(14,2)
    // 未还本息+逾期管理费：
    // (repay_flow.total_amount(还款总金额=金额(本金+利息)＋逾期利息)-repay_flow.repay_amount(还款金额)
    // +overdue_fees.sum_fees-overdue_fees.repay_sum_fees)
    // 催收员作业用
    public BigDecimal overdueTotalAmount = new BigDecimal(0.00);
    // 未还利息(利息＋逾期利息 notYetInterest decimal(14,2)
    // table: transactionflow.notyetinterest
    // 催收员作业用
    public BigDecimal interest = new BigDecimal(0.00);
    public BigDecimal repay_interest = new BigDecimal(0.00);
    public BigDecimal notYetInterest = new BigDecimal(0.00);

    // 未还 逾期利息
    public BigDecimal overdue_interest = new BigDecimal(0.00);
    public BigDecimal repay_overdue_interest = new BigDecimal(0.00);
    public BigDecimal notYetOverdueInterest = new BigDecimal(0.00);
    // 未还本金 notYetPrincipal decimal(14,2)
    // table: transactionFlow.notyetprincipal
    // 催收员作业用
    public BigDecimal principal = new BigDecimal(0.00);
    public BigDecimal repay_principal = new BigDecimal(0.00);
    public BigDecimal notYetPrincipal = new BigDecimal(0.00);
    // 结清标识 flag tinyint 2
    // table: repay_flow.total_amount-repay_amount>0 and overdue_day>0，逾期未结清
    public int flag = 0;

    // 利息金额 decimal(14,2) 确定这个字段是否有用(??)

    // 标的基本逾期管理费 overdueBasefee decimal(14,2)
    // table: overdueFee.getFees()- overdueFee.getRepayFees()
    // 催收员作业用
    public BigDecimal overdue_base_fee = new BigDecimal(0.00);
    public BigDecimal repay_overdue_base_fee = new BigDecimal(0.00);
    @BeanAlias("notYetOverdueBasefee")
    public BigDecimal overdueBasefee = new BigDecimal(0.00);
    // 标的特殊逾期管理费 overdueSpecfee decimal(14,2)
    // table: overdueFee.getSpecialFees()- overdueFee.getRepaySpecialFees()
    // 催收员作业用
    public BigDecimal overdue_spec_fee = new BigDecimal(0.00);
    public BigDecimal repay_overdue_spec_fee = new BigDecimal(0.00);
    @BeanAlias("notYetOverdueSpecialfee")
    public BigDecimal overdueSpecfee = new BigDecimal(0.00);

    // 入库时间 saveDbTime timestamp
    @BeanAlias("createTime")
    public Date saveDbTime;
    // 入库后最后修改时间 lastUpdateTime timestamp
    public Date lastUpdateTime;
    public Date clearTime;

    @Override
    public String toLine() {
        String sRet = null;
        final int index = 0;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);

        args = productBaseMap(args);

        // 放款日期(最后一个人的出钱日期)
        //Date llCreateTime = lastLaundry.getCreateTime();
        //lastLendTime = DateUtil.convertDateToStr(llCreateTime, DDF);
        args.put("grant_money_date", "");
        // 借款用途
        args.put("purpose", remark);
        // 逾期日
        args.put("overdue_date", Api.toString(overdueStartDay, TradeDataLine.DDF));

        Money totalAmount = BaseContext.valueOf(overdueTotalAmount);
        totalAmount = totalAmount.plus(overdueBasefee).plus(overdueSpecfee);
        Money overduePrincipalAmount = BaseContext.valueOf(notYetPrincipal);
        Money overdueInterestAmount = BaseContext.valueOf(notYetOverdueInterest);
        overdueInterestAmount = overdueInterestAmount.plus(notYetInterest);
        Money baseManageFee = BaseContext.valueOf(overdueBasefee);
        Money specManageFee = BaseContext.valueOf(overdueSpecfee);
        Money overdueManagerAmount = BaseContext.valueOf(0);
        overdueManagerAmount = baseManageFee.plus(specManageFee);
        maxOverdueDay = maxOverdueDayRaw;
        args.put("overdue_days", String.valueOf(maxOverdueDay));
        args.put("overdue_amount", totalAmount.toMoneyString(2));
        args.put("overdue_interest", overdueInterestAmount.toMoneyString(2));
        args.put("overdue_managefee", overdueManagerAmount.toMoneyString(2));
        args.put("overdue_principal", overduePrincipalAmount.toMoneyString(2));
        // 加载什么位置, 好像7.4批量的顺序和现在的顺序不对 [wangfeng on 16/4/12 下午4:10]
        args.put("overdue_basefee", baseManageFee.toMoneyString(2));
        args.put("overdue_specfee", specManageFee.toMoneyString(2));
        //以下为默认内容
        args.put("overdue_latefee", "");
        args.put("overdue_lawfee", "");
        args.put("backwash", "");
        args.put("repay_amt", "");
        args.put("repay_date", "");
        args.put("repay_sum_amt", "");
        args.put("out_in", Api.toString(flag));
        args.put("memo", "");

        // 输出 单行信息
        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);

        return sRet;
    }

    public Map<String, String> productBaseMap(Map<String, String> args) {
        //String entryUuid = product.getEntryUuid();
        String entryUuid = fromUser;
        //String uuid = product.getUuid();
        String uuid = bidUuid;
        //String pCode = product.getpCode();
        String pCode = uuid;//productCode;
        //String productCode = product.getProductCode();
        //int level = getProductLevel(productCode);
        //String rate = product.getRate();
        String rate = this.rate.toString();
        //Date createTime = product.getCreateTime();
        //Date endTime = product.getEndTime();
        //String createDateStr = DateUtil.convertDateToStr(createTime, DDF);
        String createDateStr = Api.toString(saveDbTime, TradeDataLine.DDF);
        //String endDateStr = DateUtil.convertDateToStr(endTime, DDF);
        String endDateStr = Api.toString(endTime, TradeDataLine.DDF);
        //String amount = product.getAmount();
        Money amount = BaseContext.valueOf(planAmount);
        //Money avaliable = BaseContext.valueOf(product.getAvaliable(), "0");
        Money avaliable = BaseContext.valueOf(notYetPrincipal);
        //Money curAmount = BaseContext.valueOf(product.getCurrentAmount(), "0");
        //String totalLendAmount = curAmount.minus(avaliable).toMoneyString();
        Money totalLendAmount = BaseContext.valueOf(realAmount);
        //int term = product.getTerm();
        int term = maxOverdueDay;
        //Money interest = InterestUtil.computeBorrowInterestPercentM(totalLendAmount, rate, term);
        Money interest = BaseContext.valueOf(notYetInterest);

        // 标的标号
        args.put("id", productCode);
        // UUID
        args.put("UUID", uuid);
        // 赚利差父节点
        args.put("Pcode", pCode);
        // 债务人客户编号
        args.put("person_id", entryUuid);
        // 出借类型（利差层数）
        args.put("lend_type", String.valueOf(layer));
        // 贷款类型
        args.put("loan_type", "1");
        // 年利率
        args.put("rateey", rate);
        // 标的申请日期
        args.put("request_date", createDateStr);
        // 标的金额
        args.put("pact_money", amount.toMoneyString(2));
        // 放款金额
        args.put("grant_money", totalLendAmount.toMoneyString(2));
        args.put("time", String.valueOf(term));
        // 利息金额
        args.put("pact_interest", interest.toMoneyString(2));
        // 约定还款日
        args.put("promise_return_date", endDateStr);
        // 剩余本金
        args.put("residual_pact_money", avaliable.toMoneyString(2));

        return args;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[0];
        return csvFilename;
    }

    @Override
    public void plus(DebtInfo debt) {
        // 如果债务保护开关打开, 且债务清偿时间(毫秒)大于0, 则不累加债务数据
        boolean debtProtection = Runtime.hasDebtProtection && debt.debt_clear_time > 0;
        Money overdueAmount = BaseContext.valueOf(debt.total_amount);
        overdueAmount = overdueAmount.minus(BaseContext.valueOf(debt.repay_amount));
        boolean bOverdue = debt.isOverdue();
        bidUuid = debt.bid_uuid; // bigint 30 主键
        // 债务人客户编号 fromUser bigint    20
        fromUser = debt.fromUser;
        // 产品编码 productCode varchar 255
        // table: product.productcode
        productCode = debt.product_code;
        // 利差层数 layer tinyint 3 债务人处在第几层利差
        layer = 0; // TODO: 债务没有利差层数
        // 借款年利率 rate decimal(4,2) 年利率
        // table: product.rate
        rate = debt.rate;
        // 金额 planAmount decimal(14,2) 金额
        // (原标的计划借款金额)
        // table: product.currentamount(???)
        //public BigDecimal planAmount;
        // 实际借到金额 realAmount decimal(14,2)
        // 实际上借到的金额
        // table: product.currentamount-product.avaliable(???)
        //public BigDecimal realAmount;
        principal = principal.add(debt.principal);
        repay_principal = repay_principal.add(debt.repay_principal);
        Money tmp = Money.of(debt.principal);
        if(bOverdue) realAmount = realAmount.add(tmp.toStoreDecimal());
        // 借款用途 remark varchar(n) 255 默认为''
        //remark = "";
        // 约定还款日 endTime Timestamp
        // table: product.endtime
        if (Api.isEmpty(endTime) || (!Api.isEmpty(debt.end_time) && endTime.before(debt.end_time))) {
            endTime = debt.end_time;
        }
        // 逾期天数 maxOverdueDay Smallint 最大逾期天数
        //public int maxOverdueDayRaw;
        if (maxOverdueDayRaw < debt.overdue_day) {
            maxOverdueDayRaw = debt.overdue_day;
        }
        // 逾期起始日 overdueStartDay Timestamp
        // table: product.endtime+1
        Date tmpOverdueStartDay = null;
        if (!Api.isEmpty(debt.end_time)) {
            tmpOverdueStartDay = Api.addDate(debt.end_time, Calendar.DAY_OF_YEAR, 1);
        }
        if (Api.isEmpty(overdueStartDay) || (!Api.isEmpty(tmpOverdueStartDay) && !overdueStartDay.before(tmpOverdueStartDay))) {
            overdueStartDay = tmpOverdueStartDay;
        }
        // 未还利息(利息＋逾期利息 notYetInterest decimal(14,2)
        // table: transactionflow.notyetinterest
        // 催收员作业用
        interest = interest.add(debt.interest);
        repay_interest = repay_interest.add(debt.repay_interest);
        tmp = BaseContext.valueOf(debt.interest);
        tmp = tmp.minus(BaseContext.valueOf(debt.repay_interest));
        if(bOverdue) notYetInterest = notYetInterest.add(tmp.toStoreDecimal());

        // 未还 逾期利息
        overdue_interest = overdue_interest.add(debt.overdue_interest);
        repay_overdue_interest = repay_overdue_interest.add(debt.repay_overdue_interest);
        tmp = BaseContext.valueOf(debt.overdue_interest);
        tmp = tmp.minus(BaseContext.valueOf(debt.repay_overdue_interest));
        if(bOverdue) notYetOverdueInterest = notYetOverdueInterest.add(tmp.toStoreDecimal());
        // 未还本金 notYetPrincipal decimal(14,2)
        // table: transactionFlow.notyetprincipal
        // 催收员作业用
        tmp = BaseContext.valueOf(debt.principal);
        tmp = tmp.minus(BaseContext.valueOf(debt.repay_principal));
        if(bOverdue) notYetPrincipal = notYetPrincipal.add(tmp.toStoreDecimal());
        // 结清标识 flag tinyint 2
        // table: repay_flow.total_amount-repay_amount>0 and overdue_day>0，逾期未结清
        //int tmpFlag = (debt.payStatus != 4 && debt.payStatus!=7 && debt.overdue_day > 0) ? 0 : 1;
        // 未逾期  0x00
        // 逾期    0x01
        // 结清    0x02
        int tmpFlag = 0x00;
        if (debt.overdue_day > 0) {
            tmpFlag |= 0x01;
        }
        if (debt.debt_clear_time > 0) {
            tmpFlag |= 0x02;
        }
        flag &= tmpFlag;
        // 利息金额 decimal(14,2) 确定这个字段是否有用(??)

        // 标的基本逾期管理费 overdueBasefee decimal(14,2)
        // table: overdueFee.getFees()- overdueFee.getRepayFees()
        // 催收员作业用
        overdue_base_fee = overdue_base_fee.add(debt.overdue_base_fee);
        repay_overdue_base_fee = repay_overdue_base_fee.add(debt.overdue_base_fee_payed);
        Money lastBaseManager = BaseContext.valueOf(debt.overdue_base_fee);
        lastBaseManager = lastBaseManager.minus(BaseContext.valueOf(debt.overdue_base_fee_payed));
        // 标的特殊逾期管理费 overdueSpecfee decimal(14,2)
        // table: overdueFee.getSpecialFees()- overdueFee.getRepaySpecialFees()
        // 催收员作业用
        overdue_spec_fee = overdue_spec_fee.add(debt.overdue_spec_fee);
        repay_overdue_spec_fee = repay_overdue_spec_fee.add(debt.overdue_spec_fee_payed);
        Money lastSpecManager = BaseContext.valueOf(debt.overdue_spec_fee);
        lastSpecManager = lastSpecManager.minus(BaseContext.valueOf(debt.overdue_spec_fee_payed));

        // 逾期总额 overdueTotalAmount decimal(14,2)
        // 未还本息+逾期管理费：
        // (repay_flow.total_amount(还款总金额=金额(本金+利息)＋逾期利息)-repay_flow.repay_amount(还款金额)
        // +overdue_fees.sum_fees-overdue_fees.repay_sum_fees)
        // 催收员作业用
        // 如果债务保护开关打开, 且本利罚还清, 逾期管理费强制为0
        if(!bOverdue)  {
            overdueAmount = BaseContext.valueOf("0.00");
            lastBaseManager = BaseContext.valueOf("0.00");
            lastSpecManager = BaseContext.valueOf("0.00");
        }
        //logger.error("bid={},amount={},total={},repay={}", bidUuid, overdueTotalAmount.toPlainString(), debt.total_amount, debt.repay_amount);
        overdueBasefee = overdueBasefee.add(lastBaseManager.toStoreDecimal());
        overdueSpecfee = overdueSpecfee.add(lastSpecManager.toStoreDecimal());

        overdueTotalAmount = overdueTotalAmount.add(overdueAmount.toStoreDecimal());
        //overdueTotalAmount = overdueTotalAmount.add(lastBaseManager.toStoreDecimal());
        //overdueTotalAmount = overdueTotalAmount.add(lastSpecManager.toStoreDecimal());
        //logger.error("bid={},amount={}", bidUuid, overdueTotalAmount.toPlainString());
        // 入库时间 saveDbTime timestamp
        if (Api.isEmpty(saveDbTime) || saveDbTime.after(debt.create_time)) {
            saveDbTime = debt.create_time;
        }
        // 入库后最后修改时间 lastUpdateTime timestamp
        if (Api.isEmpty(lastUpdateTime) || lastUpdateTime.before(debt.update_time)) {
            lastUpdateTime = debt.update_time;
        }
        clearTime = debt.cleanTime;
    }
}
