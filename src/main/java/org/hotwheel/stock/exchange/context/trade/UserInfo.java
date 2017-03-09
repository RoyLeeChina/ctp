package org.hotwheel.stock.exchange.context.trade;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.hotwheel.stock.exchange.context.jdb.v1.BatchMapping;
import org.hotwheel.stock.exchange.context.jdb.v1.TradeDataLine;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.mymmsc.api.assembly.Api;

import java.util.Map;

/**
 * 用户信息
 * <p>
 * Created by wangfeng on 2016/11/18.
 * @since 2.1.0
 */
public class UserInfo extends TradeDataLine {
    public String id = ""; // "1800074",
    public String user_id = ""; //"583753510571483264",
    public String user_type = ""; //1,
    public String user_name = ""; //"王涛",
    public String phone_num = ""; //"18010001114",
    public String avatar_url = ""; //"http://jdbserver.b0.upaiyun.com/images/58371465698350434",
    public String thumbnail_url = ""; //"http://jdbserver.b0.upaiyun.com/images/58371465698350434!percent25",
    public String level = ""; //"0",
    public String register_time = ""; //1459860658,
    public String image_sign = ""; //"58371465698350434",
    public String user_status = ""; //1,
    public String yft_id = ""; // 支付id
    public UserExtInfo ext = new UserExtInfo();

    @Override
    public String toLine() {
        String sRet = null;
        final int  index = 8;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);

        String custStatus = "2";
        String custIstMarginStatus = "2";
        /*
        String entryUuid = uuids.get(j);
        EntryInfo entryInfo = entryInfoMap.get(entryUuid);

        ProductPoolIndex ppi = productPoolIndexMap.get(entryUuid);
        if (ppi != null) {
            String productIds = ppi.getProductIds();
            List<String> productIdsList = Lists.newArrayList(Splitter.on(",").trimResults().split(productIds));
            List<Product> curEntryProductList = productService.shardSelectByIdList(productIdsList, -1);
            for (Product product : curEntryProductList) {
                String pCode = product.getProductCode();
                int leve = getProductLevel(pCode);
                if (StringUtils.equals(custStatus, "1") && StringUtils.equals(custIstMarginStatus, "1")) {
                    break;
                }
                if (!StringUtils.equals(custStatus, "1") && leve == 0) {
                    custStatus = "1";
                }
                if (!StringUtils.equals(custIstMarginStatus, "1") && leve > 0) {
                    custIstMarginStatus = "1";
                }
            }
        }
        Map<String, String> args = new HashMap<>(valTemplate);
        Entry entry = entryMap.get(entryUuid);
        if (entry != null) {

        }
        */
        args.put("name", user_name);
        args.put("phone_nbr", phone_num);
        args.put("id", user_id);
        args.put("idtype", "I");
        args.put("custStatus", custStatus);
        args.put("custIstMarginStatus", custIstMarginStatus);

        String birthday = Api.toString(ext.birthday, DDF);
        args.put("birthday", birthday);
        Integer sex = null;
        try {
            sex = Integer.parseInt(ext.sex);
        } catch (Exception e) {
            //
        }

        args.put("sex", sex == null ? "Z" : (sex == 1 ? "M" : "F"));
        args.put("ed_level", level);
        args.put("graduation", ext.school);
        args.put("college", ext.major);
        args.put("emergency_contact", ext.emergency_name);
        args.put("emergency_mobile", ext.emergency_phone_num);
        //data.add(new StrSubstitutor(args).replace(valueTemp));

        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);
        return sRet;
    }

    public String toLine2() {
        String sRet = null;
        final int  index = 4;
        final String valueTemp = BatchMapping.valueTemp[index];
        final String[] infos = BatchMapping.infos[index];
        Map<String, String> args = BaseContext.builderValuesEmptyMap(infos);
        args.put("jdb_id", user_id);
        args.put("yft_id", yft_id);

        BaseContext.fillDefault(args, infos);
        sRet = new StrSubstitutor(args).replace(valueTemp);

        return sRet;
    }

    @Override
    protected String getCSVFilename() {
        csvFilename = BatchMapping.files[2];
        return csvFilename;
    }
}
