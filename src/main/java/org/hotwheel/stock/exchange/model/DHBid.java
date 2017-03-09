package org.hotwheel.stock.exchange.model;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v2.BidContext;
import org.hotwheel.stock.exchange.context.trade.ProductInfo;

/**
 * 标的
 * Created by wangfeng on 2017/1/9.
 * @since 1.0.0
 */
public class DHBid implements BidContext {
    public String bidId;//1",
    public String code;//null,
    public long createTime;//null,
    public String debtorId;//1,
    public long endTime;//null,
    public long expiredTime;//null,
    public int flag;//null,
    //public String id;//null,
    //public String isdelete;//null,
    public long lastUpdateTime;//null,
    public int layer;//null,
    public String interest;//null,
    public String lawFee;//null,
    public String overdueFee;//null,
    public String penaltiesInterest;//null,
    public String principal;//null,
    // TODO 催收费记费时间,到期日+宽限期
    public String overdueFeeTime;//null,
    public String parentCode;//null,
    public String planAmount;//null,
    public String rate;//null,
    public String realAmount;//null,
    public String remark;//null,
    public String repayInterest;//null,
    public String repayLawFee;//null,
    public String repayOverdueFee;//null,
    public String repayPenaltiesInterest;//null,
    public String repayPrincipal;//null,
    public long saveTime;//null,
    public long settledTime;//null,
    public long startTime;//null,
    public String status;//null,
    public long updateTime;//null
    public int maxOverdueDays = 0;

    public static DHBid valueOf(ProductInfo product) {
        DHBid bid = new DHBid();
        bid.parse(product);
        return bid;
    }

    public boolean parse(ProductInfo product) {
        boolean bRet = false;
        bidId = product.bidUuid;
        code = product.productCode;
        createTime = BaseContext.getTime(product.saveDbTime);;
        debtorId = product.fromUser;
        //public String endTime;//null,
        endTime = BaseContext.getTime(product.endTime);
        //public String expiredTime;//null,
        expiredTime = endTime;
        //public String flag;//null,
        flag = product.flag;
        //public String id;//null,
        //public String isdelete;//null,
        lastUpdateTime = BaseContext.getTime(product.lastUpdateTime);
        //public String layer;//null,
        layer = product.layer;
        // String interest;//null,
        interest = BaseContext.toString(product.interest);
        //public String lawFee;//null,
        //public String overdueFee;//null,
        overdueFee = BaseContext.toString(product.overdue_base_fee.add(product.overdue_spec_fee));
        //public String penaltiesInterest;//null,
        penaltiesInterest = BaseContext.toString(product.overdue_interest);
        //public String principal;//null,
        principal = BaseContext.toString(product.principal);
        //public String overdueFeeTime;//null,
        //public String parentCode;//null,
        //public String planAmount;//null,
        planAmount = BaseContext.toString(product.planAmount);
        //public String rate;//null,
        rate = BaseContext.toString(product.rate);
        //public String realAmount;//null,
        realAmount = BaseContext.toString(product.realAmount);
        //public String remark;//null,
        remark = product.remark;
        //public String repayInterest;//null,
        repayInterest = BaseContext.toString(product.repay_interest);
        //public String repayLawFee;//null,
        //public String repayOverdueFee;//null,
        repayOverdueFee = BaseContext.toString(product.repay_overdue_base_fee.add(product.repay_overdue_spec_fee));
        //public String repayPenaltiesInterest;//null,
        repayPenaltiesInterest = BaseContext.toString(product.repay_overdue_interest);
        //public String repayPrincipal;//null,
        repayPrincipal = BaseContext.toString(product.repay_principal);
        //public String saveTime;//null,
        saveTime = BaseContext.getTime(product.saveDbTime);
        //public String settledTime;//null,
        settledTime = BaseContext.getTime(product.clearTime);
        //public String startTime;//null,
        startTime = saveTime;
        //public String status;//null,
        updateTime = BaseContext.getTime(product.lastUpdateTime);

        maxOverdueDays = product.maxOverdueDay;

        bRet = true;
        return bRet;
    }
}
