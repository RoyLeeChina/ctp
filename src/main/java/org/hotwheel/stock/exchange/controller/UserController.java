package org.hotwheel.stock.exchange.controller;

import org.hotwheel.assembly.Api;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.stock.dao.IStockUser;
import org.hotwheel.stock.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户操作
 *
 * Created by wangfeng on 2017/3/13.
 * @version 1.0.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IStockUser stockUser;

    @ResponseBody
    @RequestMapping("/add")
    public ActionStatus add(String phone, String name, String weixin, String email) {
        ActionStatus resp = new ActionStatus();
        int errno = 10000;
        String message = "用户已经存在";
        if (Api.isEmpty(name)) {
            name = phone;
        }
        if (Api.isEmpty(weixin)) {
            weixin = phone;
        }
        User user = stockUser.select(phone);
        if (user == null) {
            user = new User();
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
}
