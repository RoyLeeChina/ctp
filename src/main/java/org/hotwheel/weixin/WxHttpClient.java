package org.hotwheel.weixin;

import org.hotwheel.assembly.Api;
import org.hotwheel.io.HttpClient;
import org.hotwheel.io.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信客户端连接
 */
public class WxHttpClient {
    private static Logger logger = LoggerFactory.getLogger(WxHttpClient.class);
    private final static String kUserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

    CookieManager ca = new CookieManager();
    String sessionID = "";
    String contentType = "";
    private static WxHttpClient hcClient;

    static WxHttpClient getInstance() {
        if (hcClient == null) {
            synchronized (WxHttpClient.class) {
                if (hcClient == null) {
                    hcClient = new WxHttpClient();
                }
            }
        }
        return hcClient;
    }

    public String get(String url, String charset, String referer, boolean isRedirects) {
        String sRet = null;
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;
        try {
            String key = "";
            String cookieVal = "";

            URL httpURL = new URL(url);
            httpConn = (HttpURLConnection) httpURL.openConnection();
            httpConn.setInstanceFollowRedirects(isRedirects);//设置自动跳转
            if (referer != null) {
                httpConn.setRequestProperty("Referer", referer);
            }
            if (contentType != null) {
                httpConn.setRequestProperty("content-type", contentType);
            }
            httpConn.setConnectTimeout(10 * 1000);
            httpConn.setReadTimeout(10 * 1000);
            httpConn.setRequestProperty("User-agent", kUserAgent);
            if (!sessionID.equals("")) {
                httpConn.setRequestProperty("Cookie", sessionID);
            }
            for (int i = 1; (key = httpConn.getHeaderFieldKey(i)) != null; i++) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    cookieVal = httpConn.getHeaderField(i);
                    cookieVal = cookieVal.substring(
                            0,
                            cookieVal.indexOf(";") > -1 ? cookieVal
                                    .indexOf(";") : cookieVal.length() - 1);
                    sessionID = sessionID + cookieVal + ";";
                }
            }
            inputStream = httpConn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
                sb.append("\n");
            }
            br.close();
            sRet = sb.toString();
        } catch (SocketTimeoutException e) {
            // 忽略超时
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            Api.closeQuietly(inputStream);
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        return sRet;
    }

    public String get(String url) {
        String sRet = "";
        try {
            sRet = get(url, "utf-8", null, true);
        } catch (Exception e) {
            //
        }
        return sRet;
    }

    public String post(String url, String data, String charset, String referer, boolean isRedirects) {
        HttpURLConnection httpConn = null;
        InputStream inputStream = null;
        try {
            URL httpURL = new URL(url);
            String key = null;
            String cookieVal = null;
            httpConn = (HttpURLConnection) httpURL.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("User-Agent", kUserAgent);
            httpConn.setInstanceFollowRedirects(isRedirects);//设置自动跳转
            if (referer != null) {
                httpConn.setRequestProperty("Referer", referer);
            }
            if (contentType != null) {
                httpConn.setRequestProperty("content-type", contentType);
            }
            if (!sessionID.equals("") && sessionID != null) {
                httpConn.setRequestProperty("Cookie", sessionID);
            }
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(httpConn.getOutputStream(), charset));
            bw.write(data);
            bw.close();
            inputStream = httpConn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
                sb.append("\n");
            }
            br.close();
            for (int i = 1; (key = httpConn.getHeaderFieldKey(i)) != null; i++) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    cookieVal = httpConn.getHeaderField(i);
                    cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                    sessionID = sessionID + cookieVal + ";";
                }
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            Api.closeQuietly(inputStream);
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }
        return null;
    }

    public String post(String url, String data) {
        String sRet = "";
        try {
            sRet = post(url, data, "utf-8", null, true);
        } catch (Exception e) {
            //
        }

        return sRet;
    }

    public String post2(String url, String data) {
        String sRet = "";
        HttpClient httpClient = new HttpClient(url, "utf-8");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        HttpResult hRet = httpClient.post(headers, data);
        if (hRet.getStatus() == 200) {
            sRet = hRet.getBody();
        }
        return sRet;
    }
}