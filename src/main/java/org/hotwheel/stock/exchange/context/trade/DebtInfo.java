package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.hotwheel.stock.exchange.model.Money;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.BeanAlias;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债务
 * Created by wangfeng on 2016/12/7.
 * @since 3.0.1
 */
public class DebtInfo extends TradeDataLine{
    // 债务id
    public String uuid; //481169400842436609
    public String link_uuid; //481169400842436608
    public String laundryUuid; //481169399017914368
    // 债务人
    public String fromUser; //480695998503473152
    // 债权人
    public String toUser; //478495978014396416
    // 债务总额, 本利罚
    public BigDecimal total_amount = new BigDecimal(0.00); //135.71
    public BigDecimal amount = new BigDecimal(0.00); //101.00
    // 本金
    public BigDecimal principal = new BigDecimal(0.00); //100.00
    // 利息
    public BigDecimal interest = new BigDecimal(0.00); //1.00
    // 罚息
    public BigDecimal overdue_interest = new BigDecimal(0.00); //34.71
    // 年化利率
    public BigDecimal rate = new BigDecimal(0.00); //30.00
    // 延期天数
    public int overdue_day = -1;// 405
    // 债权人赚利差
    public int is_earn_as_from_user; //0,
    // 债权人赚利差
    public int is_earn_as_to_user;// 1
    // 支付状态
    public int payStatus; //1
    public String product_code; //2000000003776
    public String bid_uuid; //4811635887911813120001
    public String tbid_uuid; //481169399017914368
    // 还款额
    public BigDecimal repay_amount = new BigDecimal(0.00); //0.00
    // 还本息
    public BigDecimal repay_principal = new BigDecimal(0.00); //0.00
    // 还利息
    public BigDecimal repay_interest = new BigDecimal(0.00); //0.00
    // 还罚息
    public BigDecimal repay_overdue_interest = new BigDecimal(0.00); //0.00
    public Date start_time; //1435401324000
    public Date end_time; //1446047999000
    public Date create_time; //1435401324000
    public Date update_time; //1480662827000
    // 原始本金
    public BigDecimal original_principal = new BigDecimal(0.00); //100.00
    // 原始利息
    public BigDecimal original_interest = new BigDecimal(0.00); //10.10
    // 担保人uuid
    public String gurantee_uuid; //
    public String source_bid_uuid; //4811635887911813120001
    // 债务是否清偿
    public long debt_clear_time; //0
    public BigDecimal overdue_base_fee = new BigDecimal(0.00); //0.28
    public BigDecimal overdue_spec_fee = new BigDecimal(0.00); //0.00
    public BigDecimal overdue_base_fee_payed = new BigDecimal(0.00); //0.00
    public BigDecimal overdue_spec_fee_payed = new BigDecimal(0.00); //0.00

    public int lawStatus;
    public Date lawTime;
    @BeanAlias("debt_clear_time")
    public Date cleanTime;

    @Override
    public String toLine() {
        String sRet = "";
        final int index = 3;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);

        String loanAgreementId = uuid;
        String transferAgreementId = "";
        String transferAgreementTime = "";
        if(lawStatus > 0) {
            transferAgreementId = uuid + "03";
            transferAgreementTime = Api.toString(lawTime, DDF);
        }
        String orderId = laundryUuid;
        String isOverDue = "1";
        String creditorCustNo = toUser;
        String debetorCustNo = fromUser;
        String lendDate = Api.toString(start_time, DDF);
        String loanYearRate = BaseContext.format(rate);
        String repayType = "";//repayFlow.getType(); // 默认, 一次性本息还款
        if(Api.isEmpty(repayType)) {
            repayType = "1";
        }
        Date endTime = end_time;
        String loanDueDate = Api.toString(endTime, DDF);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String overDueDate = Api.toString(calendar.getTime(), DDF);
        String overDueDays = Api.toString(overdue_day);
        // 合同金额, 原始本金
        String contractAmt = BaseContext.format(original_principal);
        if(Api.isEmpty(contractAmt)) {
            contractAmt = BaseContext.format(principal);
        }
        String baseOverDueManagerFee = "0.00";
        String specOverDueManagerFee = "0.00";
        // 罚息
        String overDuePenaty = BaseContext.format(overdue_interest);
        // 逾期利息
        String overDueInterest = BaseContext.format(interest);
        String interestRate = BaseContext.format(rate);//repayFlow.getOverdueRate();

