package org.hotwheel.ctp.exchange.controller.weixin;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.exchange.task.CTPContext;
import org.hotwheel.io.DataStream;
import org.hotwheel.weixin.DownLoadQrCodeThread;
import org.hotwheel.weixin.HeartBeatThread;
import org.hotwheel.weixin.WaitScanAndLoginThread;
import org.hotwheel.weixin.WeChatApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * 微信
 * Created by wangfeng on 2017/3/25.
 * @version 1.0.2
 */
@Controller
@RequestMapping("/third")
public class PortalController {
    private static Logger logger = LoggerFactory.getLogger(PortalController.class);

    private final static String kToken = "stockExchange";
    private final static String kEncodingAESKey = "Pl2la9FY1Ka91Py1Kf5lMGFt0BGSuff87AUMO0vZAyt";

    @RequestMapping("/wx")
    @ResponseBody
    public String wxPortal1(String signature, String timestamp, String nonce, String echostr) {
        String sRet = "OK";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("token", kToken);
        params.put("timestamp", timestamp);
        params.put("nonce", nonce);

        StringBuilder preSign = new StringBuilder();
        for (Map.Entry<String, Object> entry: params.entrySet()) {
            preSign.append(Api.toString(entry.getValue()));
            //preSign.append('|');
        }
        sRet = Api.md5(preSign.toString());
        sRet = sRet.toLowerCase();
        return sRet;
    }

    @RequestMapping("/start.wx")
    //@ResponseBody
    public void scheduleDownload(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        //String html = "";
        response.setCharacterEncoding("UTF-8");
        String downLoadName = "wx.jpg";
        InputStream input = null;
        try {
            request.setCharacterEncoding("UTF-8");
            //获取文件的路径
            String url = "login-qrcode.jpg";
            File file = new File(url);
            try {
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                //
            }
            DownLoadQrCodeThread.setPathname(url);
            {
                AsyncSubmitThread thrTask = new AsyncSubmitThread();
                String threadName = thrTask.getClass().getSimpleName();
                Thread thread = new Thread(thrTask, threadName);
                // 此次将User线程变为Daemon线程
                thread.setDaemon(true);
                thread.start();
            }
            while (!file.exists()) {
                Api.sleep( 5 * 1000);
            }
            input = new FileInputStream(file);
            byte[] data = DataStream.recv(input);
            response.reset();
            //设置响应的报头信息(中文问题解决办法)
            //response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(downLoadName, "UTF-8"));
            //response.addHeader("Content-Length", "" + data.length);
            response.setContentType("image/jpeg");
            ServletOutputStream output = response.getOutputStream();
            output.write(data);
            output.flush();
            output.close();
        } catch (Exception e) {
            logger.error("下载图片出错");
        } finally {
            Api.closeQuietly(input);
        }
    }

    private class AsyncSubmitThread implements Runnable {

        @Override
        public void run() {
            final WeChatApp weChat = new WeChatApp();
            weChat.setmScanListener(new WaitScanAndLoginThread.OnScanListener() {

                @Override
                public void onSure() {
                    logger.info("登陆成功");
                    CTPContext.setWeChat(weChat);

                }

                @Override
                public void onScan() {
                    logger.info("已经扫描成功，等待确认登陆");

                }
            });
            weChat.setmNewMsgListener(new HeartBeatThread.OnNewMsgListener() {

                @Override
                public void onNewMsg(String fromUser, String toUser, String text) {
                    logger.info("{}->{}: {}", fromUser, toUser, text);
                    if (text.startsWith("@王布衣")) {
                        String msg = text.replaceAll("( )+"," ");
                        String[] args = msg.split(" ");
                        if (args.length >= 3) {
                            String command = args[1];
                            String params = args[2];
                            String message = null;
                            if (command.equalsIgnoreCase("help")) {
                                // 帮助信息
                                message = "CTP策略订阅帮助信息:\r\n1)订阅个股预警信息: at 王布衣 订阅 股票代码";
                            } else if (command.equalsIgnoreCase("订阅") || command.equalsIgnoreCase("dy")){
                                //
                            }
                        }

                    }
                }

                @Override
                public void startBeat() {
                    logger.info("开始心跳");

                }
            });
            weChat.startListner();
        }
    }
}
