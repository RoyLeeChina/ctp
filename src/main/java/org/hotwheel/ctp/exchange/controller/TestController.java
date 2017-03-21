package org.hotwheel.ctp.exchange.controller;

import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.dao.IStockHistory;
import org.hotwheel.ctp.dao.IStockMonitor;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.model.StockMonitor;
import org.hotwheel.ctp.util.EmailApi;
import org.hotwheel.ctp.util.PolicyApi;
import org.hotwheel.ctp.util.StockApi;
import org.hotwheel.io.ActionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 接口测试
 * Created by wangfeng on 2017/3/16.
 */
@Controller
@RequestMapping("/test")
public class TestController {
    private static Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private IStockHistory stockHistory;

    @Autowired
    private IStockMonitor stockMonitor;

    @ResponseBody
    @RequestMapping("/sendmail")
    public ActionStatus add(String email, String subject, String content) {
        ActionStatus resp = new ActionStatus();
        try {
            boolean ret = EmailApi.send(email, subject, content);
            if (ret) {
                resp.set(0, "SUCCESS");
            } else {
                resp.set(1, "FAILED");
            }
        } catch (Exception e) {
            resp.set(10000, e.getMessage());
            logger.error("", e);
        }
        return resp;
    }

    @ResponseBody
    @RequestMapping("/genPolicy")
    public StockMonitor genPolicy(String code) {
        StockMonitor info;
        if (Api.isEmpty(code)) {
            info =  new StockMonitor();
        } else {
            code = StockApi.fixCode(code);
            List<StockHistory> shList = stockHistory.selectOne(code);
            info = PolicyApi.dxcl(shList);
            if (info != null) {
                info.setCode(code);
                StockMonitor old = stockMonitor.query(code);
                int result = -1;
                if (old == null) {
                    result = stockMonitor.insert(info);
                    if (result == 0) {
                        logger.error("{}添加{}策略价格范围失败", info.getDay(), code);
                    }
                } else {
                    result = stockMonitor.update(info);
                    if (result == 0) {
                        logger.error("{}更新{}策略价格范围失败", info.getDay(), code);
                    }
                }
            }
        }
        return info;
    }
}