        // 已还 合同金额, 本金部分
        String payContractAmt = "0.00";
        // 已还 基础逾期管理费
        String payBaseOverDueManagerFee = "0.00";
        // 已还 特别逾期管理费
        String paySpecOverDueManagerFee = "0.00";
        // 已还 罚息
        String payOverDuePenaty = "0.00";
        // 延期利息
        String payOverDueInterest = "0.00";
        // 未还 合同金额
        String unPayContractAmt = "0.00";
        // 未还 基础逾期管理费
        String unPayBaseOverDueManagerFee = "0.00";
        // 未还 特别逾期管理费
        String unPaySpecOverDueManagerFee = "0.00";
        // 未还 罚息
        String unPayOverDuePenaty = "0.00";
        // 未还 逾期利息
        String unPayOverDueInterest = "0.00";
        // 本息金额
        Money mAmount = Money.of(amount);

        //OverdueFees odf = overdueFeesDao.getOverdueFees(repayFlow.getUuid());
        //if(odf != null)
        {
            // 基本逾期管理费
            baseOverDueManagerFee = BaseContext.format(overdue_base_fee);
            // 特别逾期管理费
            specOverDueManagerFee = BaseContext.format(overdue_spec_fee);
            // 罚息, 已并入利息, 无法拆分
            //overDuePenaty = repayFlow.getOverdueInterest();
            // 利率
            //interestRate = odf.getFeesRate();
            // 已还合同金额
            payContractAmt = "0.00";
            payBaseOverDueManagerFee = BaseContext.format(overdue_base_fee_payed);
            paySpecOverDueManagerFee = BaseContext.format(overdue_spec_fee_payed);
            //payOverDuePenaty = odf.getSumFees();
            //payOverDueInterest = "0.00";
            // 以下均默认为0.00
            //unPayContractAmt = "0.00";
            unPayBaseOverDueManagerFee = BaseContext.format(overdue_base_fee.subtract(overdue_base_fee_payed));
            unPaySpecOverDueManagerFee = BaseContext.format(overdue_spec_fee.subtract(overdue_spec_fee_payed));
        }

        List<String> repayUuids = new ArrayList<String>();
        repayUuids.add(uuid);

        Money oPrincipal = Money.of(principal);
        Money oInterest = Money.of(interest);
        Money oOverdueInterest = Money.of(overdue_interest);
        // 还本金
        Money tmPrincipal = BaseContext.valueOf(0);;
        // 还利息
        Money tmPayInterest = BaseContext.valueOf(0);;
        //Money tmpayOverdueInterest = zero();

        /*
        List<TransactionInfo> tis = transactionInfoDao.getRepayInfoByOriginalTfUuids(repayUuids);
        for (int i=0,tSize=tis.size(); i<tSize; i++) {
            TransactionInfo ti = tis.get(i);
            Money payInterest = Money.of(ti.getRepayInterest(), "0.00");
            Money payAmount = Money.of(ti.getRepayAmount(), "0.00");
            Money payPrincipal = Money.of(ti.getRepayPrincipal(), "0.00");
            tmPayInterest = tmPayInterest.plus(payInterest);
            //tmPrincipal = tmPrincipal.plus(payAmount.minus(payInterest));
            tmPrincipal = tmPrincipal.plus(payPrincipal);
        }
        */
        payContractAmt = tmPrincipal.toMoneyString(2);
        unPayContractAmt = (oPrincipal.minus(tmPrincipal)).toMoneyString(2);
        // 罚息, 无法拆分
        payOverDuePenaty = "0.00";
        unPayOverDuePenaty = "0.00";
        payOverDueInterest = tmPayInterest.toMoneyString(2);
        unPayOverDueInterest = ((oInterest.plus(oOverdueInterest)).minus(tmPayInterest)).toMoneyString(2);

