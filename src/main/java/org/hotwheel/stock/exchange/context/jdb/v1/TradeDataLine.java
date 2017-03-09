package org.hotwheel.stock.exchange.context.jdb.v1;

import org.hotwheel.stock.exchange.context.BaseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 交易数据行
 *
 * Created by wangfeng on 16/8/26.
 */
public abstract class TradeDataLine {
    /**< 日志记录器 */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final static String DDF           = "yyyyMMdd";
    protected final static String DDL           = "yyyyMMddHHmmss";
    protected final static String separator     = File.separator;
    protected final static String lineSeparator = System.lineSeparator();
    private static ResourceBundle rbRuntime     = null;

    private static String reportPath = null;
    private File csvFile = null;
    protected String csvFilename = null;

    static {
        rbRuntime = ResourceBundle.getBundle("runtime", Locale.getDefault());

        String tmp = rbRuntime.getString("overdue.report.path");
        reportPath = new String(tmp);
    }

    /**
     * 输出交易单行数据
     * @return
     */
    public abstract String toLine();

    protected abstract String getCSVFilename();

    public File getFile() {
        getCSVFilename();
        return BaseContext.createCVSFile(reportPath, csvFilename);
    }

    public File getFile(String csvFilename) {
        return BaseContext.createCVSFile(reportPath, csvFilename);
    }
}
