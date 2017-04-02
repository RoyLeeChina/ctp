package org.hotwheel.weixin;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private WxHttpClient httpClient = WxHttpClient.getInstance();

    static {
        System.setProperty("https.protocols", "TLSv1");
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    /**
     * 匹配字符串
     * @param str
     * @param exp
     * @param defaultValue
     * @return
     */
    public static String match(String str, String exp, final String defaultValue){
        Pattern pattern = Pattern.compile(exp);
        Matcher m = pattern.matcher(str);
        if(m.find()){
            return m.group(1);
        }
        return defaultValue;
    }

    /**
     * 获取UUID
     */
    public String getUuid() {
        String sRet = null;
        String url = "https://login.weixin.qq.com/jslogin";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("appid", kAppId);
        params.put("lang", "zh_CN");
        params.put("fun", "new");
        params.put("_", System.currentTimeMillis());

        String result = HttpUtils.request(url, null, params);
        if (!Api.isEmpty(result)) {
            //String result = httpClient.post("https://login.weixin.qq.com/jslogin",
            //        "appid=wx782c26e4c19acffb&fun=new&lang=zh_CN&_=" + System.currentTimeMillis());
            //uuid = ss.subStringOne(result, ".uuid = \"", "\";");//得到uuid
            String code = match(result, "window.QRLogin.code = (\\d+);", "");
            if (null != code) {
                if (code.equals("200")) {
                    sRet = match(result, "window.QRLogin.uuid = \"(.*)\";", "");
                }
            }
        }
        if (!Api.isEmpty(sRet)) {
            uuid = new String(sRet);
        }
        return uuid;
    }

    /**
     * 下载登录二维码
     * @return
     */
    public byte[] downloadQrCode() {
        byte[] baImage = null;
        String qrCodeUrl = "https://login.weixin.qq.com/qrcode/" + uuid + "?t=webwx&_=";
        URL url;
        DataInputStream bfin = null;
        try {
            url = new URL(qrCodeUrl);
            bfin = new DataInputStream(url.openStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = bfin.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bfin.close();
            baImage = bos.toByteArray();
            bos.close();
        } catch (MalformedURLException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }

        if (baImage == null) {
            logger.info("二维码下载失败");
        } else {
            logger.info("二维码已生成");
        }
        return baImage;
    }
}
