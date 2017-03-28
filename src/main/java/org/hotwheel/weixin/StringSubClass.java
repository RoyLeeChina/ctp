package org.hotwheel.weixin;

import org.hotwheel.assembly.Api;

public class StringSubClass {

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