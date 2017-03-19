package org.hotwheel.ctp.exchange.task;

import org.hotwheel.core.io.DefaultResourceLoader;
import org.hotwheel.core.io.Resource;
import org.hotwheel.ctp.util.ExcelApi;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 市场数据任务
 *
 * Created by wangfeng on 2017/3/19.
 * @version 1.0.1
 */
@Service("marketDataTask")
public class MarketDataTask extends SchedulerContext {
    private Logger logger = LoggerFactory.getLogger(MarketDataTask.class);

    @Autowired
    private ExcelApi excelApi;

    @Override
    protected void service() {
        String filename = "classpath:/stock/china-stock-list.xlsx";
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(filename);
        try {
            String filepath = resource.getFile().getAbsolutePath();
            excelApi.read(filepath);
        } catch (IOException e) {
            logger.error("read file failed: ", e);
        }
    }
}
