package org.hotwheel.ctp.exchange.controller.weixin;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockCode;
import org.hotwheel.ctp.data.MoneyFlowUtils;
import org.hotwheel.ctp.exchange.task.CTPContext;
import org.hotwheel.ctp.model.StockCode;
import org.hotwheel.ctp.model.StockMoneyFlow;
import org.hotwheel.ctp.model.StockMonitor;
import org.hotwheel.ctp.service.UserService;
import org.hotwheel.ctp.util.StockApi;
import org.hotwheel.ctp.util.Validate;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.util.StringUtils;
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
import java.util.Date;
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

    @Autowired
    private IStockCode stockCode;

    @PostConstruct
    public void init() {
        logger.info("初始化...");
    }

    @PreDestroy
    public void close() {
        CTPContext.setServerCloseing(true);
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

    @Override
    public void sendHelp(String toUser) {
        String message = "帮助信息:";
        message += "\n1)注册股票预警功能: zc 手机号码";
        message += "\n2)查询注册id: cx id";
        message += "\n3)查询订阅信息: cx dy";
        message += "\n4)查询个股策略: cx 股票代码";
        message += "\n5)订阅个股信息: dy 股票代码";
        message += "\n6)退订个股信息: td 股票代码";

        weChat.sendMessageByUserId(toUser, message);
    }

    @Override
    public void handleMessage(String groupId, String fromUser, String toUser, String text) {
        String kToMe = "@"  + weChat.kNickName;
        boolean isFriend = true;
        String groupName = weChat.getNickName(groupId);
        String nickName = weChat.getNickName(fromUser);
        if (Api.isEmpty(nickName)) {
            // 不是好友
            nickName = weChat.getNickName(groupId, fromUser);
            logger.info("群昵称: {}", nickName);
            isFriend = false;
        } else {
            logger.info("好友昵称: {}", nickName);
        }
        String phone = null;
        if (!Api.isEmpty(nickName)) {
            phone = userService.getPhone(nickName);
        }
        if (!Api.isEmpty(phone)) {
            phone = phone.trim();
        }
        logger.info("{}->{}: {}", nickName, toUser, text);
        if (text.toUpperCase().startsWith(kToMe)) {
            text = text.substring(kToMe.length()).trim();
            text = StringUtils.trimWhitespace(text);
            text = text.replaceAll("<br/>"," ");
            String msg = text.replaceAll("( )+"," ");
            String[] args = msg.split(" ");
            if (args.length >= 1) {
                String command = StringUtils.trimWhitespace(args[0]);
                String params = args.length>=2 ? args[1] : "";
                params = StringUtils.trimWhitespace(params);
                String message = null;
                if (Api.isEmpty(groupId)) {
                    kToMe = "";
                }
                if (command.equalsIgnoreCase("help")) {
                    // 帮助信息
                    message = "帮助信息:";
                    message += "\n1)注册股票预警功能: " + kToMe + " zc 手机号码";
                    message += "\n2)查询注册id: " + kToMe + "cx id";
                    message += "\n3)查询订阅信息: " + kToMe + "cx dy";
                    message += "\n4)查询个股策略: " + kToMe + "cx 股票代码";
                    message += "\n5)订阅个股信息: " + kToMe + "dy 股票代码";
                    message += "\n6)退订个股信息: " + kToMe + "td 股票代码";
                } else if (command.equalsIgnoreCase("查询") || command.equalsIgnoreCase("cx")) {
                    boolean bFound = false;
                    String fullCode = StockApi.fixCode(params);
                    if (params.equalsIgnoreCase("id")) {
                        // 查询用户ID
                        ActionStatus resp = userService.query(nickName);
                        if (resp.getStatus() == 0) {
                            message = nickName + "的id是" + resp.getMessage();
                        } else {
                            message = nickName + "查询ID失败: " + resp.getMessage();
                        }
                        bFound = true;
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
                        bFound = true;
                    }
                    if (!bFound && fullCode == null) {
                        // 如果都没找到, 但是还是有参数? 按照中文证券名称来查询
                        fullCode = userService.getFullCode(params);
                    }
                    if (!bFound && fullCode != null) {
                        StockMonitor info = userService.queryPolicy(fullCode);
                        if (info == null) {
                            message = nickName + "，暂无该股策略";
                        } else {
                            StockCode sc = stockCode.select(fullCode, fullCode);
                            String stockName = null;
                            if (sc != null) {
                                stockName = sc.getName();
                            }
                            StockMoneyFlow moneyFlow = MoneyFlowUtils.getOne(fullCode);
                            String content = String.format("%s(%s): 第2支撑位%s~第1支撑位%s/第1压力位%s~第2压力位%s, 阻力位%s, 止损位%s",
                                    stockName, sc.getCode(), info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                                    info.getResistance(), info.getStop());

                            String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                            String title = prefix + "-CTP策略提示(" + Api.toString(info.getCreateTime(), StockOptions.TimeFormat) + ")";
                            if (moneyFlow == null) {
                                content += "。";
                            } else {
                                double r0 = moneyFlow.r0_in - moneyFlow.r0_out;
                                double r1 = moneyFlow.r1_in - moneyFlow.r1_out;
                                double r2 = moneyFlow.r2_in - moneyFlow.r2_out;
                                double r3 = moneyFlow.r3_in - moneyFlow.r3_out;

                                double vzb = (moneyFlow.r0_out + moneyFlow.r1_out);
                                double vall = (moneyFlow.r0 + moneyFlow.r1 + moneyFlow.r2 + moneyFlow.r3);
                                String zb = "N/A";
                                if (vall > 0) {
                                    zb = String.format("%.2f%%", vzb / vall);
                                }
                                content += String.format(", 超大单净流入%.2f万元, 大单净流入%.2f万元, 中单净流入%.2f万元, 散单净流入%.2f万元, 主力资金流出占比%s。", r0, r1, r2, r3, zb);
                            }
                            content += StockOptions.kSuffixMessage;
                            message = title + ": " + content;
                        }
                        bFound = true;
                    }
                    if (!bFound){
                        message  =   "1) 查询注册id: "  + kToMe + "cx id";
                        message += "\n2) 查询订阅信息: " + kToMe + "cx dy";
                        message += "\n3) 查询个股策略: " + kToMe + "cx 股票代码";
                        message = ": " + message;
                    }
                }  else if (command.equalsIgnoreCase("注册") || command.equalsIgnoreCase("zc")) {
                    String nm = nickName;
                    if (!isFriend) {
                        nm = weChat.getNickName(groupId, fromUser);
                    }
                    if (Api.isEmpty(params) || !Validate.isPhoneNum(params)) {
                        message = nickName + "输入了无效的手机号码";
                    } else if (!Api.isEmpty(nm)) {
                        // 查询用户ID
                        ActionStatus resp = userService.query(nm);
                        if (resp.getStatus() != 0) {
                            phone = params;
                            resp = userService.register(phone, nickName, nm, "");
                            message = nickName + "注册: " + resp.getMessage();
                        } else {
                            message = nickName + "已注册过";
                        }
                    } else {
                        message = "群(" + groupName + ") 暂未开通微信助手功能";
                    }
                } else if (Api.isEmpty(phone)) {
                    message = nickName + " 未注册";
                } else if (command.equalsIgnoreCase("订阅") || command.equalsIgnoreCase("dy")){
                    ActionStatus resp = userService.subscribe(phone, params);
                    message = nickName + "，订阅" + params;
                    if (resp.getStatus() == 0) {
                        message += "成功";
                        StockMonitor sm = userService.queryPolicy(params);
                        if (sm != null) {
                            message += "";
                        }
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
