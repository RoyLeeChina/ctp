package stock

import (
	"time"
)

type StockMonitor struct {
	Flag       string    `xorm:"default '00' comment('标志($): 00-禁止检测,01-正常检测') CHAR(2)"`
	Code       string    `xorm:"not null comment('股票代码') index CHAR(32)"`
	Day        time.Time `xorm:"not null comment('日期') index DATE"`
	Support1   string    `xorm:"default '0.000' comment('第一支撑位') CHAR(20)"`
	Support2   string    `xorm:"default '0.000' comment('第二支撑位') CHAR(20)"`
	Pressure1  string    `xorm:"default '0.000' comment('第一压力位') CHAR(20)"`
	Pressure2  string    `xorm:"default '0.000' comment('第二压力位') CHAR(20)"`
	Stop       string    `xorm:"default '0.000' comment('止损位') CHAR(20)"`
	Resistance string    `xorm:"default '0.000' comment('压力位') CHAR(20)"`
	Remark     string    `xorm:"comment('策略命中备注') TEXT"`
	Createtime time.Time `xorm:"comment('创建时间($)') DATETIME"`
	Operator   string    `xorm:"default 'system' comment('操作人(?$)') VARCHAR(50)"`
	Id         int       `xorm:"not null pk autoincr INT(10)"`
}
