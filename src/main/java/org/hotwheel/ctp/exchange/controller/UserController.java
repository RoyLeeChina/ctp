package org.hotwheel.ctp.exchange.controller;

import org.hotwheel.ctp.service.UserService;
import org.hotwheel.io.ActionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户操作
 * <p>
 * Created by wangfeng on 2017/3/13.
 *
 * @version 1.0.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("/add")
    public ActionStatus add(String phone, String name, String weixin, String email) {
        return userService.register(phone, name, weixin, email);
    }
}
