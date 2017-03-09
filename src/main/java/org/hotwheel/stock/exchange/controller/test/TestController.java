package org.hotwheel.stock.exchange.controller.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试控制器
 *
 * Created by wangfeng on 2017/1/10.
 */
@Controller
@RequestMapping("/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    /**
     * 项目活跃测试接口
     * @return
     */
    @RequestMapping(value = "/status.cgi", method = RequestMethod.GET)
    @ResponseBody
    public String testStatus() {
        return "SUCCESS";
    }
}
