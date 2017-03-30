package org.hotwheel.weixin;

import org.hotwheel.assembly.Api;

/**
 * 字符串工具
 * @version 2.0.0
 */
public class StringSubClass {

    /**
     * 截取字符串
     * @param from
     * @param start
     * @param end
     * @return
     */
    public String subStringOne(String from, String start, String end) {
        String sRet = "";
        if (!Api.isEmpty(from) && !Api.isEmpty(start)) {
            int s = from.indexOf(start) + start.length();
            if (s >= 0) {
                int e = from.indexOf(end, s);
                if (e >= s) {
                    sRet = from.substring(s, e);
                }
            }
        }
        return sRet;
    }

    /**
     * 截取字符串数组
     * @param from
     * @param start
     * @param end
     * @return
     */
    public String[] subStringAll(String from, String start, String end) {
        int fromIndex = 0;
        int count = 0;
        int startStringLength = 0;
        int endStringLength = end.length();
        while ((fromIndex = from.indexOf(start, fromIndex + startStringLength)) != -1) {
            if (count == 0) {
                startStringLength = start.length();
            }
            count++;
        }//获取到有几组匹配数据
        String returnString[] = new String[count];
        for (int j = 0; j < count; j++) {
            returnString[j] = subStringOne(from, start, end);
            from = from.substring(from.indexOf(end) + endStringLength);
        }
        return returnString;
    }

}