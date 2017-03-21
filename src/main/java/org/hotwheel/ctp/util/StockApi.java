package org.hotwheel.ctp.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.hotwheel.asio.HttpApi;
import org.hotwheel.assembly.Api;
import org.hotwheel.ctp.StockOptions;
import org.hotwheel.ctp.data.HistoryUtils;
import org.hotwheel.ctp.data.RealTimeUtils;
import org.hotwheel.ctp.model.StockHistory;
import org.hotwheel.ctp.model.StockRealTime;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.io.HttpClient;
import org.hotwheel.io.HttpResult;
import org.hotwheel.json.JsonAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 股票接口
 * <p>
 * Created by wangfeng on 2017/3/12.
 */
public final class StockApi {

    private static Logger logger = LoggerFactory.getLogger(StockApi.class);

    /**
     * 价格转字符串
     * @param price
     * @return
     */
    public static String toPrice(final double price) {
        return String.format("%.3f", price);
    }

    /**
     * 规范 代码
     *
     * @param fullcode
     * @return 不合规范的代码 返回null
     */
    public static String fixCode(final String fullcode) {
        String code = fullcode.toLowerCase().trim();
        if (code.startsWith("sh") || code.startsWith("sz")) {
            // 代码前缀正确
            code = code.substring(2);
        } else if (code.length() == 6) {
            // 6位数字
        } else {
            // 非sh或sz开头或长度不等于6
            code = null;
        }

        if (!Api.isInteger(code)) {
            // 非数字
            code = null;
        } else {
            // 如果是纯数字
            if (code.startsWith("6")) {
                code = "sh" + code;
            } else {
                code = "sz" + code;
            }
        }
        return code;
    }

    public static <T> T request(String url, Map headers, Map object, Class<T> clazz, Class subClass) {
        T obj = null;
        ActionStatus as = new ActionStatus();
        int errCode = 900;
        String params = HttpApi.getParams(object);
        //System.out.println(params);
        logger.info("request={}, params={}", url, params);
        HttpClient hc = new HttpClient(url, 30);
        HttpResult hRet = hc.post(headers, object);

        logger.info("http-status=[" + hRet.getStatus() + "], body=[" + hRet.getBody() + "], message="
                + hRet.getError());
        if (hRet == null) {
            as.set(errCode + 0, "调用接口失败");
        } else if (hRet.getStatus() >= 400) {
            as.set(errCode + 1, String.format("调用接口失败: %d, %s", hRet.getStatus(), hRet.getError()));
        } else if (hRet.getStatus() != 200) {
            as.set(errCode + 2, String.format("调用接口成功, 但是: %d, %s", hRet.getStatus(), hRet.getError()));
        } else if (hRet.getBody() == null) {
            as.set(errCode + 3, "HTTP接口返回BODY为空");
        } else if (clazz == List.class && subClass != null) {
            //subClass = clazz.getComponentType();
            List list = JSON.parseArray(hRet.getBody(), subClass);
            if (list != null) {
                obj = (T) list.toArray();
            }
        } else if (clazz.isArray() && subClass == null) {
            subClass = clazz.getComponentType();
            List list = JSON.parseArray(hRet.getBody(), subClass);
            if (list == null) {
                as.set(errCode + 11, "接口返回内容不能匹配");
            } else {
                obj = (T) list.toArray();
                as.set(0, "接口成功");
            }
        } else {
            JsonAdapter json = JsonAdapter.parse(hRet.getBody());
            if (json == null) {
                as.set(errCode + 10, "调用接口失败");
            } else {
                try {
                    obj = (T) json.get(clazz, subClass);
                    if (obj == null) {
                        as.set(errCode + 11, "接口返回内容不能匹配");
                    } else {
                        as.set(0, "接口成功");
                    }
                } catch (Exception e) {
                    logger.error("", e);
                } finally {
                    json.close();
                }
            }
        }
        logger.info("request={}, result={}", url, JsonAdapter.get(as, false));
        return obj;
    }

    public static List<StockHistory> getHistory(final String code) {
        List<StockHistory> result;
        //result = request(StockOptions.urlHistory, null, StockOptions.historyParams(code), List.class, StockHistory.class);
        result = HistoryUtils.getKLineDataObjects(code, StockOptions.ONE_DAY);
        return result;
    }

