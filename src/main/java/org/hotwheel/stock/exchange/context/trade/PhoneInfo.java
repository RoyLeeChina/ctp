package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mymmsc.api.assembly.BeanAlias;

import java.util.Map;

/**
 * 通讯录
 *
 * Created by wangfeng on 2016/11/18.
 * @since 2.1.0
 */
public class PhoneInfo extends TradeDataLine{
    public String uuid = "";
    @BeanAlias("entryUserName")
    public String name = "";

    @BeanAlias("entryMobile")
    public String mobile = "";

    public String tags = "";

    @Override
    public String toLine() {
        String sRet = null;
        final int index = 7;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);
        args.put("entryUuid", uuid);
        args.put("contactMobile", mobile);
        String entryUserName = name;
        args.put("contactName", entryUserName.replaceAll("\r|\n", ""));
        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);
        return sRet;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[7];
        return csvFilename;
    }
}
