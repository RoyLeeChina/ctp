--
--
-- stock.sql 行情数据(Stock) - 数据库部分
--
--
-- 版权和版本的声明
--
-- Copyright (C) 2014-2017, stock.hotwheel.org  All rights reserved.
--
-- 文件名称: stock.mysql
-- 文件标识: 行情数据 -- 数据库脚本
-- 摘    要: STOCK -- 数据库部分
--
-- 当前版本: 1.0.0 for MySQL
-- 作    者: 王锋 wangfengxy@sina.cn
-- 完成日期: 2017年03月09日
-- 使用说明: 在控制台执行:
--           Win32: mysql -vvf < stock.sql
--      Unix/Linux: mysql -uroot --socket=$HOME/runtime/mysql/mysql.sock  -vvf < stock.sql
--
--
-- MySQL数据库脚本说明如下:
--
-- 1. 使用标准的MySQL脚本规范
-- 2. 对于自动生成后台配置的关联数组的信息,定义数据在/**/的注释信息内部定义
--    2.1. 第一对/**/存放的是对本行字段的描述,包括若为form-list-object,则":"与","来分隔即将组成的关联数组
--         例如:/* 用户标志($): 00-禁止登录,01-正常登录,02-正在审批中,03-审批不通过,04-黑名单成员 */
--         2.1.1. ":"前面的部分是字段的中文名称"用户标志"
--         2.1.2. 字段中文名称后面可能包含一对"()",里面对该字段的操作字元进行了描述
--                2.1.2.1 "$" -- 表示在查询中可以显示
--                2.1.2.2 "?" -- 表示在查询中可以作为关键字段来查询
--                2.1.2.3 "$"和"?"可以单独使用,也可以组合
--                2.1.2.4 "!" -- 表示文件上传
--         2.1.3. ":"后面的部分用","分开表示不同的属性
--         2.1.4. 不同的属性由"关键字-文字描述组成",比如"01-正常登录"
--    2.3. 第二对/**/对于form-object的event动作进行了描述,里面为字符串,其语法遵循JavaScript,具体的实现在js/myjs_html.js中完成
--    2.4. 如果存在第三对/**/,那是对于form-list-object的关联数组的名称进行了描述,里面为裸名的字符串,具体的关联数组的实现在include/myphp_mysql_data.php中完成
--
--
-- SELECT COLUMN_NAME,COLUMN_COMMENT FROM INFORMATION_SCHEMA.Columns WHERE table_name='表名' AND table_schema='stock'
--


-- 创建MySQL新用户, 只允许本地连接访问

#GRANT ALL ON *.* TO stock_runtime@'%' IDENTIFIED BY 'stock_w1f2';
#GRANT ALL ON *.* TO stock_runtime@'localhost' IDENTIFIED BY 'stock_w1f2';

#SET PASSWORD FOR 'stock_runtime'@'%' = OLD_PASSWORD('stock_w1f2');
#SET PASSWORD FOR 'stock_runtime'@'localhost' = OLD_PASSWORD('stock_w1f2');

--
-- 创建数据库 stock
--

-- 如果数据库 stock 存在删除
DROP DATABASE IF EXISTS `stock`;

-- 创建CMS系统主数据库
CREATE DATABASE `stock`;
-- 修改默认数据库默认字符集
ALTER DATABASE `stock` DEFAULT CHARACTER SET utf8;

-- 打开数据库
USE `stock`;