    public static List<StockHistory> getHistory(final String code, final long days) {
        List<StockHistory> result;
        //result = request(StockOptions.urlHistory, null, StockOptions.historyParams(code), List.class, StockHistory.class);
        result = HistoryUtils.getKLineDataObjects(code, StockOptions.ONE_DAY, "" + days);
        return result;
    }

    public static List<StockRealTime> getRealTime(final List<String> listCode) {
        List<StockRealTime> result = null;
        String[] codes = listCode.toArray(new String[]{});
        result = RealTimeUtils.getRealTimeDataObjects(codes);

        return result;
    }

    /**
     * 发送get请求，返回内容字符串
     *
     * @param url         请求urll
     * @param charsetName 字符码
     * @return 响应内容字符串
     */
    public static String sendHTTPGET(String url, String charsetName) {
        String result = "";
        HttpGet httpGet = new HttpGet(url);
        try (
                CloseableHttpClient httpclient = HttpClients.createDefault();
                CloseableHttpResponse response = httpclient.execute(httpGet);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                result = InputStreamToString(entity.getContent(), charsetName);
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        return result;
    }

    /**
     * 将如"2017-01-07 14:07:35"或"2017-01-07"这样的字符串转换为LocalDateTime对象
     *
     * @param time 时间字符串
     * @return {@link Date}对象
     */
    public static Date string2LocalDateTime(String time) {
        Date result;
        if (time.length() > 10) {
            result = Api.toDate(time, StockOptions.TimeFormat);
        } else {
            result = Api.toDate(time, StockOptions.DateFormat);
        }
        return result;
    }

    /**
     * 将{@link InputStream}转换为{@link String}
     *
     * @param in          {@link InputStream}
     * @param charsetName 字符串编码
     * @return 返回String字符串
     * @throws UnsupportedEncodingException 不支持的编码
     * @throws IOException                  io错误
     */
    public static String InputStreamToString(InputStream in, String charsetName) throws UnsupportedEncodingException, IOException {
        StringBuffer sb = new StringBuffer();
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = in.read(b)) != -1) {
            sb.append(new String(b, 0, len, charsetName));
        }
        return sb.toString();
    }

    /**
     * 从方法生成的文件中读取指定属性
     *
     * @param file 文件路径
     * @param key  关键词
     * @return 一个{@link List}
     * @throws Exception 错误
     */
    public static List<String> getValueFromJSONFile(String file, String key) throws Exception {
        List<String> result = new ArrayList<>();
        InputStream inp = new FileInputStream(file);
        JSONArray jsonarray = new JSONArray(InputStreamToString(inp, "UTF-8"));
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject obj = jsonarray.optJSONObject(i);
            if (obj != null) {
                String value = obj.optString(key);
                if (!value.equals("")) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    /**
     * 读取excel文件，生成一个json文件，文件格式见项目根目录的.xlsx文件
     *
     * @param inFileName  excel文件文件路径
     * @param outFileName 输出文件路径
     */
    public static void readExcel2JSON(String inFileName, String outFileName) {
        try
                (
                        InputStream inp = new FileInputStream(inFileName);
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFileName)));) {
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);
            Boolean flag = true;
            int startRow = 1;
            JSONWriter json = new JSONWriter(out);
            json.array();
            while (flag) {
                Row row = sheet.getRow(startRow++);
                if (row != null) {
                    Cell code = row.getCell(0);
                    Cell name = row.getCell(1);
                    if (code != null && name != null) {
                        String codeString = code.getStringCellValue();
                        String nameString = name.getStringCellValue();
                        String fullCode = "";
                        switch (codeString.charAt(0)) {
                            case '6':
                                fullCode = "sh" + codeString;
                                break;
                            case '0':
                                fullCode = "sz" + codeString;
                                break;
                            case '3':
                                fullCode = "sz" + codeString;
                                break;
                        }
                        json.object();
                        json.key("code").value(codeString);
                        json.key("fullCode").value(fullCode);
                        json.key("name").value(nameString);
                        json.endObject();
                    } else {
                        flag = false;
                    }
                } else {
                    flag = false;
                }
            }
            json.endArray();
        } catch (FileNotFoundException e) {
            System.out.print("Don't find " + inFileName);
            e.printStackTrace();
        } catch (EncryptedDocumentException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
