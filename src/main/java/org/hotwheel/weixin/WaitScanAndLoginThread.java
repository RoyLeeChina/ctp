package org.hotwheel.weixin;

/**
 * 等待线程
 */
public class WaitScanAndLoginThread extends Thread {
    private int tip = 1;//0表示已经扫描
    private String uuid;
    private boolean running = true;
    private HttpClient hc = HttpClient.getInstance();
    private StringSubClass ss = new StringSubClass();
    private OnScanListener mScanListener;
    private WeChatApp wechat;

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

    WaitScanAndLoginThread(String uuid, WeChatApp wechat) {
        this.uuid = uuid;
        this.wechat = wechat;

    }

    @Override
    public void run() {
        System.out.println("开启等待线程");
        while (running) {
            String result = hc.get(
                    "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login?tip="
                            + tip + "&uuid=" + uuid + "&_="
                            + System.currentTimeMillis(), "utf-8", null, false);
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
