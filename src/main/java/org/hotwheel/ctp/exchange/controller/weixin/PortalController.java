package org.hotwheel.ctp.exchange.controller.weixin;

import org.hotwheel.assembly.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.TreeMap;

/**
 * 微信
 * Created by wangfeng on 2017/3/25.
 * @version 1.0.2
 */
@Controller
@RequestMapping("/third")
public class PortalController {
    private final static String kToken = "stockExchange";
    private final static String kEncodingAESKey = "Pl2la9FY1Ka91Py1Kf5lMGFt0BGSuff87AUMO0vZAyt";

    @RequestMapping("/wx")
    @ResponseBody
    public String wxPortal(String signature, String timestamp, String nonce, String echostr) {
        String sRet = "OK";
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("token", kToken);
        params.put("timestamp", timestamp);
        params.put("nonce", nonce);

        StringBuilder preSign = new StringBuilder();
        for (Map.Entry<String, Object> entry: params.entrySet()) {
            preSign.append(Api.toString(entry.getValue()));
            //preSign.append('|');
        }
        sRet = Api.md5(preSign.toString());
        sRet = sRet.toLowerCase();
        return sRet;
    }
}
