package org.hotwheel.stock.exchange.context;

/**
 * 固定参数
 * 
 * @author wangfeng
 * @date 2015年12月28日 上午11:05:37
 */
public final class Category {
	/** number of microseconds per second */
	public final static long USEC_PER_SEC = 1000000L;
	/** number of milliseconds per second */
	public final static long MSEC_PER_SEC = 1000L;
	/** 一天的秒数 */
	public final static long SecondOfDay = 24L * 60L * 60L;
	/** 一天的毫秒数 */
	public final static long MillisecondsOfDay = SecondOfDay * MSEC_PER_SEC;
	
	/** 时间格式 */
	public final static String TimeFormat  = "yyyy-MM-dd HH:mm:ss";
	/** 日期格式 */
	public final static String DateFormat  = "yyyy-MM-dd";
	public final static String DateFormat2 = "yyyyMMdd";
	public final static String DDF         = "yyyyMMdd";
	public final static String DDL         = "yyyyMMddHHmmss";

	/** 时间戳格式 */
	public final static String TimeStamp = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/** JDB环境 */
	public final static String ENV_JDB_SCHEMA = "JDB_SCHEMA";
	public final static String ENV_DSMP_TIMER = "DSMP_TIMER";
	
	/** WEB 数据库连接池资源名称 -- 采集系统数据库 */
	public final static String DBCP_DSMP = "jdbc/dsmp";// + '_' + JDBSchema.getEnv();
	/** WEB 数据库连接池资源名称 -- 催收平台数据库 */
	public final static String DBCP_ERMAS = "jdbc/cuishou";// + '_' + JDBSchema.getEnv();
	
	/** APP 数据库连接池资源名称 -- 采集系统数据库 */
	public final static String jndiDsmp = "app/dsmp";
	/** APP 数据库连接池资源名称 -- 采集系统数据库 */
	public final static String jndiErmas = "app/cuishou";
	
	
	/** 本地催收数据库 */
	public final static String DB_ERMAS = "ermas_trunk";
	/** DEV催收数据库 */
	//public final static String DB_ERMAS = "ermas_erqi";
	/** BETA催收数据库 */
	//public final static String DB_ERMAS = "ermas_erqi";
	/** BETA催收数据库 */
	//public final static String DB_ERMAS = "ermas_erqi";
	
	/** 数据库连接池资源名称 */
	public final static String DBCP_APPS = "jdbc/apps";
	/** 默认返回错误页面, 返回上一页面 */
	public final static String ReturnUrl = "javascript: history.back(-1)";
	/** session, 用户信息关键字 */
	public final static String SESSION_USER = "dsmp_user";
	/** session, 产品信息关键字 */
	public final static String SESSION_PRODUCT = "dsmp_proudct";

	
	/** codis公共集群采集前缀 */
	public final static String prefixCodisDsmp = "jdb_cuiji";
	/** redis公共集群key前缀 */
	//public final static String prefixRedisKey = "jdb_collect";
	public final static String prefixRedisKey = "postloan_exchange";
	/** redis公共集群key前缀 */
	public final static String suffixAuthcode = "com.jiedaibao.dsmp.md5";
		
	/** 系统充值默认商户 */
	public final static String PROVIDER = "FXF";
	/** 支付状态 */
	public final static String CHARGE_SUCCESS = "0";
	
	/** 交易状态 */
	public final static String PAY_SUCCESS = "0";

	/** 日志类型-帐户-冲值 */
	public final static String LOGTYPE_CHARGE = "01";
	/** 日志类型-交易-支付 */
	public final static String LOGTYPE_PAY = "11";
	/** 日志类型-交易-销售 */
	public final static String LOGTYPE_SALE = "12";

	public final static int PAGE_RECORD_NUM = 10;
	public final static String SESSION_PAGE = "paging";

	/** 客户端类型标识-Android */
	public final static int CLIT_ANDROID = 0;
	/** 客户端类型标识-iOS for AppStore */
	public final static int CLIT_IOS = 1;
	/** 客户端类型标识-iOS for 企业 */
	public final static int CLIT_IOSCOM = 2;
	
	//----------< 借贷宝催收系统参数 >----------
	public final static String accessKey = "0aa2c432-0163-421d-84d7-1acb0085a243";
	
	public final static String tmBegin = "0900";
	public final static String tmEnd = "1800";

	//----------< 字符串类 默认常亮定义 >----------

	/** 不显示 默认值 */
	public final static String kNoShowed ="--";
	/** 废弃的 默认值 */
	public final static String kAbandoned ="---";
	/** 不可用 */
	public final static String kUnusable = "N/A";

}
