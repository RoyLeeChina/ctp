package org.hotwheel.ctp.service;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockSubscribe;
import org.hotwheel.ctp.dao.IStockUser;
import org.hotwheel.ctp.model.StockSubscribe;
import org.hotwheel.ctp.model.UserInfo;
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
    private static Map<String, String> mapUsers = new HashMap<>();

    @Autowired
    private IStockUser stockUser;

    @Autowired
    private IStockSubscribe stockSubscribe;

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

    public String getPhone(final String weixin) {
        initUser();
        return mapUsers.get(weixin);
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
            resp.set(errno + 1, "用户不存在，请联系管理员");
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
        int errno = 10000;
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
                resp.set(errno + 1, "添加用户失败");
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
        int errno = 10000;
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
                    resp.set(errno + 1, "添加订阅失败");
                }
            } else {
                info.setCode(fullCode);
                info.setCreateTime(new Date());
                result = stockSubscribe.update(info);
                if (result == 1) {
                    resp.set(0, "SUCCESS");
                } else {
                    resp.set(errno, message);
                }
            }
        }

        return resp;
    }
}
