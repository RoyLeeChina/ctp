package org.hotwheel.ctp.exchange.task;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.*;
import org.hotwheel.ctp.model.*;
import org.hotwheel.ctp.util.DateUtils;
import org.hotwheel.ctp.util.EmailApi;
import org.hotwheel.ctp.util.PolicyApi;
import org.hotwheel.weixin.bean.ContactInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 生成当天策略任务
 *
 * Created by wangfeng on 2017/3/21.
 * @version 1.0.1
 */
@Service("createPolicyTask")
public class CreatePolicyTask extends CTPContext {
    private Logger logger = LoggerFactory.getLogger(CreatePolicyTask.class);

    private final static String kIndexShangHai = "sh000001";
    private final static String kIndexShenZhen = "sz399001";
    private final static String kIndexChuangYe = "sz399006";
    private final static String kAllIndex = kIndexShangHai + ',' + kIndexShenZhen + ',' + kIndexChuangYe;

    @Autowired
    private IStockUser stockUser;

    @Autowired
    private IStockCode stockCode;

    @Autowired
    private IStockSubscribe stockSubscribe;

    @Autowired
    private IStockHistory stockHistory;

    @Autowired
    private IStockMonitor stockMonitor;

    @Autowired
    private IStockMessage stockMessage;

    @Override
    protected void service() {
        while (true) {
            if (isTimeExpire()) {
                logger.info("运行时间{}->{}到, 任务退出", taskStartTime, taskEndTime);
                break;
            }
            if (weChat == null || !weChat.isRunning()) {
                Api.sleep(StockOptions.kRealTimenterval);
                continue;
            }
            //Map<String, PolicyMessage> mapMessage = new HashMap<>();
            // 获取订阅的个股
            Set<String> allCode = new HashSet<>();
            // 上证指数
            allCode.add("sh000001");
            // 深证成指
            allCode.add("sz399001");
            // 创业板指数
            allCode.add("sz399006");
            List<String> stockList = stockSubscribe.checkoutAllCode();
            if (stockList != null && stockList.size() > 0) {
                allCode.addAll(stockList);
            }
            //allCode.clear();
            //allCode.add("sz000088");
            for (String code : allCode) {
                // 查询现存历史记录
                List<StockHistory> shList = stockHistory.selectOne(code);
                StockMonitor info = PolicyApi.dxcl(shList);
                if (info != null) {
                    info.setCode(code);
                    StockMonitor old = stockMonitor.query(code);
                    int result = -1;
                    if (old == null) {
                        result = stockMonitor.insert(info);
                        if (result == 0) {
                            logger.error("{}添加{}策略价格范围失败", info.getDay(), code);
                        }
                    } else {
                        result = stockMonitor.update(info);
                        if (result == 0) {
                            logger.error("{}更新{}策略价格范围失败", info.getDay(), code);
                        }
                    }
                    StockCode sc = stockCode.select(code, code);
                    String stockName = null;
                    if (sc != null) {
                        stockName = sc.getName();
                    }
                    String tm = Api.toString(info.getCreateTime(), StockOptions.TimeFormat);
                    logger.info("{}({}): {}~{}/{}~{}, 阻力位{}, 止损位{}。",
                            stockName, code, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                            info.getResistance(), info.getStop());
                    if (kAllIndex.indexOf(code) >= 0) {
                        String content = String.format("%s(%s): %s~%s/%s~%s, 阻力位%s, 止损位%s。",
                                stockName, code, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                                info.getResistance(), info.getStop());
                        logger.info(content);
                        String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                        String title = prefix + "-CTP策略订阅早盘提示(" + tm + ")-" + stockName;
                        content += StockOptions.kSuffixMessage;
                        weChat.sendGroupMessage("", title + ": " + content);
                    } else {
                        List<StockSubscribe> tmpSubscribe = stockSubscribe.queryByCode(code);
                        if (tmpSubscribe == null || tmpSubscribe.size() < 1) {
                            logger.info("{} 暂无用户订阅");
                        } else {
                            Map<String, StringBuffer> mapGroupMessage = new HashMap<>();
                            String content = String.format("%s(%s): 第2支撑位%s~第1支撑位%s/第1压力位%s~第2压力位%s, 阻力位%s, 止损位%s。",
                                    stockName, code, info.getSupport2(), info.getSupport1(), info.getPressure1(), info.getPressure2(),
                                    info.getResistance(), info.getStop());

                            String prefix = Api.toString(new Date(), "yyyy年MM月dd日");
                            String title = prefix + "-CTP策略早盘提示(" + tm + ")";
                            content += StockOptions.kSuffixMessage;
                            String message = title + ": " + content;
                            for (StockSubscribe userSubscribe : tmpSubscribe) {
                                UserInfo user = stockUser.select(userSubscribe.getPhone());
                                if (user == null) {
                                    logger.info("not found user={}", userSubscribe.getPhone());
                                } else {
                                    boolean bSent = true;
                                    logger.info("{}: {}", user.getMemberName(), content);
                                    Date tmpDate = user.getSendDate();
                                    Date sendDate = DateUtils.getZero(tmpDate);
                                    Date today = DateUtils.getZero(new Date());
                                    // 如果已经是第二天了
                                    if (bSent && today.after(sendDate)) {
                                        bSent = false;
                                    }
                                    // 暂时全部打开, 每只股票的已经推送的状态没有设置 [wangfeng on 2017/4/11 07:39]
                                    bSent = false;
                                    if (!bSent) {
                                        String fullName = user.getWeixin();
                                        if (!Api.isEmpty(fullName)) {
                                            ContactInfo contactInfo = weChat.parseContact(fullName);
                                            if (contactInfo == null) {
                                                logger.info("weixin={}, 好友和群信息无法识别, 不能推送", fullName);
                                            } else if (Api.isEmpty(contactInfo.getGroupId())){
                                                // 如果不是群消息
                                                weChat.sendMessageByUserId(contactInfo.getToUserName(), message);
                                            } else {
                                                // 非好友, 发信息到群里
                                                String groupId = contactInfo.getGroupId();
                                                StringBuffer sb = mapGroupMessage.get(groupId);
                                                if (sb == null) {
                                                    sb = new StringBuffer();
                                                }
                                                sb.append("@" + contactInfo.getNickName() + " ");
                                                mapGroupMessage.put(groupId, sb);
                                            }
                                        } else if (!Api.isEmpty(user.getEmail())) {
                                            EmailApi.send(user.getEmail(), prefix + "-CTP策略订阅早盘提示", content);
                                        }
                                        stockUser.finished(user);
                                    }
                                }
                            }
                            // 合并发送
                            if (mapGroupMessage.size() > 0) {
                                for (Map.Entry<String, StringBuffer> entry : mapGroupMessage.entrySet()) {
                                    String groupId = entry.getKey();
                                    StringBuffer sb = entry.getValue();
                                    if (!Api.isEmpty(groupId) && !Api.isEmpty(sb.toString())) {
                                        String groupName = weChat.getNickName(groupId);
                                        String toUser = sb.toString();
                                        logger.info("推送群消息[{}]: {}=>{}", groupName, toUser, message);
                                        weChat.sendMessageByUserId(groupId, toUser + message);
                                    }
                                }
                                mapGroupMessage.clear();
                            }
                            //Api.sleep(1000);
                        }
                    }
                }
                Api.sleep(1000);
            }
            /*
            for (Map.Entry<String, PolicyMessage> entry : mapMessage.entrySet()) {
                String phone = entry.getKey();
                PolicyMessage pm = entry.getValue();
                if (pm != null) {
                    StockMessage sm = new StockMessage();
                    sm.setFlag("00");
                    sm.setPhone(phone);
                    sm.setRemark(pm.getBuffer().toString());
                    //EmailApi.send(email, pm.getTitle(), pm.getBuffer().toString());
                    stockMessage.insert(sm);
                }
            }*/
            //Api.sleep(StockOptions.kRealTimenterval);
            break;
        }
    }
}
