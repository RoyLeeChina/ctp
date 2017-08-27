package org.hotwheel.ctp.service;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.*;
import org.hotwheel.ctp.model.*;
import org.hotwheel.ctp.util.PolicyApi;
import org.hotwheel.ctp.util.StockApi;
import org.hotwheel.io.ActionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务处理接口
 *
 * Created by wangfeng on 2017/3/28.
 * @version 1.0.3
 */
@Service("userService")
public class UserService {
    private final static int kErrorCode = 10000;
    private final static String kMeNickname = "CTP助手";
    private final static String kContactByError = "系统操作异常，请@王布衣。";
    private static Map<String, String> mapUsers = new HashMap<>();

    @Autowired
    private IStockUser stockUser;

    @Autowired
    private IStockSubscribe stockSubscribe;

    @Autowired
    private IStockCode stockCode;

    @Autowired
    private IStockMonitor stockMonitor;

    @Autowired
    private IStockHistory stockHistory;

    private void initUser() {
        if (mapUsers.size() < 1) {
            List<UserInfo> list = stockUser.selectAll();
            if (list != null) {
                for (UserInfo info : list) {
                    mapUsers.put(info.getWeixin(), info.getPhone());
                }
            }
        }
    }

    public String getPhone1(final String weixin) {
        initUser();
        String sRet = mapUsers.get(weixin);
        if (Api.isEmpty(sRet)) {
            for (Map.Entry<String, String> entry : mapUsers.entrySet()) {
                String wx = entry.getKey();
                String phone = entry.getValue();
                if (wx.startsWith(weixin + "@")) {
                    sRet = phone;
                    break;
                }
            }
        }
        return sRet;
    }

    /**
     * 通过手机号码获取微信昵称
     * @param weixin
     * @return
     */
    public String getPhone(final String weixin) {
        String sRet = null;
        UserInfo info = stockUser.selectByWeixin(weixin);
        if (info != null) {
            sRet = info.getPhone();
        }
        return sRet;
    }

    /**
     * 根据证券名称查询证券代码
     * @param stockName
     * @return
     */
    public String getFullCode(String stockName) {
        String sRet = null;
        StockCode info = stockCode.selectByName(stockName);
        if (info != null) {
            sRet = info.getFull_code();
        }

        return sRet;
    }

    /**
     * 查询自己的用户id-手机号码
     * @param weixin
     * @return
     */
    public ActionStatus query(String weixin) {
        ActionStatus resp = new ActionStatus();
        int errno = kErrorCode + 1000;
        UserInfo info = stockUser.selectByWeixin(weixin);
        if (info == null) {
            resp.set(errno + 1, "用户不存在");
        } else {
            resp.set(0, info.getPhone());
        }

        return resp;
    }

    /**
     * 用户注册
     * @param phone
     * @param name
     * @param weixin
     * @param email
     * @return
     */
    public ActionStatus register(String phone, String name, String weixin, String email) {
        ActionStatus resp = new ActionStatus();
        int errno = kErrorCode + 1100;
        String message = "用户已经存在";
        if (!Api.isEmpty(phone) && !Api.isEmpty(weixin)) {
            mapUsers.put(weixin, phone);
        }
        if (Api.isEmpty(name)) {
            name = phone;
        }
        if (Api.isEmpty(weixin)) {
            weixin = phone;
        }
        UserInfo user = stockUser.select(phone);
        if (user == null) {
            user = new UserInfo();
            user.setMemberId(phone);
            user.setMemberName(name);
            user.setPhone(phone);
            user.setWeixin(weixin);
            user.setEmail(email);
            int result = stockUser.insert(user);
            if (result == 1) {
                resp.set(0, "SUCCESS");
            } else {
                resp.set(errno + 1, "添加用户失败，" + kContactByError);
            }
        } else {
            user.setMemberName(name);
            user.setWeixin(weixin);
            user.setEmail(email);
            stockUser.update(user);
            resp.set(errno, message);
        }

        return resp;
    }

