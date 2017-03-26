package org.hotwheel.weixin;

import java.util.Date;

/**
 * Created by wangfeng on 2017/3/26.
 */
public class TestAbc {

    public static void main(String[] args) {
        Date now = new Date();
        long randomId = System.nanoTime();
        String str = "" + randomId;
        System.out.println(str.substring(2, 17));
        System.out.println("哈哈");
    }
}
