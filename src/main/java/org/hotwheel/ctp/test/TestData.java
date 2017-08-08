package org.hotwheel.ctp.test;

import org.hotwheel.assembly.RegExp;
import org.hotwheel.core.io.DefaultResourceLoader;
import org.hotwheel.core.io.Resource;
import org.hotwheel.ctp.util.ExcelApi;

import java.io.IOException;

/**
 * 测试数据
 *
 * Created by wangfeng on 2017/3/19.
 */
public class TestData {

    public static void main(String[] args) {
        String fullCode = "000001";
        final String exp = "^[0-9]{6}$";
        if (RegExp.valid(fullCode, exp)) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }
        System.out.println(RegExp.get(fullCode, exp, ""));

        String filename = "classpath:/stock/china-stock-list.xlsx";
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(filename);
        ExcelApi api = new ExcelApi();
        try {
            String filepath = resource.getFile().getAbsolutePath();
            api.read(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
