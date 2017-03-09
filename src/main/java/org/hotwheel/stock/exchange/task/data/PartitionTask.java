package org.hotwheel.stock.exchange.task.data;

import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.scheduling.FastBatchTask;
import org.hotwheel.scheduling.PartitionContext;
import org.mymmsc.api.assembly.Api;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static java.io.File.separator;

/**
 * 任务块
 *
 * Created by wangfeng on 2016/11/20.
 */
public abstract class PartitionTask<T extends PartitionContext> extends FastBatchTask<T> {
    protected final static String reportPath = Runtime.reportPath;
    protected final static int kBatchSize = Runtime.batchSize;


    public PartitionTask(int threadNum, int threshold, int batchSize, String taskName) {
        super(threadNum, threshold, batchSize, taskName);
    }

    protected boolean isNewFile(File file, String path, String fileName) {
        boolean bRet = false;
        Date today = new Date();
        String curDateStr = Api.toString(today, "yyyyMMdd");
        String basePath = path.concat(separator).concat(curDateStr);
        String filePath = basePath.concat(separator).concat(fileName);
        bRet = !file.getAbsolutePath().equals(filePath);
        if(bRet) {
            logger.info("create new file {}", filePath);
        }
        return bRet;
    }

    protected File createCVSFile(String path, String fileName) {
        Date today = new Date();
        String curDateStr = Api.toString(today, "yyyyMMdd");
        String basePath = path.concat(separator).concat(curDateStr);
        File dir = new File(basePath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                //logger.error("目录创建失败！");
            }
        }
        String filePath = basePath.concat(separator).concat(fileName);
        File newFile = new File(filePath);
        try {
            newFile.deleteOnExit();
            if (!newFile.createNewFile()) {
                //logger.error("文件创建失败！");
            }
        } catch (IOException e) {
            //logger.error("文件创建失败：", e);
            return null;
        }
        return newFile;
    }
}
