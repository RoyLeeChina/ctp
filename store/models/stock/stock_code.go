package stock

import (
	"time"
)

type StockCode struct {
	Flag       string    `xorm:"default '01' comment('标志($): 00-禁止检测,01-正常检测') CHAR(2)"`
	Code       string    `xorm:"not null comment('股票代码') index CHAR(32)"`
	FullCode   string    `xorm:"not null comment('完整的股票代码') CHAR(32)"`
	Name       string    `xorm:"not null comment('股票名称(?$)') CHAR(128)"`
	Operator   string    `xorm:"default 'system' comment('操作人(?$)') VARCHAR(50)"`
	Createtime time.Time `xorm:"comment('创建时间($)') DATETIME"`
	Id         int       `xorm:"not null pk autoincr INT(10)"`
}
