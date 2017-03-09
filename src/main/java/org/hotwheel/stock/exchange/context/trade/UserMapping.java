package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mymmsc.api.assembly.BeanAlias;

import java.util.Map;

/**
 * 借贷宝id映射支付Id
 *
 * Created by wangfeng on 2016/11/21.
 * @since 2.1.0
 * @remark 464556771327873024,110000043072,00,150202198002011620,10901089,
 */
public class UserMapping extends TradeDataLine{
    @BeanAlias("jdb_id")
    public String memberId;
    @BeanAlias("jbs_id")
    public String yftId = "";
    @BeanAlias("ptp_id")
    public String ptpId = "";
    public String phone = "";
    public String x1 = "00";
    public String idCode = "";
    public String amount = "0";
    public String x2 = "";

    @Override
    public String toLine() {
        String sRet = null;
        final int  index = 4;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);
        args.put("jdb_id", memberId);
        args.put("yft_id", yftId);
        // 以下是新增部分
        //"x1","idCode","amount","x2"
        args.put("x1", x1);
        args.put("idCode", idCode);
        args.put("amount", amount);
        args.put("x2", x2);

        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);

        return sRet;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[4];
        return csvFilename;
    }
}
