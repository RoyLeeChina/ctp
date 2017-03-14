package org.hotwheel.stock.exchange.controller;

import org.hotwheel.assembly.Api;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.stock.StockOptions;
import org.hotwheel.stock.dao.IStockSubscribe;
import org.hotwheel.stock.model.StockSubscribe;
import org.hotwheel.stock.util.StockApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 监控接口
 * Created by wangfeng on 2017/3/14.
 */
@Controller
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private IStockSubscribe stockSubscribe;

    @ResponseBody
    @RequestMapping("/add")
    public ActionStatus add(String phone, String code) {
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
            StockSubscribe info = stockSubscribe.select(phone, code);
            int result = -1;
            if (info == null) {
                info = new StockSubscribe();
                info.setFlag(StockOptions.kNormalState);
                info.setPhone(phone);
                info.setCode(code);
                result = stockSubscribe.insert(info);
                if (result == 1) {
                    resp.set(0, "SUCCESS");
                } else {
                    resp.set(errno + 1, "添加订阅失败");
                }
            } else {
                info.setCode(code);
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
