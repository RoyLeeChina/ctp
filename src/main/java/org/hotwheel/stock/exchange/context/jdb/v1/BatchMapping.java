package org.hotwheel.stock.exchange.context.jdb.v1;

/**
 * 批量数据映射
 *
 * Created by wangfeng on 16/8/27.
 */
public final class BatchMapping {

    /**
     * 批量文件
     */
    public final static String[] files = {
            "overdueBidsInfo.csv",       // 债务标的
            "overdueCreditorOrders.csv", // 债权人订单
            "overdueRepayFlow.csv",      // 还款流水
            "overdueAgreements.csv",     // 诉讼协议
            "RRC_PAY.in",               // 支付中心交换文件
            //"RRC_PAY.out",               // 支付中心交换文件
            "overdueHacked.csv",         // 债务人被冒用标识
            "overdueDebtorContacts.csv", // 债务人联系人
            "phoneList.csv",             // 通讯录
            "overdueEntryUuids.csv",     // 债务人信息
            "creditorInfos.csv"          // 债权人信息
    };

    public static final String[][] infos = {
            // 借款标的信息
            //{"id","UUID","Pcode","person_id","lend_type","FST_REPAY_DATE","END_REPAY_DATE","loan_due_date","loan_type","loan_state","rateem","rateey","request_date","grant_money_date","pact_money","pact_interest","grant_money","time","purpose","ledger_money","promise_return_date","residual_pact_money","residual_time","overdue_terms","overdue_days","overdue_date","overdue_amount","overdue_interest","overdue_principal","overdue_penaty","overdue_managefee","overdue_basefee","overdue_specfee","overdue_latefee","overdue_lawfee","backwash","repay_amt","repay_date","repay_sum_amt","out_in","memo"},
            {"id","UUID","Pcode","person_id","lend_type","FST_REPAY_DATE","END_REPAY_DATE","loan_due_date","loan_type","loan_state","rateem","rateey","request_date","grant_money_date","pact_money","pact_interest","grant_money","time","purpose","ledger_money","promise_return_date","residual_pact_money","residual_time","overdue_terms","overdue_days","overdue_date","overdue_amount","overdue_interest","overdue_principal","overdue_penaty","overdue_managefee","overdue_basefee","overdue_specfee","overdue_latefee","overdue_lawfee","backwash","repay_amt","repay_date","repay_sum_amt","out_in","memo"},
            // 债权人-订单表
            {"cust_id","order_id","productid","creditor_name","creditor_phone","lend_principal","lend_bal","payment_amt","lend_days","lend_real_days","lend_date","lend_penaty","lend_rate","memo","isEarn","overDuePrincipal","orderInterest","overDueInterest","overDuePenaty","orderManagefee","overdue_basefee","overdue_specfee","bid_uuid","tbid_uuid","real_orderid","uuids"},
            // 还款流水
            {"id","product_id","trade_no","person_id","loan_id","trade_date","trade_time","subject_name","tradeTerm","tranAmt","sub_type","trade_type","trade_method","memo","isDelete","isVirtual","timeRange"},
            // 诉讼协议
            {"loanAgreementId","bid_uuid","tbid_uuid","transferAgreementId","transferAgreementTime","orderId","isOverDue","creditorCustNo","debetorCustNo","lendDate","loanYearRate","repayType","loanDueDate","overDueDate","overDueDays","contractAmt","baseOverDueManagerFee","specOverDueManagerFee","overDuePenaty","overDueInterest","interestRate","payContractAmt","payBaseOverDueManagerFee","paySpecOverDueManagerFee","payOverDuePenaty","payOverDueInterest","unPayContractAmt","unPayBaseOverDueManagerFee","unPaySpecOverDueManagerFee","unPayOverDuePenaty","unPayOverDueInterest"},
            // JDB YFT Mapping
            {"jdb_id","yft_id"},
            //{"jdb_id","yft_id","x1","idCode","amount","x2"},
            // 债务人-身份被冒用
            {"cust_id","hacked_flag"},
            // 债务人-联系人
            {"contact_id","cust_id","name","relation","mobile_nbr"},
            // 债务人-通讯录
            {"entryUuid","contactName","contactMobile"},
            // 客户信息
            {"id","name","idtype","idnum","sex","birthday","married","ed_level","graduation","college","phone_nbr","custStatus","custIstMarginStatus","businessResource","resourceName","resourcePhone","ca_company","ca_type","ca_industry_type","ca_department","ca_duty","ca_official_rank","ca_enter_time","ca_pay_type","ca_pay_day","ca_wage","communicate_address","company_address","car_type","car_buy_time","car_has_loan","car_price","car_valuation","house_type","house_address","house_valuation","house_has_loan","house_building_area","house_buy_time","house_loan_balance","house_price","comp_type","comp_time_founded","comp_shareholding_ratio","comp_premises_type","comp_employee_amount","comp_profits","emergency_contact","emergency_mobile","memo"}
    };

