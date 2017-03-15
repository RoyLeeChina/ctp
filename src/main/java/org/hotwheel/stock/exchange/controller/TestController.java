package org.hotwheel.stock.exchange.controller;

import org.hotwheel.io.ActionStatus;
import org.hotwheel.stock.util.EmailApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 接口测试
 * Created by wangfeng on 2017/3/16.
 */
@Controller
@RequestMapping("/test")
public class TestController {
    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @ResponseBody
    @RequestMapping("/sendmail")
    public ActionStatus add(String email, String subject, String content) {
        ActionStatus resp = new ActionStatus();
        try {
            EmailApi.send(email, subject, content);
            resp.set(0, "SUCCESS");
        } catch (Exception e) {
            resp.set(10000, e.getMessage());
            logger.error("", e);
        }
        return resp;
    }
}
