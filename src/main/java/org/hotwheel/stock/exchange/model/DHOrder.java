package org.hotwheel.stock.exchange.model;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v2.OrderContext;
import org.hotwheel.stock.exchange.context.trade.DebtInfo;

/**
 * 贷后债务
 * Created by wangfeng on 2017/1/9.
 * @since 1.0.0
 */
public class DHOrder implements OrderContext {
    public String bidId;//1",
    public long createTime = 0;
    public String creditorId;//111,
    public int debtFlag = 0;//null,
    public String debtId;//1",
    public String debtorId;//1,
    public long expiredTime;//":1483588707408,
    public long lastUpdateTime;//null,
    public String laundryUuid;//null,
    public String lawFee;//null,
    public String lawFlag;//null,
    public long lawTime;//null,
    public long loanTime;//null,
    public String loanUuid;//null,
    public String interest;//11,
    public String LawFee;//:null,
    public String overdueFee;//null,
    public String penaltiesInterest;//null,
    public String principal;//11,
    public long  overdueFeeTime;//null,
    //public String princiapal;//null,
    public String rate;//null,
    public String remark;//null,
    public String repayInterest;//null,
    public String repayLawFee;//null,
    public String repayOverdueFee;//null,
    public String repayPenaltiesInterest;//null,
    public String repayPrincipal;//null,
    public long saveTime;//null,
    public long settledTime;//null,
    public int status;//:null,
    public long updateTime;//:null,
    public String uuids;//null
    public int collectFlag = 0;

    public static DHOrder valueOf(DebtInfo orderInfo) {
        DHOrder order = new DHOrder();
        order.parse(orderInfo);
        return order;
    }

    @Override
    public boolean parse(DebtInfo debt) {
        boolean bRet = false;

        //public String bidId;//1",
        bidId = debt.bid_uuid;
        //public long createTime = 0;
        createTime = BaseContext.getTime(debt.create_time);
        //public String creditorId;//111,
        creditorId = debt.toUser;
        //public int debtFlag;//null,
        //public String debtId;//1",
        debtId = debt.uuid;
        //public String debtorId;//1,
        debtorId = debt.fromUser;
        //public long expiredTime;//":1483588707408,
        expiredTime = BaseContext.getTime(debt.end_time);
        //public long lastUpdateTime;//null,
        lastUpdateTime = BaseContext.getTime(debt.update_time);
        //public String laundryUuid;//null,
        laundryUuid = debt.laundryUuid;
        //public String lawFee;//null,
        //public String lawFlag;//null,
        //public long lawTime;//null,
        //public long loanTime;//null,
        loanTime = BaseContext.getTime(debt.start_time);
        //public String loanUuid;//null,
        loanUuid = debt.tbid_uuid;
        //public String interest;//11,
        interest = BaseContext.toString(debt.interest);
        //public String LawFee;//:null,
        //public String overdueFee;//null,
        overdueFee = BaseContext.toString(debt.overdue_base_fee.add(debt.overdue_spec_fee));
        //public String penaltiesInterest;//null,
        penaltiesInterest = BaseContext.toString(debt.overdue_interest);
        //public String principal;//11,
        principal = BaseContext.toString(debt.principal);
        //public long  overdueFeeTime;//null,
        //public String princiapal;//null,
        //public String rate;//null,
        rate = BaseContext.toString(debt.rate);
        //public String remark;//null,
        remark = "临时周转";
        //public String repayInterest;//null,
        repayInterest = BaseContext.toString(debt.repay_interest);
        //public String repayLawFee;//null,
        //public String repayOverdueFee;//null,
        repayOverdueFee = BaseContext.toString(debt.overdue_base_fee_payed.add(debt.overdue_spec_fee_payed));
        //public String repayPenaltiesInterest;//null,
        repayPenaltiesInterest = BaseContext.toString(debt.repay_overdue_interest);
        //public String repayPrincipal;//null,
        repayPrincipal = BaseContext.toString(debt.repay_principal);
        //public long saveTime;//null,
        saveTime = BaseContext.getTime(debt.create_time);
        //public long settledTime;//null,
        settledTime = debt.debt_clear_time;
        //public String status;//:null,
        status = debt.payStatus;
        //public long updateTime;//:null,
        updateTime = BaseContext.getTime(debt.update_time);
        //public String uuids;//null
        uuids = debt.uuid;
        collectFlag = debt.isOverdue() ? 0 : 1;

        bRet = true;
        return false;
    }
}
