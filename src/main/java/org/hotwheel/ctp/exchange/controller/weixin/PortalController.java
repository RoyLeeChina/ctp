package org.hotwheel.ctp.exchange.controller.weixin;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.service.UserService;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.weixin.WeChat;
import org.hotwheel.weixin.WeChatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.TreeMap;

/**
 * 微信
 * Created by wangfeng on 2017/3/25.
 * @version 1.0.2
 */
@Controller
@RequestMapping("/third")
public class PortalController implements WeChatContext {
    private static Logger logger = LoggerFactory.getLogger(PortalController.class);

    private final static String kToken = "stockExchange";
    private final static String kEncodingAESKey = "Pl2la9FY1Ka91Py1Kf5lMGFt0BGSuff87AUMO0vZAyt";
    private WeChat weChat = null;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        logger.info("初始化...");
    }

    @PreDestroy
    public void close() {
        logger.info("关闭...");
    }

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
        response.setCharacterEncoding("UTF-8");
        try {
            request.setCharacterEncoding("UTF-8");
            weChat = new WeChat();
            weChat.getUuid();
            byte[] data = weChat.downloadQrCode();
            response.reset();
            //设置响应的报头信息(中文问题解决办法)
            //response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(downLoadName, "UTF-8"));
            //response.addHeader("Content-Length", "" + data.length);
            response.setContentType("image/jpeg");
            ServletOutputStream output = response.getOutputStream();
            output.write(data);
            output.flush();
            output.close();
            weChat.start(this);
        } catch (Exception e) {
            logger.error("下载图片出错", e);
        } finally {
            //
        }
    }

    private final static String kPrefix = "【CTP微信助手】";
    private final static String kToMe = "@王布衣";

    @Override
    public void handleMessage(String groupId, String fromUser, String toUser, String text) {
        String nickName = weChat.mapUserToNick.get(fromUser);
        String phone = null;
        if (!Api.isEmpty(nickName)) {
            phone = userService.getPhone(nickName);
        }
        if (!Api.isEmpty(phone)) {
            phone = phone.trim();
        }
        logger.info("{}->{}: {}", nickName, toUser, text);
        if (text.startsWith(kToMe)) {
            text = text.substring(kToMe.length()).trim();
            String msg = text.trim().replaceAll("( )+"," ");
            String[] args = msg.split(" ");
            if (args.length >= 1) {
                String command = args[0].trim();
                String params = args.length>=2 ? args[1].trim() : "";
                String message = null;
                if (command.equalsIgnoreCase("help")) {
                    // 帮助信息
                    message = "帮助信息:";
                    message += "\n1)注册股票预警功能: @王布衣 zc 手机号码";
                    message += "\n2)订阅个股预警信息: @王布衣 dy 股票代码";
                } else if (command.equalsIgnoreCase("查询") || command.equalsIgnoreCase("cx")) {
                    if (params.equalsIgnoreCase("id")) {
                        // 查询用户ID
                        ActionStatus resp = userService.query(nickName);
                        if (resp.getStatus() == 0) {
                            message = nickName + "的id是" + resp.getMessage();
                        } else {
                            message = nickName + "查询ID失败: " + resp.getMessage();
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
                        message = nickName + " 订阅信息: " + message;
                    } else {
                        message  = "1. 查询注册id: cx id\n";
                        message += "2. 查询订阅信息: cx dy";
                        message = ": " + message;
                    }
                }  else if (command.equalsIgnoreCase("注册") || command.equalsIgnoreCase("zc")) {
                    // 查询用户ID
                    ActionStatus resp = userService.query(nickName);
                    if (resp.getStatus() != 0) {
                        phone = params;
                        resp = userService.register(phone, nickName, nickName, "");
                        message = nickName + "注册: " + resp.getMessage();
                    } else {
                        message = nickName + "已注册过";
                    }
                } else if (Api.isEmpty(phone)) {
                    //weChat.sendMessage(nickName, kPrefix + nickName + " 未注册");
                } else if (command.equalsIgnoreCase("订阅") || command.equalsIgnoreCase("dy")){
                    ActionStatus resp = userService.subscribe(phone, params);
                    message = nickName + "，订阅" + params;
                    if (resp.getStatus() == 0) {
                        message += "成功";
                    } else {
                        message += "失败: " + resp.getMessage();
                    }
                } else if (command.equalsIgnoreCase("退订") || command.equalsIgnoreCase("td")){
                    ActionStatus resp = userService.unsubscribe(phone, params);
                    message = nickName + "，退订" + params;
                    if (resp.getStatus() == 0) {
                        message += "成功";
                    } else {
                        message += "失败: " + resp.getMessage();
                    }
                }
                if (!Api.isEmpty(message)) {
                    message = kPrefix + message;
                    if (!Api.isEmpty(groupId)) {
                        weChat.sendGroupMessage(groupId, fromUser, message);
                    } else {
                        weChat.sendMessageByUserId(fromUser, message);
                    }
                }
            }
        }
    }
}