-- TABLE: 个股列表
DROP TABLE IF EXISTS `stock`.`stock_code`;
CREATE TABLE `stock`.`stock_code`
(
  `flag`         CHAR    (2)    BINARY DEFAULT '00' COMMENT '标志($): 00-禁止检测,01-正常检测',
  `code`         CHAR    (32)   BINARY NOT NULL     COMMENT '股票代码',
  `full_code`    CHAR    (32)   BINARY NOT NULL     COMMENT '完整的股票代码',
  `name`         CHAR    (128)  BINARY NOT NULL     COMMENT '股票名称(?$)',
  `Operator`     VARCHAR (50)   BINARY NOT NULL     COMMENT '操作人(?$)',
	`CreateTime`   DATETIME          DEFAULT NULL     COMMENT '创建时间($)',
  `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY  /* 记录标号 */
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='个股列表';
ALTER TABLE `stock`.`stock_code` ADD INDEX (`code`);

-- TABLE: 监控个股列表
DROP TABLE IF EXISTS `stock`.`stock_monitor`;
CREATE TABLE `stock`.`stock_monitor`
(
	`Flag`         CHAR    (2)    BINARY DEFAULT '00'     COMMENT '标志($): 00-禁止检测,01-正常检测',
	`code`         CHAR    (32)   BINARY NOT NULL         COMMENT '股票代码',
	`support1`     CHAR    (20)   BINARY NOT NULL         COMMENT '第一支撑位',
	`support2`     CHAR    (20)   BINARY NOT NULL         COMMENT '第二支撑位',
	`pressure1`    CHAR    (20)   BINARY NOT NULL         COMMENT '第一压力位',
	`pressure2`    CHAR    (20)   BINARY NOT NULL         COMMENT '第二压力位',
	`stop`         CHAR    (20)   BINARY NOT NULL         COMMENT '止损位',
	`resistance`   CHAR    (20)   BINARY NOT NULL         COMMENT '压力位',
	`CreateTime`   DATETIME          DEFAULT NULL         COMMENT '创建时间($)',
	`Operator`     VARCHAR (50)   BINARY DEFAULT 'system' COMMENT '操作人(?$)',
	`ID`           INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY  /* 记录标号 */
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='监控个股列表';
ALTER TABLE `stock`.`stock_monitor` ADD INDEX (`code`);
ALTER TABLE `stock`.`stock_monitor` ADD INDEX (`CreateTime`);

-- TABLE: 用户表
DROP TABLE IF EXISTS `stock`.`stock_user`;
CREATE TABLE `stock`.`stock_user`
(
	`Flag`         CHAR    (2)    BINARY DEFAULT '00'     COMMENT '标志($): 00-禁止,01-正常',
	`MemberId`     CHAR    (128)  BINARY DEFAULT ''       COMMENT '客户ID',
	`MemberName`   CHAR    (128)  BINARY DEFAULT ''       COMMENT '客户姓名',
	`Phone`        CHAR    (32)   BINARY NOT NULL         COMMENT '手机号码(?$)',
	`weixin`       CHAR    (128)  BINARY DEFAULT ''       COMMENT '微信id(?$)',
	`email`        CHAR    (128)  BINARY DEFAULT ''       COMMENT '邮箱(?$)',
	`CreateTime`   DATETIME          DEFAULT NULL         COMMENT '创建时间($)',
	`SendDate`     DATE              DEFAULT NULL         COMMENT '发送日期',
	`Operator`     VARCHAR (50)   BINARY DEFAULT 'system' COMMENT '操作人(?$)',
	`ID`           INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY  /* 记录标号 */
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';
ALTER TABLE `stock`.`stock_user` ADD INDEX (`memberId`);
ALTER TABLE `stock`.`stock_user` ADD INDEX (`phone`);

-- TABLE: 用户订阅表
DROP TABLE IF EXISTS `stock`.`stock_subscribe`;
CREATE TABLE `stock`.`stock_subscribe`
(
	`flag`         CHAR    (2)    BINARY DEFAULT '00'     COMMENT '标志($): 00-禁止订阅,01-正常订阅',
	`phone`        CHAR    (32)   BINARY NOT NULL         COMMENT '客户ID',
	`code`         CHAR    (32)   BINARY NOT NULL         COMMENT '股票代码',
	`CreateTime`   DATETIME          DEFAULT NULL         COMMENT '创建时间($)',
	`SendDate`     DATE              DEFAULT NULL         COMMENT '发送日期',
	`Operator`     VARCHAR (50)   BINARY DEFAULT 'system' COMMENT '操作人(?$)',
	`ID`           INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY  /* 记录标号 */
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户订阅表';
ALTER TABLE `stock`.`stock_subscribe` ADD INDEX (`phone`);
ALTER TABLE `stock`.`stock_subscribe` ADD INDEX (`code`);

-- TABLE: 个股实时行情表
DROP TABLE IF EXISTS `stock`.`stock_realtime`;
CREATE TABLE `stock`.`stock_realtime`
(
	`type`              TINYINT               DEFAULT 2        COMMENT '类型: 1-指数,2-股票',
	`date`              DATE                  DEFAULT NULL     COMMENT '日期',
	`time`              TIME                  DEFAULT NULL     COMMENT '时间',
	`code`              CHAR    (32)   BINARY NOT NULL         COMMENT '证券代码',
	`name`              CHAR    (32)   BINARY NOT NULL         COMMENT '证券名称',
  `open`              VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '开盘价',
  `close`             VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '收盘价',
  `now`               VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '最新价',
  `high`              VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '最高价',
  `low`               VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '最低价',
  `buy_price`         VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '买入价',
  `sell_price`        VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '卖出价',
  `volume`            VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '成交量',
  `volume_price`      VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '成交额',
  `buy_1_num`         VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托买一量',
  `buy_1_price`       VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托买一价',
  `buy_2_num`         VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托买二量',
  `buy_2_price`       VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托买二价',
  `buy_3_num`         VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托买三量',
  `buy_3_price`       VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托买三价',
  `buy_4_num`         VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托买四量',
  `buy_4_price`       VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托买四价',
  `buy_5_num`         VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托买五量',
  `buy_5_price`       VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托买五价',
  `sell_1_num`        VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托卖一量',
  `sell_1_price`      VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托卖一价',
  `sell_2_num`        VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托卖二量',
  `sell_2_price`      VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托卖二价',
  `sell_3_num`        VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托卖三量',
  `sell_3_price`      VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托卖三价',
  `sell_4_num`        VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托卖四量',
  `sell_4_price`      VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托卖四价',
  `sell_5_num`        VARCHAR (20)   BINARY DEFAULT '0'      COMMENT '委托卖五量',
  `sell_5_price`      VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '委托买五价',
  `rise_fall`         VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '涨跌价',
  `rise_fall_percent` VARCHAR (20)   BINARY DEFAULT '0.000'  COMMENT '涨跌幅'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='个股实时行情表';
ALTER TABLE `stock`.`stock_realtime` ADD INDEX (`code`);

-- TABLE: 历史行情数据
DROP TABLE IF EXISTS `stock`.`stock_history`;
CREATE TABLE `stock`.`stock_history`
(
	`day`         TIMESTAMP  NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '日期',
	`code`        VARCHAR(20)  BINARY NOT NULL                      COMMENT '股票代码',
	`open`        VARCHAR(20)  BINARY NOT NULL                      COMMENT '开盘价',
	`high`        VARCHAR(20)  BINARY NOT NULL                      COMMENT '最高价',
	`low`         VARCHAR(20)  BINARY NOT NULL                      COMMENT '最低价',
	`close`       VARCHAR(20)  BINARY NOT NULL                      COMMENT '收盘价',
	`volume`      VARCHAR(20)  BINARY NOT NULL                      COMMENT '成交量',
	`MA5`         VARCHAR(20)  BINARY NOT NULL                      COMMENT 'MA5价',
	`MA5_volume`  VARCHAR(20)  BINARY NOT NULL                      COMMENT 'MA5量',
	`MA10`        VARCHAR(20)  BINARY NOT NULL                      COMMENT 'MA10价',
	`MA10_volume` VARCHAR(20)  BINARY NOT NULL                      COMMENT 'MA10量',
	`MA30`        VARCHAR(20)  BINARY NOT NULL                      COMMENT 'MA30价',
	`MA30_volume` VARCHAR(20)  BINARY NOT NULL                      COMMENT 'MA30量'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='历史行情数据';
ALTER TABLE `stock`.`stock_history` ADD INDEX (`code`);