    public static final String[] valueTemp = {
            // 借款标的信息
            "${id},${UUID},${Pcode},${person_id},${lend_type},${FST_REPAY_DATE},${END_REPAY_DATE},${loan_due_date},${loan_type},${loan_state},${rateem},${rateey},${request_date},${grant_money_date},${pact_money},${pact_interest},${grant_money},${time},${purpose},${ledger_money},${promise_return_date},${residual_pact_money},${residual_time},${overdue_terms},${overdue_days},${overdue_date},${overdue_amount},${overdue_interest},${overdue_principal},${overdue_penaty},${overdue_managefee},${overdue_basefee},${overdue_specfee},${overdue_latefee},${overdue_lawfee},${backwash},${repay_amt},${repay_date},${repay_sum_amt},${out_in},${memo}",
            //"${id},${UUID},${Pcode},${person_id},${lend_type},${FST_REPAY_DATE},${END_REPAY_DATE},${loan_due_date},${loan_type},${loan_state},${rateem},${rateey},${request_date},${grant_money_date},${pact_money},${pact_interest},${grant_money},${time},${purpose},${ledger_money},${promise_return_date},${residual_pact_money},${residual_time},${overdue_terms},${overdue_days},${overdue_date},${overdue_amount},${overdue_interest},${overdue_principal},${overdue_penaty},${overdue_managefee},${overdue_basefee},${overdue_specfee},,,,,,,,",
            // 债权人=订单表
            "${cust_id},${order_id},${productid},${creditor_name},${creditor_phone},${lend_principal},${lend_bal},${payment_amt},${lend_days},${lend_real_days},${lend_date},${lend_penaty},${lend_rate},${memo},${isEarn},${overDuePrincipal},${orderInterest},${overDueInterest},${overDuePenaty},${orderManagefee},${overdue_basefee},${overdue_specfee},${bid_uuid},${tbid_uuid},${real_orderid},${uuids}",
            // 还款流水
            "${id},${product_id},${trade_no},${person_id},${loan_id},${trade_date},${trade_time},${subject_name},${tradeTerm},${tranAmt},${sub_type},${trade_type},${trade_method},${memo},${isDelete},${isVirtual},${timeRange}",
            // 诉讼协议
            "${loanAgreementId},${bid_uuid},${tbid_uuid},${transferAgreementId},${transferAgreementTime},${orderId},${isOverDue},${creditorCustNo},${debetorCustNo},${lendDate},${loanYearRate},${repayType},${loanDueDate},${overDueDate},${overDueDays},${contractAmt},${baseOverDueManagerFee},${specOverDueManagerFee},${overDuePenaty},${overDueInterest},${interestRate},${payContractAmt},${payBaseOverDueManagerFee},${paySpecOverDueManagerFee},${payOverDuePenaty},${payOverDueInterest},${unPayContractAmt},${unPayBaseOverDueManagerFee},${unPaySpecOverDueManagerFee},${unPayOverDuePenaty},${unPayOverDueInterest}",
            // JDB YFT Mapping
            //"${jdb_id},${yft_id},${x1},${idCode},${amount},${x2}",
            "${jdb_id},${yft_id}",
            // 债务人-身份被冒用
            "${cust_id},${hacked_flag}",
            // 债务人-联系人
            "${contact_id},${cust_id},${name},${relation},${mobile_nbr}",
            // 债务人-通讯录
            "${entryUuid},${contactName},${contactMobile}",
            // 客户信息
            "${id},${name},${idtype},${idnum},${sex},${birthday},${married},${ed_level},${graduation},${college},${phone_nbr},${custStatus},${custIstMarginStatus},${businessResource},${resourceName},${resourcePhone},${ca_company},${ca_type},${ca_industry_type},${ca_department},${ca_duty},${ca_official_rank},${ca_enter_time},${ca_pay_type},${ca_pay_day},${ca_wage},${communicate_address},${company_address},${car_type},${car_buy_time},${car_has_loan},${car_price},${car_valuation},${house_type},${house_address},${house_valuation},${house_has_loan},${house_building_area},${house_buy_time},${house_loan_balance},${house_price},${comp_type},${comp_time_founded},${comp_shareholding_ratio},${comp_premises_type},${comp_employee_amount},${comp_profits},${emergency_contact},${emergency_mobile},${memo}"
    };
}
