package org.hotwheel.ctp.exchange.controller.weixin;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.exchange.task.CTPContext;
import org.hotwheel.ctp.service.UserService;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.io.DataStream;
import org.hotwheel.weixin.DownLoadQrCodeThread;
import org.hotwheel.weixin.HeartBeatThread;
import org.hotwheel.weixin.OldWeChat;
import org.hotwheel.weixin.WaitScanAndLoginThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserService userService;

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
    @ResponseBody
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
            logger.info(file.getAbsolutePath());
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
            logger.info("starting...");
            final OldWeChat weChat = new OldWeChat();
            weChat.setmScanListener(new WaitScanAndLoginThread.OnScanListener() {

                @Override
                public void onSure() {
                    logger.info("登陆成功");
                    CTPContext.setWeChat(weChat);
                }

                @Override
                public void onScan() {
                    logger.info("已经扫描成功，等待确认登陆");
                    //CTPContext.setWeChat(null);
                }
            });

            weChat.setmNewMsgListener(new HeartBeatThread.OnNewMsgListener() {
                private final static String kPrefix = "【CTP微信助手】";

                @Override
                public void onNewMsg(final String groupId, String fromUser, String toUser, String text) {
                    String nickName = weChat.mapUserToNick.get(fromUser);
                    String phone = null;
                    if (!Api.isEmpty(nickName)) {
                        phone = userService.getPhone(nickName);
                    }
                    logger.info("{}->{}: {}", nickName, toUser, text);
                    if (text.startsWith("@王布衣")) {
                        String msg = text.trim().replaceAll("( )+"," ");
                        String[] args = msg.split(" ");
                        if (args.length >= 2) {

                            String command = args[1].trim();
                            String params = args.length>=3 ? args[2].trim() : "";
                            String message = null;
                            if (command.equalsIgnoreCase("help")) {
                                // 帮助信息
                                message = "CTP策略订阅帮助信息:\r\n1)注册股票预警功能: @王布衣 zc 手机号码\n2)订阅个股预警信息: @王布衣 dy 股票代码\n";
                                if (groupId == null) {
                                    weChat.sendMessage(nickName, message);
                                } else {
                                    String groupName = weChat.mapUserToNick.get(groupId);
                                    nickName = weChat.mapUserToNick.get(toUser);
                                    weChat.sendGroupMessage(groupName, nickName, message);
                                }
                            } else if (command.equalsIgnoreCase("查询") || command.equalsIgnoreCase("cx")) {
                                if (params.equalsIgnoreCase("id")) {
                                    // 查询用户ID
                                    ActionStatus resp = userService.query(nickName);
                                    if (resp.getStatus() == 0) {
                                        weChat.sendMessage(nickName, kPrefix + nickName + "的id是" + resp.getMessage());
                                    } else {
                                        weChat.sendMessage(nickName, kPrefix + nickName + "查询ID失败: " + resp.getMessage());
                                    }
                                } else if (params.equalsIgnoreCase("dy")) {
                                    if (Api.isEmpty(phone)) {
                                        message = nickName + "未注册";
                                    } else {
                                        message = userService.querySubscribe(phone);
                                        if (Api.isEmpty(message)) {
                                            message = "没有订阅个股";
                                        }
                                    }
                                    weChat.sendMessage(nickName, kPrefix + nickName + " 订阅信息: " + message);
                                } else {
                                    message  = "1. 查询注册id: cx id\n";
                                    message += "2. 查询订阅信息: cx dy";
                                    weChat.sendMessage(nickName, kPrefix + ": " + message);
                                }
                            }  else if (command.equalsIgnoreCase("注册") || command.equalsIgnoreCase("zc")) {
                                // 查询用户ID
                                ActionStatus resp = userService.query(nickName);
                                if (resp.getStatus() != 0) {
                                    phone = params;
                                    resp = userService.register(phone, nickName, nickName, "");
                                    if (resp.getStatus() == 0) {
                                        weChat.sendMessage(nickName, kPrefix + nickName + "注册:" + resp.getMessage());
                                    } else {
                                        weChat.sendMessage(nickName, kPrefix + nickName + "注册: " + resp.getMessage());
                                    }
                                } else {
                                    weChat.sendMessage(nickName, kPrefix + nickName + "已注册过");
                                }
                            } else if (Api.isEmpty(phone)) {
                                //weChat.sendMessage(nickName, kPrefix + nickName + " 未注册");
                            } else if (command.equalsIgnoreCase("订阅") || command.equalsIgnoreCase("dy")){
                                ActionStatus resp = userService.subscribe(phone, params);
                                if (resp.getStatus() == 0) {
                                    weChat.sendMessage(nickName, kPrefix + nickName + "订阅" + params+ "成功");
                                } else {
                                    weChat.sendMessage(nickName, kPrefix + nickName + "订阅" + params+ "失败: " + resp.getMessage());
                                }
                            } else if (command.equalsIgnoreCase("退订") || command.equalsIgnoreCase("td")){
                                ActionStatus resp = userService.unsubscribe(phone, params);
                                if (resp.getStatus() == 0) {
                                    weChat.sendMessage(nickName, kPrefix + nickName + "退订" + params+ "成功");
                                } else {
                                    weChat.sendMessage(nickName, kPrefix + nickName + "退订" + params+ "失败: " + resp.getMessage());
                                }
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
