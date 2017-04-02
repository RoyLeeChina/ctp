package org.hotwheel.weixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 微信网页版实现
 *
 * Created by wangfeng on 2017/4/2.
 * @version 2.0.1
 */
public class WeChat {
    private static Logger logger = LoggerFactory.getLogger(WeChat.class);

    private final static String kAppId = "wx782c26e4c19acffb";
    private String baseUrl = null;
    private static String uuid = null;
    private String wxSkey;
    private String wxSid;
    private String wxUin;
    private String syncKey;
    private String pass_ticket;
}