    /**
     * 订阅
     *
     * @param phone
     * @param code
     * @return
     */
    public ActionStatus subscribe(String phone, String code) {
        ActionStatus resp = new ActionStatus();
        int errno = kErrorCode + 1200;
        String message = "订阅已经存在";
        String fullCode = StockApi.fixCode(code);
        if (Api.isEmpty(phone)) {
            // 手机号码为空
            resp.set(errno + 1, "手机号码不能为空");
        } else if (Api.isEmpty(code)) {
            // 代码为空
            resp.set(errno + 2, "股票代码不能为空");
        } else if (Api.isEmpty(fullCode)) {
            // 代码为空
            resp.set(errno + 3, "股票代码无效");
        } else {
            StockCode scInfo = stockCode.select(code, fullCode);
            if (scInfo == null) {
                resp.set(errno + 4, "股票代码非法或暂未被CTP系统收录，如确系需要，请@王布衣");
            } else if (!scInfo.getFlag().equals(StockOptions.kNormalState)) {
                resp.set(errno + 5, scInfo.getName() + "(" + code + "), CTP暂停该股预警服务，如确系需要，请@王布衣");
            } else {
                StockSubscribe info = stockSubscribe.select(phone, fullCode);
                int result = -1;
                if (info == null) {
                    info = new StockSubscribe();
                    info.setFlag(StockOptions.kNormalState);
                    info.setPhone(phone);
                    info.setCode(fullCode);
                    result = stockSubscribe.insert(info);
                    if (result == 1) {
                        resp.set(0, "SUCCESS");
                    } else {
                        resp.set(errno + 6, "添加订阅失败，" + kContactByError);
                    }
                } else if (info.getFlag().equals(StockOptions.kNormalState)) {
                    resp.set(errno + 7, message);
                } else {
                    info.setFlag(StockOptions.kNormalState);
                    info.setCode(fullCode);
                    info.setCreateTime(new Date());
                    result = stockSubscribe.update(info);
                    if (result == 1) {
                        resp.set(0, "早前订阅暂停，现已恢复。");
                    } else {
                        resp.set(errno + 8, "订阅信息更新失败，" + kContactByError);
                    }
                }
            }
        }

        return resp;
    }

    /**
     * 退订
     *
     * @param phone
     * @param code
     * @return
     */
    public ActionStatus unsubscribe(String phone, String code) {
        ActionStatus resp = new ActionStatus();
        int errno = kErrorCode + 1300;
        String message = "没有订阅";
        String fullCode = StockApi.fixCode(code);
        if (Api.isEmpty(phone)) {
            // 手机号码为空
            resp.set(errno + 1, "手机号码不能为空");
        } else if (Api.isEmpty(code)) {
            // 代码为空
            resp.set(errno + 2, "股票代码不能为空");
        } else if (Api.isEmpty(fullCode)) {
            // 代码为空
            resp.set(errno + 3, "股票代码无效");
        } else {
            StockSubscribe info = stockSubscribe.select(phone, fullCode);
            int result = -1;
            if (info != null) {
                info.setFlag(StockOptions.kNullState);
                info.setCode(fullCode);
                info.setCreateTime(new Date());
                result = stockSubscribe.update(info);
                if (result > 0) {
                    resp.set(0, "SUCCESS");
                } else {
                    resp.set(errno + 4, "已退订");
                }
            } else {
                resp.set(errno, message);
            }
        }

        return resp;
    }

    /**
     * 查询订阅情况
     * @param phone
     * @return
     */
    public String querySubscribe(final String phone) {
        String sRet = null;
        List<String> list = stockSubscribe.checkoutByPhone(phone);
        if (list != null && list.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (String code: list) {
                StockCode sc = stockCode.select(code, code);
                if (sc != null) {
                    sb.append(",");
                    sb.append(sc.getName()).append("(").append(sc.getCode()).append(")");
                }
            }
            //sRet = StringUtils.collectionToDelimitedString(list, ",");
            sRet = sb.substring(1);
        }

        return sRet;
    }

    /**
     * 查询策略
     * @param code
     * @return
     */
    public StockMonitor queryPolicy(String code) {
        StockMonitor info = null;
        if (!Api.isEmpty(code)) {
            code = StockApi.fixCode(code);
            info = stockMonitor.query(code);
            if (info == null) {
                List<StockHistory> shList = stockHistory.selectOne(code);
                info = PolicyApi.dxcl(shList);
                if (info != null) {
                    info.setCode(code);
                    int result = stockMonitor.insert(info);
                }
            }
        }
        return info;
    }
}
