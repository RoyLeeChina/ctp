package org.hotwheel.stock.exchange.controller;

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
    public ActionStatus add(String phone, String weixin) {
        ActionStatus resp = new ActionStatus();
        int errno = 10000;
        String message = "用户已经存在";
        User user = stockUser.select(phone);
        if (user == null) {
            user = new User();
            user.setMemberId("");
            user.setMemberName("wangfeng");
            user.setPhone(phone);
            user.setWeixin(weixin);
            int result = stockUser.insert(user);
            if (result == 1) {
                resp.set(0, "SUCCESS");
            } else {
                resp.set(errno + 1, "添加用户失败");
            }
        } else {
            resp.set(errno, message);
        }


        return resp;
    }
}
