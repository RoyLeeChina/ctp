package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mymmsc.api.assembly.Api;

import java.util.Map;

/**
 * 身份冒用 单行数据结构
 *
 * Created by wangfeng on 16/9/18.
 * @since 2.1.0
 * <p>
 *  {"errno":0,"errmsg":"Ok.","data":{"uploadStatus":1,"collectionStatus":1,"collectionRemark":""}}
 *  uploadStatus: 上传征信状态（1，未确认，2需上传，3无需上传）
 * collectionStatus: 是否需要催收的状态（1-未确认,2-需催收,3-无需催收）
 * </p>
 */
public class HackedInfo extends TradeDataLine {
    public String memberId = "";
    public String uploadStatus = "";
    public String collectionStatus = "";
    public String collectionRemark = "";

    @Override
    public String toLine() {
        String sRet = null;
        final int index = 5;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);

        int cs = Api.valueOf(int.class, collectionStatus);
        if(cs == 3) {
            collectionStatus = "1";
        } else {
            collectionStatus = "0";
        }

        args.put("cust_id", memberId);
        args.put("hacked_flag", collectionStatus);
        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);
        return sRet;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[5];
        return csvFilename;
    }
}
