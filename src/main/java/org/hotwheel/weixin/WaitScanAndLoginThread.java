package org.hotwheel.weixin;

import org.hotwheel.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 等待线程
 * @version 2.0.0
 */
public class WaitScanAndLoginThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(WaitScanAndLoginThread.class);
    private int tip = 1;//0表示已经扫描
    private String uuid;
    private boolean running = true;
    private WxHttpClient hc = WxHttpClient.getInstance();
    private StringSubClass ss = new StringSubClass();
    private OnScanListener mScanListener;
    private WeChat wechat;

    /**
     * 登陆的监听器
     */
    public interface OnScanListener {

        void onScan();//扫描成功

        void onSure();//确定登陆
    }

    public void setmScanListener(OnScanListener mScanListener) {
        this.mScanListener = mScanListener;
    }

    WaitScanAndLoginThread(String uuid, WeChat wechat) {
        this.uuid = uuid;
        this.wechat = wechat;

    }

    @Override
    public void run() {
        logger.info("开启等待线程");
        while (running) {
            String result = hc.get(
                    "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login?tip="
                            + tip + "&uuid=" + uuid + "&_="
                            + System.currentTimeMillis(), "utf-8", null, false);
            if (Api.isEmpty(result)) {
                continue;
            }
            String code = ss.subStringOne(result, ".code=", ";");
            if (mScanListener != null) {
                if (code.equals("201")) {
                    tip = 0;
                    mScanListener.onScan();
                } else if (code.equals("200")) {

                    String redirect_uri = ss.subStringOne(result, "window.redirect_uri=\"", "\";");
                    running = false;
                    String loginResult = hc.get(redirect_uri + "&fun=new");
                    wechat.skey = ss.subStringOne(loginResult, "<skey>", "</skey>");
                    wechat.wxsid = ss.subStringOne(loginResult, "<wxsid>", "</wxsid>");
                    wechat.pass_ticket = ss.subStringOne(loginResult, "<pass_ticket>", "</pass_ticket>");
                    wechat.wxuin = ss.subStringOne(loginResult, "<wxuin>", "</wxuin>");
                    wechat.baseUrl = redirect_uri.substring(0, redirect_uri.lastIndexOf("/"));
                    mScanListener.onSure();
                    wechat.init();

                }
            }

        }//while

    }

}
