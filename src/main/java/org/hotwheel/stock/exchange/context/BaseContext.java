package org.hotwheel.stock.exchange.context;

import org.hotwheel.stock.exchange.model.Money;
import org.mymmsc.api.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.io.File.separator;

/**
 * 通用父类, 集合日志等其它功能之用
 *
 * Created by wangfeng on 16/7/14.
 */
public abstract class BaseContext {
    /**< 日志记录器 */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final static String lineSeparator       = System.lineSeparator();
    private static final String NUMBER_PATTERN = "^[0-9]+(.[0-9]{0,1})?$";// 判断小数点后一位的数字的正则表达式
    private static final String CNUMBER_PATTERN = "^[0-9]*$";// 判断数字的正则表达式

    /*
    protected final static NumberFormat currency;

    static {
        currency =  NumberFormat.getInstance();
        currency.setMaximumFractionDigits(2);
        currency.setMinimumFractionDigits(2);
    }*/

    public static long getTime(Date date) {
        long lRet = 0;
        if (!Api.isEmpty(date)) {
            lRet = date.getTime();
        }
        return lRet;
    }

    /**
     * 执行正则表达式
     *
     * @param pattern
     *            表达式
     * @param str
     *            待验证字符串
     * @return 返回 <b>true </b>,否则为 <b>false </b>
     */
    private static boolean match(String pattern, String str) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 验证是不是数字(没有小数点)
     *
     * @param number
     * @return
     */
    public static boolean isInteger(String number) {
        return match(CNUMBER_PATTERN, number);
    }

    /**
     * Map value初始化
     *
     * @param keys
     * @return
     */
    public static Map<String, String> builderValuesEmptyMap(String[] keys) {
        Map<String, String> result = new HashMap<>();
        for (String key: keys) {
            result.put(key, "");
        }
        return result;
    }

    public static void fillDefault(Map<String, String> map, final String[] keys) {
        /*
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey().trim();
            String value = Api.toString(entry.getValue());
            if (Api.isEmpty(value)) {
                map.put(key, "");
            }
        }*/
        for (String key: keys) {
            String value = map.get(key);
            if (Api.isEmpty(value)) {
                map.put(key, "");
            }
        }
    }

    public static Money valueOf(BigDecimal value) {
        Money money = Money.of(0);
        if(value != null) {
            money = Money.of(value);
        }
        return money;
    }

    public static Money valueOf(int value) {
        return valueOf(BigDecimal.valueOf(value));
    }

    public static Money valueOf(String value) {
        return Money.of(value, "0.00");
    }

    public static String getProductTopCode(String productCode) {
        int index = productCode.indexOf('-');
        return index>0?productCode.substring(0,index):productCode;
    }

    public static String toString(final BigDecimal value) {
        return valueOf(value).toMoneyString(2);
    }


    /**
     * 格式化货币
     * @param value
     * @return
     */
    public static String format(final BigDecimal value) {
        //double d = value.doubleValue();
        return String.format("%.2f", value.doubleValue());
        //return Money.of(value).toMoneyString(2);
        //return value.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        //return currency.format(value.setScale(2, BigDecimal.ROUND_HALF_UP));
        //return currency.format(value);
    }

    public static boolean isNewFile(File file, String path, String fileName) {
        boolean bRet = false;
        Date today = new Date();
        String curDateStr = Api.toString(today, Category.DateFormat2);
        String basePath = path.concat(separator).concat(curDateStr);
        String filePath = basePath.concat(separator).concat(fileName);
        bRet = !file.getAbsolutePath().equals(filePath);
        return bRet;

    }

    /**
     * 写文件
     * @param toFile
     * @param value
     * @return
     */
    public static boolean writeToFile(File toFile, String value) {
        try {
            value = Api.isEmpty(value)?"":value;
            value = value.replaceAll("\r\n", "\\@@@1@").replaceAll("\n\r", "\\@@@2@")
                    .replaceAll( "\r", "\\@@@3@").replaceAll("\n", "\\@@@4@");
            value = value.replaceAll("\"", " ");
            RandomAccessFile raf = new RandomAccessFile(toFile, "rw");
            if (raf.length() > 0) {
                value = lineSeparator.concat(value);
            }
            raf.seek(raf.length());
            raf.write(value.getBytes("UTF-8"));
            raf.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static File createCVSFile(String path, String fileName) {
        Date today = new Date();
        String curDateStr = Api.toString(today, Category.DateFormat2);
        String basePath = path.concat(separator).concat(curDateStr);
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath = basePath.concat(separator).concat(fileName);
        File newFile = new File(filePath);
        try {
            newFile.deleteOnExit();
            newFile.createNewFile();
        } catch (IOException e) {
            //logger.error("文件创建失败：", e);
            return null;
        }
        return newFile;
    }

    public static String toShowId(Date date,String uuid) {
        String sRet = "";
        String cDate = Api.toString(date, Category.DateFormat2);
        int len = uuid.length();
        if (len == 22) {
            sRet = cDate.concat(uuid.substring(len-8,len-4));
        } else {
            sRet = cDate.concat(uuid.substring(len - 4, len));
        }
        return sRet;
    }
/*
    public static boolean writeToFile(Future<PartitionContext> future) {
        boolean bRet = false;
        PartitionContext ret = null;
        try {
            ret = future.get();
            bRet = true;
        } catch (Exception e) {
            bRet = false;
        }

        if(bRet && ret != null && ret.rows != null && ret.file != null) {
            for (String line : ret.rows) {
                //logger.info(line);
                boolean bWrite = writeToFile(ret.file, line);
                if(!bWrite) {
                    bRet = false;
                }
            }
            //logger.info("{}\t==> {} rows.", ret.taskName, ret.lines.size());
            bRet = true;
        } else {
            bRet = false;
        }

        return bRet;
    }*/
}
