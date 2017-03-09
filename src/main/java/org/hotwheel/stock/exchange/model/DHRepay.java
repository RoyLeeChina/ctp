package org.hotwheel.stock.exchange.model;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v2.RepayContext;
import org.hotwheel.stock.exchange.context.trade.RepayInfo;
import org.mymmsc.api.assembly.Api;

/**
 * Created by wangfeng on 2017/1/9.
 */
public class DHRepay implements RepayContext {
    public String amount;//null,
    public String bidId;//:"1",
    public long createTime;//null,
    public String creditorId;//:111,
    public String debtId;//:"1",
    public String debtorId;//1,
    public String interest;//null,
    public long lastUpdateTime;//null,
    public String lawFee;//null,
    public String overdueFee;//null,
    public String penaltiesInterest;//null,
    public String principal;//null,
    public String remark;//null,
    public String repayId;//"1",
    public String repayMethod;//:null,
    public long repayTime;//null,
    public long repayType;//null,
    public long saveTime;//null,
    public int status;//null,
    public long updateTime;//null

    public static DHRepay valueOf(RepayInfo obj) {
        DHRepay repay = new DHRepay();
        repay.parse(obj);
        return repay;
    }

    @Override
    public boolean parse(RepayInfo repay) {
        boolean bRet = false;

        //public String amount;//null,
        amount = BaseContext.toString(repay.amount);
        //public String bidId;//:"1",
        bidId = repay.bidUuid;
        //public long createTime;//null,
        createTime = BaseContext.getTime(repay.createTime);
        //public String creditorId;//:111,
        creditorId = repay.toUser;
        //public String debtId;//:"1",
        debtId = repay.debtId;
        //public String debtorId;//1,
        debtorId = repay.fromUser;
        //public String interest;//null,
        interest = BaseContext.toString(repay.repayInterest);
        //public long lastUpdateTime;//null,
        lastUpdateTime = BaseContext.getTime(repay.lastUpdateTime);
        //public String lawFee;//null,
        //public String overdueFee;//null,
        overdueFee = BaseContext.toString(repay.repayOverduefees);
        //public String penaltiesInterest;//null,
        penaltiesInterest = BaseContext.toString(repay.repayOverdueInterest);
        //public String principal;//null,
        principal = BaseContext.toString(repay.repayPrincipal);
        //public String remark;//null,
        repayId = repay.uuid;
        //public String repayMethod;//:null,
        repayMethod = Api.toString(repay.tradeMethod);
        //public long repayTime;//null,
        repayTime = BaseContext.getTime(repay.createTime);
        //public long repayType;//null,
        repayType = repay.subType;
        //public long saveTime;//null,
        saveTime = BaseContext.getTime(repay.createTime);
        //public int status;//null,
        //status = repay.s
        //public long updateTime;//null
        updateTime = BaseContext.getTime(repay.lastUpdateTime);

        bRet = true;
        return bRet;
    }
}
