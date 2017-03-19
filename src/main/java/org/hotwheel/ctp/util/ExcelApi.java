package org.hotwheel.ctp.util;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.hotwheel.ctp.dao.IStockCode;
import org.hotwheel.ctp.model.StockCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Excel 工具类
 *
 * Created by wangfeng on 2017/3/19.
 * @version 1.0.1
 */
@Service("excelApi")
public class ExcelApi {
    private static Logger logger = LoggerFactory.getLogger(ExcelApi.class);

    @Autowired
    private IStockCode stockCode;

    public ExcelApi() {
        //
    }

    /**
     * 读取excel文件，生成一个json文件，文件格式见项目根目录的.xlsx文件
     * @param inFileName excel文件文件路径
     */
    public void read(String inFileName){
        try
        {
            InputStream inp = new FileInputStream(inFileName);
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);
            Boolean flag = true;
            int startRow = 1;
            while(flag){
                Row row = sheet.getRow(startRow++);
                if(row != null){
                    Cell code = row.getCell(0);
                    Cell name = row.getCell(1);
                    if(code != null && name != null){
                        StockCode stock = new StockCode();
                        String codeString = code.getStringCellValue();
                        String nameString = name.getStringCellValue();
                        stock.setCode(codeString);
                        stock.setName(nameString);
                        String fullCode = StockApi.fixCode(codeString);
                        stock.setFull_code(fullCode);
                        StockCode old = stockCode.select(codeString, fullCode);
                        if (old == null) {
                            stockCode.insert(stock);
                        } else {
                            stockCode.update(stock);
                        }
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Don't find " + inFileName);
        } catch (EncryptedDocumentException e) {
            logger.error("", e);
        } catch (InvalidFormatException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
