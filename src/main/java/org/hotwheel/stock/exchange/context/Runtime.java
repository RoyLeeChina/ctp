package org.hotwheel.stock.exchange.context;

import org.hotwheel.stock.exchange.util.ShortMessageCenter;
import org.hotwheel.beans.EValue;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.ResourceApi;
import org.mymmsc.api.redis.RedisApi;

import java.util.ResourceBundle;

/**
 * 运行时配置入口
 *
 * Created by wangfeng on 2016/10/26.
 */
public final class Runtime {
    private static ResourceBundle rbRuntime = null;
    private static ResourceBundle rbRedis = null;

    public final static String env;
    public final static ShortMessageCenter smcApi;

    public final static String kDateFormatOfRedis = "yyyyMMdd";

    // redis 对象
    private static RedisApi redisApi = null;

    // redis前缀
    public final static String cachePrefix = Category.prefixRedisKey;
    // 批量redis缓存的前缀
    public final static String kDebtorTask = Runtime.cachePrefix + "_batch";
    // 债务人列表缓存的前缀
    public final static String kDebtorList = Runtime.cachePrefix + "_batch_debtorlist";
    // 给债务系统的备份redis的key前缀
    public final static String kDebtorBak  = "postloan_debt_caiji_debtor";
    // 债务数据推送完成设置redis状态
    public final static String kDebtFinished = "postloan_case_finish_caiji";

    private final static String kTaskKey = "trade.batch";

    public final static int kShardMax = 8;
    /** 最大处理的记录数阀值 */
    public static int threshold = 20000;
    /**< 最大任务线程数 */
    public static int threadNum = 4;
    public static int batchSize = 200;
    // 是否调试
    public final static boolean hasDebug;
    // 是否等待上游数据准备状态
    public final static boolean hasWait;
    // 是否输出日志
    public final static boolean hasBatchOutLog;
    // 是否上传
    public final static boolean hasUpload;
    public final static boolean hasDebtProtection;

    // 批量文件缓存路径
    public final static String ftpIp;
    public final static int    ftpPort;
    public final static String ftpUserName;
    public final static String ftpPassWord;
    public final static String remotePath;
    public final static String reportPath;
    public final static String reportCallBack;

    // 服务器
    public final static String tradeHost;
    // appKey
    public final static String tradeAppkey;
    // MD5 Key
    public final static String tradeMd5Key;

    // redis配置信息
    public final static String redisHost;
    public final static int    redisPort;
    public final static int    redisDatabase;
    public final static String redisPassword;

    public static String payAppId = null;
    public static String payAppKey = null;
    public static String payListUrl = null;

    public final static String sfzAppId;
    public final static String sfzAppKey;
    public final static String sfzListUrl;

    public static String hackedAppKey = null;
    public static String hackedUrl = null;

    public static String friendAppId = null;
    public static String friendAppKey = null;
    public static String userAppId = null;
    public static String userAppKey = null;

    public static String friendsListUrl = null;
    public static String usersListUrl = null;

    public static String phoneAppId = null;
    public static String phoneAppKey = null;
    public static String phoneListUrl = null;

    // 好友开关
    public final static boolean hasFriends;
    // 通讯录开关
    public final static boolean hasPhoneList;

    // 每日逾期数据状态
    public final static String urlOverdueJobState;
    // 获取全部债务人id
    public final static String urlDebtorIdList;
    // 获取全部产品id
    public final static String urlProductIdList;
    // 获取产品列表的债务以及数据
    public final static String urlDebtList;
    public final static String urlRepayList;

    // 短信预警开关
    public final static boolean hasNotify;

    // 债务系统接口
    public final static String debtBidUrl;
    public final static String debtOrderUrl;
    public final static String debtRepayUrl;

    public final static String verifyBidUrl;
    public final static String verifyOrderUrl;
    public final static String verifyRepayUrl;

    // 账户系统
    public final static String accountAppId;
    public final static String accountAppKey;
    public final static String debtorUrl;
    public final static String creditorUrl;

