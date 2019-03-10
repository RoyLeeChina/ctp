package org.hotwheel.ctp.exchange.controller;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.dao.IStockMonitor;
import org.hotwheel.ctp.model.StockMonitor;
import org.hotwheel.ctp.service.UserService;
import org.hotwheel.ctp.util.StockApi;
import org.hotwheel.io.ActionStatus;
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
@RequestMapping("/strategy")
public class MonitorController {

    @Autowired
    private UserService userService;

    @Autowired
    private IStockMonitor stockMonitor;

    @ResponseBody
    @RequestMapping("/subscribe/add")
    public ActionStatus addSubscribe(String phone, String code) {
        return userService.subscribe(phone, code);
    }

    /**
     * 插入一个监控数据
     *
     * @param code
     * @param range 格式"第一支撑,第一压力,第二支撑,第二压力,止损位,阻力位"
     * @return
     */
    @ResponseBody
    @RequestMapping("/monitor/add")
    public ActionStatus add(String code, String range) {
        ActionStatus resp = new ActionStatus();
        int errno = 10000;
        Date today = new Date();
        String message = "策略已经存在";
        String fullCode = StockApi.fixCode(code);
        if (Api.isEmpty(code)) {
            // 代码为空
            resp.set(errno + 2, "股票代码不能为空");
        } else if (Api.isEmpty(fullCode)) {
            // 代码为空
            resp.set(errno + 3, "股票代码无效");
        } else if (Api.isEmpty(range)) {
            // 价格范围
            resp.set(errno + 4, "策略价格范围");
        } else {
            String[] array = range.split(",");
            if (array.length < 6) {
                resp.set(errno + 4, "策略价格范围不完整");
            } else {
                double support1 = Api.valueOf(double.class, array[0]);
                double support2 = Api.valueOf(double.class, array[2]);
                double pressure1 = Api.valueOf(double.class, array[1]);
                double pressure2 = Api.valueOf(double.class, array[3]);
                double stop = Api.valueOf(double.class, array[4]);
                double resistance = Api.valueOf(double.class, array[5]);

                StockMonitor info = stockMonitor.query(fullCode);
                int result = -1;
                if (info == null) {
                    info = new StockMonitor();
                    info.setFlag(StockOptions.kNormalState);
                    info.setCode(fullCode);
                    info.setCreateTime(today);
                    info.setPressure1(Api.toString(pressure1));
                    info.setSupport1(Api.toString(support1));
                    info.setPressure2(Api.toString(pressure2));
                    info.setSupport2(Api.toString(support2));
                    info.setStop(Api.toString(stop));
                    info.setResistance(Api.toString(resistance));
                    result = stockMonitor.insert(info);
                    if (result == 1) {
                        resp.set(0, "SUCCESS");
                    } else {
                        resp.set(errno + 1, "添加策略价格范围失败");
                    }
                } else {
                    info.setFlag(StockOptions.kNormalState);
                    info.setCode(fullCode);
                    info.setCreateTime(today);
                    info.setPressure1(Api.toString(pressure1));
                    info.setSupport1(Api.toString(support1));
                    info.setPressure2(Api.toString(pressure2));
                    info.setSupport2(Api.toString(support2));
                    info.setStop(Api.toString(stop));
                    info.setResistance(Api.toString(resistance));
                    result = stockMonitor.update(info);
                    if (result == 1) {
                        resp.set(0, "SUCCESS");
                    } else {
                        resp.set(errno, message);
                    }
                }
            }
        }

        return resp;
    }
}
