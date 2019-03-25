package stock

import (
	"time"
)

type StockSubscribe struct {
	Flag       string    `xorm:"default '00' comment('标志($): 00-禁止订阅,01-正常订阅') CHAR(2)"`
	Phone      string    `xorm:"not null comment('客户ID') index CHAR(32)"`
	Code       string    `xorm:"not null comment('股票代码') index CHAR(32)"`
	Createtime time.Time `xorm:"comment('创建时间($)') DATETIME"`
	Senddate   time.Time `xorm:"comment('发送日期') DATE"`
	Remark     string    `xorm:"comment('策略命中备注') TEXT"`
	Operator   string    `xorm:"default 'system' comment('操作人(?$)') VARCHAR(50)"`
	Id         int       `xorm:"not null pk autoincr INT(10)"`
}