    static {
        // 加载资源属性文件
        rbRuntime = ResourceApi.getBundle("runtime");
        rbRedis   = ResourceApi.getBundle("redis");

        env = getRuntime("env", String.class);
        smcApi = ShortMessageCenter.getInstance(rbRuntime);
        hasNotify = getBoolean("smsc.notify");

        hasDebug     = getRuntime("batch.debug", boolean.class);
        hasWait      = getRuntime("batch.wait", boolean.class);
        hasFriends   = getRuntime("batch.friends", boolean.class);
        hasPhoneList = getRuntime("batch.phone", boolean.class);

        // redis配置信息
        redisHost     = getRedis("redis.host", String.class);
        redisPort     = getRedis("redis.port", int.class);
        redisDatabase = getRedis("redis.database", int.class);
        redisPassword = getRedis("redis.password", String.class);

        // 并发控制
        threadNum      = getRuntime("batch.threadnum", int.class);
        threshold      = getRuntime("batch.threshold", int.class);
        batchSize      = getRuntime("batch.sql.limit", int.class);

        hasUpload      = getRuntime("batch.upload", boolean.class);
        hasBatchOutLog = getRuntime("batch.outlog", boolean.class);
        hasDebtProtection = getRuntime("batch.debtProtection", boolean.class);
        // SFTP配置信息
        ftpIp          = getRuntime("batch.ftp.ip", String.class);
        ftpPort        = getRuntime("batch.ftp.port", int.class);
        ftpUserName    = getRuntime("batch.ftp.username", String.class);
        ftpPassWord    = getRuntime("batch.ftp.password", String.class);
        remotePath     = getRuntime("batch.ftp.remotePath", String.class);
        reportCallBack = getRuntime("overdue.report.callBack", String.class);
        reportPath     = getRuntime("overdue.report.path", String.class);


        // 读取 参数
        tradeHost     = getRuntime(kTaskKey + ".host", String.class);
        tradeAppkey   = getRuntime(kTaskKey + ".appkey", String.class);
        tradeMd5Key   = getRuntime(kTaskKey + ".md5key", String.class);

        urlOverdueJobState  = /*tradeHost + */getRuntime(kTaskKey + ".debtor.jobstate", String.class);
        // 分页获取交易所有逾期用户ID的List
        urlDebtorIdList     = tradeHost + getRuntime(kTaskKey + ".debtor.idlist", String.class);
        // 逾期标的
        urlProductIdList    = tradeHost + getRuntime(kTaskKey + ".debtor.bidlist", String.class);
        // 标的详情
        // 获取产品列表的债务以及数据
        urlDebtList         = tradeHost + getRuntime( kTaskKey + ".debtor.bidinfo", String.class);
        // 标定订单详情
        //orderListUrl      = getRuntime(kTaskKey + ".debtor.order.list", String.class);
        // 还款明细 接口
        //urlRepayList      = tradeHost + getRuntime( kTaskKey + ".debtor.repayinfo", String.class);
        urlRepayList      = getRuntime( kTaskKey + ".debtor.repayinfo", String.class);

        // 停催标识
        hackedAppKey = getRuntime(kTaskKey + ".wind.appkey", String.class);
        hackedUrl    = getRuntime(kTaskKey + ".debtor.hacked.list.url", String.class);

        // 支付
        payAppId     = getRuntime(kTaskKey + ".pay.appid", String.class);
        payAppKey    = getRuntime(kTaskKey + ".pay.appkey", String.class);
        payListUrl   = getRuntime(kTaskKey + ".debtor.mapping.list", String.class);

        sfzAppId     = getRuntime(kTaskKey + ".sfz.appid", String.class);
        sfzAppKey    = getRuntime(kTaskKey + ".sfz.appkey", String.class);
        sfzListUrl   = getRuntime(kTaskKey + ".debtor.sfz.list", String.class);

        // 好友
        friendAppId    = getRuntime(kTaskKey + ".friend.appid", String.class);
        friendAppKey   = getRuntime(kTaskKey + ".friend.appkey", String.class);
        friendsListUrl = getRuntime(kTaskKey + ".debtor.friend.list", String.class);

        userAppId      = getRuntime(kTaskKey + ".user.appid", String.class);
        userAppKey     = getRuntime(kTaskKey + ".user.appkey", String.class);
        usersListUrl   = getRuntime(kTaskKey + ".debtor.user.list", String.class);

        // 通讯录配置
        phoneAppId     = getRuntime(kTaskKey + ".phone.appid", String.class);
        phoneAppKey    = getRuntime(kTaskKey + ".phone.appkey", String.class);
        phoneListUrl   = getRuntime(kTaskKey + ".debtor.phone.list", String.class);

        debtBidUrl     = getRuntime("debt.bid.url", String.class);
        debtOrderUrl   = getRuntime("debt.order.url", String.class);
        debtRepayUrl   = getRuntime("debt.repay.url", String.class);

        verifyBidUrl   = getRuntime("verify.bid.url", String.class);
        verifyOrderUrl = getRuntime("verify.order.url", String.class);
        verifyRepayUrl = getRuntime("verify.repay.url", String.class);

        accountAppId   = getRuntime("account.appid", String.class);
        accountAppKey  = getRuntime("account.appkey", String.class);
        debtorUrl      = getRuntime("account.debtor.url", String.class);
        creditorUrl    = getRuntime("account.creditor.url", String.class);

        // 初始化 redis
        redisApi = RedisApi.getInstance(Runtime.redisHost, Runtime.redisPort, Runtime.redisDatabase, Runtime.redisPassword);
    }

    public static RedisApi getRedisApi() {
        return redisApi;
    }


    private static boolean getBoolean(String key) {
        EValue value = new EValue(rbRuntime.getString(key));
        return value.toBoolean();
    }


    private static <T> T getRuntime(String key, Class<T> clazz) {
        String value = rbRuntime.getString(key);
        return Api.valueOf(clazz, value);
    }

    private static <T> T getRedis(String key, Class<T> clazz) {
        String value = rbRedis.getString(key);
        return Api.valueOf(clazz, value);
    }

}
