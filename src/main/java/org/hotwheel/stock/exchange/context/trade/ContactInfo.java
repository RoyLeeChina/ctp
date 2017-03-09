package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Date;
import java.util.Map;

/**
 * 联系人
 * http://api.jdb-dev.com/friendapi/follow_inner/getFollowList.html
 * http://api.jdb-dev.com/passportapi/inner/getulist.html
 * 无联系人姓名和手机号码
 */
public class ContactInfo extends TradeDataLine {
    public String userId = "";
    //"memberID": "566631140618674423",
    public String memberId = "";
    // 好友姓名
    public String memberName = "";
    // 好友手机号码
    public String memberPhone = "";
    // "followStatus": 1-关注状态，0 没有关注，1 curMemberID关注了memberID，2 memberID关注了curMemberID，3 互相关注。
    public int followStatus;
    // "remark": "", // 申请时文案
    public String remark = "";
    // "source": 0, // 来源
    public String source = "";
    // "remarkName": "", // 备注名称
    public String remarkName = "";
    // "allowWatchMe": 1, // 1允许TA看我 0 不允许TA看我
    public int allowWatchMe;
    // "heWatchMe": 1, // 他是否看我的借款（1.要看，0.不要看）
    public int heWatchMe;
    // "watchHim": 1, // 1 看他的借款 0 不看他的借款'
    public int watchHim;
    // "heAllowWatch": 1,  // 他是否同意我看他的借款（1.要看，0.不要看）
    public int heAllowWatch;
    // "createTime": 0, // 创建时间
    public Date createTime;
    // "sourceTitle":"我的手机通讯录"
    public String sourceTitle = "";

    @Override
    public String toLine() {
        String sRet = null;
        final int index = 6;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);
        args.put("cust_id", userId);
        args.put("contact_id", memberId);
        args.put("name", memberName);
        args.put("mobile_nbr", memberPhone);
        args.put("relation", "好友");

        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);
        return sRet;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[6];
        return csvFilename;
    }
}
