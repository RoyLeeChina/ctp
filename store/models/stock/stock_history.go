package stock

import (
	"time"
)

type StockHistory struct {
	Day        time.Time `xorm:"comment('日期') index DATE"`
	Code       string    `xorm:"not null comment('股票代码') index VARCHAR(20)"`
	Open       string    `xorm:"not null comment('开盘价') VARCHAR(20)"`
	High       string    `xorm:"not null comment('最高价') VARCHAR(20)"`
	Low        string    `xorm:"not null comment('最低价') VARCHAR(20)"`
	Close      string    `xorm:"not null comment('收盘价') VARCHAR(20)"`
	Volume     string    `xorm:"not null comment('成交量') VARCHAR(20)"`
	Ma5        string    `xorm:"not null comment('MA5价') VARCHAR(20)"`
	Ma5Volume  string    `xorm:"not null comment('MA5量') VARCHAR(20)"`
	Ma10       string    `xorm:"not null comment('MA10价') VARCHAR(20)"`
	Ma10Volume string    `xorm:"not null comment('MA10量') VARCHAR(20)"`
	Ma30       string    `xorm:"not null comment('MA30价') VARCHAR(20)"`
	Ma30Volume string    `xorm:"not null comment('MA30量') VARCHAR(20)"`
}