        Money ta = Money.of(total_amount);
        Money tr = Money.of(repay_amount);
        int overdueDays = overdue_day;
        if(ta.minus(tr).isZero() || overdueDays == 0) {
            isOverDue = "0";
        }
        args.put("bid_uuid", bid_uuid);
        args.put("tbid_uuid", tbid_uuid);
        args.put("loanAgreementId", loanAgreementId);
        args.put("transferAgreementId", transferAgreementId);
        args.put("transferAgreementTime", transferAgreementTime);
        args.put("orderId", orderId);
        args.put("isOverDue", isOverDue);
        args.put("creditorCustNo", creditorCustNo);
        args.put("debetorCustNo", debetorCustNo);
        args.put("lendDate", lendDate);
        args.put("loanYearRate", loanYearRate);
        args.put("repayType", repayType);
        args.put("loanDueDate", loanDueDate);
        args.put("overDueDate", overDueDate);
        args.put("overDueDays", overDueDays);
        args.put("contractAmt", contractAmt);
        args.put("baseOverDueManagerFee", baseOverDueManagerFee);
        args.put("specOverDueManagerFee", specOverDueManagerFee);
        args.put("overDuePenaty", overDuePenaty);
        args.put("overDueInterest", overDueInterest);
        args.put("interestRate", interestRate);
        args.put("payContractAmt", payContractAmt);
        args.put("payBaseOverDueManagerFee", payBaseOverDueManagerFee);
        args.put("paySpecOverDueManagerFee", paySpecOverDueManagerFee);
        args.put("payOverDuePenaty", payOverDuePenaty);
        args.put("payOverDueInterest", payOverDueInterest);
        args.put("unPayContractAmt", unPayContractAmt);
        args.put("unPayBaseOverDueManagerFee", unPayBaseOverDueManagerFee);
        args.put("unPaySpecOverDueManagerFee", unPaySpecOverDueManagerFee);
        args.put("unPayOverDuePenaty", unPayOverDuePenaty);
        args.put("unPayOverDueInterest", unPayOverDueInterest);

        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);
        return sRet;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[3];
        return csvFilename;
    }
/*
    @Override
    public boolean equals(Object obj) {
        boolean bRet = false;
        DebtInfo dest = (DebtInfo)obj;
        if (toString().equals(dest.toString())) {
            bRet = true;
        }
        return bRet;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "DebtInfo{" +
                "uuid='" + uuid + '\'' +
                ", link_uuid='" + link_uuid + '\'' +
                ", laundryUuid='" + laundryUuid + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", total_amount=" + total_amount +
                ", amount='" + amount + '\'' +
                ", principal='" + principal + '\'' +
                ", interest='" + interest + '\'' +
                ", overdue_interest='" + overdue_interest + '\'' +
                ", rate='" + rate + '\'' +
                ", overdue_day=" + overdue_day +
                ", is_earn_as_from_user='" + is_earn_as_from_user + '\'' +
                ", is_earn_as_to_user='" + is_earn_as_to_user + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", product_code='" + product_code + '\'' +
                ", bid_uuid='" + bid_uuid + '\'' +
                ", tbid_uuid='" + tbid_uuid + '\'' +
                ", repay_amount='" + repay_amount + '\'' +
                ", repay_principal='" + repay_principal + '\'' +
                ", repay_interest='" + repay_interest + '\'' +
                ", repay_overdue_interest='" + repay_overdue_interest + '\'' +
                ", start_time=" + start_time +
                ", end_time=" + end_time +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                ", original_principal='" + original_principal + '\'' +
                ", original_interest='" + original_interest + '\'' +
                ", gurantee_uuid='" + gurantee_uuid + '\'' +
                ", source_bid_uuid='" + source_bid_uuid + '\'' +
                ", debt_clear_time=" + debt_clear_time +
                ", overdue_base_fee='" + overdue_base_fee + '\'' +
                ", overdue_spec_fee='" + overdue_spec_fee + '\'' +
                ", overdue_base_fee_payed='" + overdue_base_fee_payed + '\'' +
                ", overdue_spec_fee_payed='" + overdue_spec_fee_payed + '\'' +
                ", lawStatus=" + lawStatus +
                ", lawTime=" + lawTime +
                '}';
    }
*/
    public boolean isOverdue() {
        // 如果债务保护开关打开, 且债务清偿时间(毫秒)大于0, 则不累加债务数据
        boolean debtProtection = Runtime.hasDebtProtection && debt_clear_time > 0;
        Money overdueAmount = BaseContext.valueOf(total_amount);
        overdueAmount = overdueAmount.minus(BaseContext.valueOf(repay_amount));
        boolean bOverdue = true;
        if(overdue_day == 0 || (debtProtection && overdueAmount.isZero())) {
            bOverdue = false;
        }
        return bOverdue;
    }
}
