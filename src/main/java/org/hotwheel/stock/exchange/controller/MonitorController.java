package org.hotwheel.stock.exchange.controller;

import org.hotwheel.stock.dao.IStockSubscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 监控接口
 * Created by wangfeng on 2017/3/14.
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private IStockSubscribe stockSubscribe;
}
