package stock

import (
	"time"
)

type StockMessages struct {
	Flag       string    `xorm:"default '00' comment('标志($): 00-未发送,01-正常发送,97-丢弃的策略消息') CHAR(2)"`
	Phone      string    `xorm:"not null comment('客户ID') index CHAR(32)"`
	Code       string    `xorm:"not null comment('股票代码') index CHAR(32)"`
	Policy     string    `xorm:"default '' comment('策略') index VARCHAR(512)"`
	Price      string    `xorm:"default '' comment('交易价格') CHAR(20)"`
	Remark     string    `xorm:"comment('策略命中备注') TEXT"`
	Createtime time.Time `xorm:"comment('创建时间($)') DATETIME"`
	Senddate   time.Time `xorm:"comment('发送日期') DATE"`
	Operator   string    `xorm:"default 'system' comment('操作人(?$)') VARCHAR(50)"`
	Id         int       `xorm:"not null pk autoincr INT(10)"`
}
