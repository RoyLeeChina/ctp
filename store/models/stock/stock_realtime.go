package stock

import (
	"time"
)

type StockRealtime struct {
	Type            int       `xorm:"default 2 comment('类型: 1-指数,2-股票') TINYINT(4)"`
	Date            time.Time `xorm:"comment('日期') DATE"`
	Time            time.Time `xorm:"comment('时间') TIME"`
	Code            string    `xorm:"not null comment('证券代码') index CHAR(32)"`
	Name            string    `xorm:"not null comment('证券名称') CHAR(32)"`
	Open            string    `xorm:"default '0.000' comment('开盘价') VARCHAR(20)"`
	Close           string    `xorm:"default '0.000' comment('收盘价') VARCHAR(20)"`
	Now             string    `xorm:"default '0.000' comment('最新价') VARCHAR(20)"`
	High            string    `xorm:"default '0.000' comment('最高价') VARCHAR(20)"`
	Low             string    `xorm:"default '0.000' comment('最低价') VARCHAR(20)"`
	BuyPrice        string    `xorm:"default '0.000' comment('买入价') VARCHAR(20)"`
	SellPrice       string    `xorm:"default '0.000' comment('卖出价') VARCHAR(20)"`
	Volume          string    `xorm:"default '0' comment('成交量') VARCHAR(20)"`
	VolumePrice     string    `xorm:"default '0.000' comment('成交额') VARCHAR(20)"`
	Buy1Num         string    `xorm:"default '0' comment('委托买一量') VARCHAR(20)"`
	Buy1Price       string    `xorm:"default '0.000' comment('委托买一价') VARCHAR(20)"`
	Buy2Num         string    `xorm:"default '0' comment('委托买二量') VARCHAR(20)"`
	Buy2Price       string    `xorm:"default '0.000' comment('委托买二价') VARCHAR(20)"`
	Buy3Num         string    `xorm:"default '0' comment('委托买三量') VARCHAR(20)"`
	Buy3Price       string    `xorm:"default '0.000' comment('委托买三价') VARCHAR(20)"`
	Buy4Num         string    `xorm:"default '0' comment('委托买四量') VARCHAR(20)"`
	Buy4Price       string    `xorm:"default '0.000' comment('委托买四价') VARCHAR(20)"`
	Buy5Num         string    `xorm:"default '0' comment('委托买五量') VARCHAR(20)"`
	Buy5Price       string    `xorm:"default '0.000' comment('委托买五价') VARCHAR(20)"`
	Sell1Num        string    `xorm:"default '0' comment('委托卖一量') VARCHAR(20)"`
	Sell1Price      string    `xorm:"default '0.000' comment('委托卖一价') VARCHAR(20)"`
	Sell2Num        string    `xorm:"default '0' comment('委托卖二量') VARCHAR(20)"`
	Sell2Price      string    `xorm:"default '0.000' comment('委托卖二价') VARCHAR(20)"`
	Sell3Num        string    `xorm:"default '0' comment('委托卖三量') VARCHAR(20)"`
	Sell3Price      string    `xorm:"default '0.000' comment('委托卖三价') VARCHAR(20)"`
	Sell4Num        string    `xorm:"default '0' comment('委托卖四量') VARCHAR(20)"`
	Sell4Price      string    `xorm:"default '0.000' comment('委托卖四价') VARCHAR(20)"`
	Sell5Num        string    `xorm:"default '0' comment('委托卖五量') VARCHAR(20)"`
	Sell5Price      string    `xorm:"default '0.000' comment('委托买五价') VARCHAR(20)"`
	RiseFall        string    `xorm:"default '0.000' comment('涨跌价') VARCHAR(20)"`
	RiseFallPercent string    `xorm:"default '0.000' comment('涨跌幅') VARCHAR(20)"`
}
