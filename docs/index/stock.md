腾迅股票数据接口 http/javascript — 这个是我最喜欢的接口 
原创 2012年12月21日 23:48:51 
• 83017 
之前使用了新浪的股票数据，由于新浪http/javascript缺少一些数据，用chrome自带的开发工具监视腾迅财经HTTP信息，得到以下获取股票数据的方法。

以五粮液为例，要获取最新行情，访问数据接口： 
[html] view plain copy 
1. http://qt.gtimg.cn/q=sz000858 (可以批量获取：,sh600004) 
2. 沪市将sz换成sh 
返回数据： 
[html] view plain copy 
1. v_sz000858=”51~五 粮 液~000858~27.78~27.60~27.70~417909~190109~227800~27.78~492~27.77~332~27.76~202~27.75~334~27.74~291~27.79~305~27.80~570~27.81~269~27.82~448~27.83~127~15:00:13/27.78/4365/S/12124331/24602|14:56:55/27.80/14/S/38932/24395|14:56:52/27.81/116/B/322585/24392|14:56:49/27.80/131/S/364220/24385|14:56:46/27.81/5/B/13905/24381|14:56:43/27.80/31/B/86199/24375~20121221150355~0.18~0.65~28.11~27.55~27.80/413544/1151265041~417909~116339~1.10~10.14~~28.11~27.55~2.03~1054.39~1054.52~3.64~30.36~24.84~”; 
以 ~ 分割字符串中内容，下标从0开始，依次为 
[html] view plain copy 
1. 0: 未知 
2. 1: 名字 
3. 2: 代码 
4. 3: 当前价格 
5. 4: 昨收 
6. 5: 今开 
7. 6: 成交量（手） 
8. 7: 外盘 
9. 8: 内盘 
10. 9: 买一 
11. 10: 买一量（手） 
12. 11-18: 买二 买五 
13. 19: 卖一 
14. 20: 卖一量 
15. 21-28: 卖二 卖五 
16. 29: 最近逐笔成交 
17. 30: 时间 
18. 31: 涨跌 
19. 32: 涨跌% 
20. 33: 最高 
21. 34: 最低 
22. 35: 价格/成交量（手）/成交额 
23. 36: 成交量（手） 
24. 37: 成交额（万） 
25. 38: 换手率 
26. 39: 市盈率 
27. 40: 
28. 41: 最高 
29. 42: 最低 
30. 43: 振幅 
31. 44: 流通市值 
32. 45: 总市值 
33. 46: 市净率 
34. 47: 涨停价 
35. 48: 跌停价

获取实时资金流向： 
[html] view plain copy 
1. http://qt.gtimg.cn/q=ff_sz000858 
返回数据： 
[html] view plain copy 
1. v_ff_sz000858=”sz000858~41773.67~48096.67~-6322.99~-5.53~10200.89~14351.02~-4150.13~-3.63~114422.25~53015.90~59770.57~五 粮 液~20121221”; 
以 ~ 分割字符串中内容，下标从0开始，依次为： 
[html] view plain copy 
1. 0: 代码 
2. 1: 主力流入 
3. 2: 主力流出 
4. 3: 主力净流入 
5. 4: 主力净流入/资金流入流出总和 
6. 5: 散户流入 
7. 6: 散户流出 
8. 7: 散户净流入 
9. 8: 散户净流入/资金流入流出总和 
10. 9: 资金流入流出总和1+2+5+6 
11. 10: 未知 
12. 11: 未知 
13. 12: 名字 
14. 13: 日期

获取盘口分析： 
[html] view plain copy 
1. http://qt.gtimg.cn/q=s_pksz000858 
返回数据： 
[html] view plain copy 
1. v_s_pksz000858=”0.196~0.258~0.221~0.325”; 
以 ~ 分割字符串中内容，下标从0开始，依次为：

[html] view plain copy 
1. 0: 买盘大单 
2. 1: 买盘小单 
3. 2: 卖盘大单 
4. 3: 卖盘小单

获取简要信息： 
[html] view plain copy 
1. http://qt.gtimg.cn/q=s_sz000858 
返回数据： 
[html] view plain copy 
1. v_s_sz000858=”51~五 粮 液~000858~27.78~0.18~0.65~417909~116339~~1054.52”; 
以 ~ 分割字符串中内容，下标从0开始，依次为：

[html] view plain copy 
1. 0: 未知 
2. 1: 名字 
3. 2: 代码 
4. 3: 当前价格 
5. 4: 涨跌 
6. 5: 涨跌% 
7. 6: 成交量（手） 
8. 7: 成交额（万） 
9. 8: 
10. 9: 总市值

获取历史和实时股票数据接口 
实时股票数据接口 
股票数据的获取目前有如下两种方法可以获取: 
1. http/javascript接口取数据 
2. web-service接口 
1.http/javascript接口取数据 
1.1Sina股票数据接口 
以大秦铁路（股票代码：601006）为例，如果要获取它的最新行情，只需访问新浪的股票数据 
接口： 
http://hq.sinajs.cn/list=sh601006 
这个url会返回一串文本，例如： 
var hq_str_sh601006=”大秦铁路, 27.55, 27.25, 26.91, 27.55, 26.20, 26.91, 26.92, 
22114263, 589824680, 4695, 26.91, 57590, 26.90, 14700, 26.89, 14300, 
26.88, 15100, 26.87, 3100, 26.92, 8900, 26.93, 14230, 26.94, 25150, 26.95, 15220, 26.96, 2008-01-11, 15:05:32”; 
这个字符串由许多数据拼接在一起，不同含义的数据用逗号隔开了，按照程序员的思路，顺序号从0开始。 
0：”大秦铁路”，股票名字； 
1：”27.55″，今日开盘价； 
2：”27.25″，昨日收盘价； 
3：”26.91″，当前价格； 
4：”27.55″，今日最高价； 
5：”26.20″，今日最低价； 
6：”26.91″，竞买价，即“买一”报价； 
7：”26.92″，竞卖价，即“卖一”报价； 
8：”22114263″，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百； 
9：”589824680″，成交金额，单位为“元”，为了一目了然，通常以“万元”为成交金额的单位，所以通常把该值除以一万； 
10：”4695″，“买一”申请4695股，即47手； 
11：”26.91″，“买一”报价； 
12：”57590″，“买二” 
13：”26.90″，“买二” 
14：”14700″，“买三” 
15：”26.89″，“买三” 
16：”14300″，“买四” 
17：”26.88″，“买四” 
18：”15100″，“买五” 
19：”26.87″，“买五” 
20：”3100″，“卖一”申报3100股，即31手； 
21：”26.92″，“卖一”报价 
(22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖四的情况” 
30：”2008-01-11″，日期； 
31：”15:05:32″，时间； 
一个简单的JavaScript应用例子: 
script type=”text/javascript” src=”http://hq.sinajs.cn/list=sh601006” charset=”gb2312”>/script> 
/javascript”> 
var elements=hq_str_sh601006.split(“,”); 
document.write(“current price:”+elements[3]); 
这段代码输出大秦铁路（股票代码：601006）的当前股价 
current price:14.20 
如果你要同时查询多个股票，那么在URL最后加上一个逗号，再加上股票代码就可以了；比如你要一次查询大秦铁路（601006）和大同煤业（601001）的行情，就这样使用URL： 
http://hq.sinajs.cn/list=sh601003,sh601001 
查询大盘指数，比如查询上证综合指数（000001）： 
http://hq.sinajs.cn/list=s_sh000001 
服务器返回的数据为： 
var hq_str_s_sh000001=”上证指数,3094.668,-128.073,-3.97,436653,5458126”; 
数据含义分别为：指数名称，当前点数，当前价格，涨跌率，成交量（手），成交额（万元）； 
查询深圳成指数： 
http://hq.sinajs.cn/list=s_sz399001 
对于股票的K线图，日线图等的获取可以通过请求http://image.sinajs.cn/…./…/.gif此URL获取，其中代表股票代码，详见如下： 
查看日K线图： 
http://image.sinajs.cn/newchart/daily/n/sh601006.gif

分时线的查询： 
http://image.sinajs.cn/newchart/min/n/sh000001.gif

日K线查询： 
http://image.sinajs.cn/newchart/daily/n/sh000001.gif

周K线查询： 
http://image.sinajs.cn/newchart/weekly/n/sh000001.gif

月K线查询： 
http://image.sinajs.cn/newchart/monthly/n/sh000001.gif

1.2 Baidu&Google的财经数据 
在baidu, google中搜索某只股票代码时，将会在头条显示此股票的相关信息，例如在google搜索601006时， 
第一条搜索结果如下图： 
通过点击左边的图片我们发现会将此图片链接到sina财经频道上，也就是说google股票数据的获取也是从sina获取。后经抓包分析，发现google也是采用1.1中介绍的接口。 
Baidu的股票数据来自baidu的财经频道 
http://stock.baidu.com/ 
1.3 其他方式 
除了sina，baidu等网站提供股票信息外，其他网站也有类似的接口。我们分析了一款论坛上采用的股票插件， 
其中有关于实时股票数据获取的介绍，详见如下代码，其中可以看到有些数据来自sina。 
以下是ASP示例: 
=5 then 
stockdata=gethttp(“http://hq.sinajs.cn/list=sh“&code&”“) 
if not len(stockdata)=0 then stockdata=split(stockdata,chr(34))(1) 
end if 
if len(stockdata)=0 then 
stockdata=”0,0,0,0,0,0,0,0,0,0,0,0” 
else 
stockdatasplit=split(stockdata,”,”) stockdata=”“&exstock.checkstr(stockdatasplit(0))&”,”&stockdatasplit(1)&”,”&stockdatasplit(2)&”,”&stockdatasplit(3)&”,”&stockdatasplit(4)&”,”&stockdatasplit(5)&”,”&formatdatetime(“”&stockdatasplit(30)&” “&stockdatasplit(31)&”“,0)&”” 
end if 
‘0=股票名称,1=开盘价格,2=昨收盘价格,3=当前价格,4=最高价,5=最低价,6=更新时间 
getstockdata=stockdata 
end function 
function getstockimg(code) 
dim rndnum,addnum,checkcode,imgsource 
if len(code)=5 then 
getstockimg=”http://image.sinajs.cn/newchart/daily/n/sh”&code&”.gif” 
end if 
imgsource=”http://finance.sina.com.cn” 
case 3 
getstockimg=”http://hq.gazxfe.com/stockchart/realline.chart?”&code&”&1003&SZ 500 330” 
imgsource=”http://hq.gazxfe.com” 
case 4 
getstockimg=”http://chartse.stockstar.com/chartserver?code=”&code&”” 
imgsource=”http://www.stockstar.com/” 
end select 
getstockimg=split(“”&getstockimg&”||”&imgsource&”“,”||”) 
end function 
function getastockimg() 
dim rndnum,addnum,checkcode 
dim getastockimgb,imgsource 
addnum=6 
randomize:rndnum=cint(rnd*addnum) 
select case rndnum 
case 0 
getastockimg=”http://202.109.106.1/gifchartse/gif/000001.gif” 
getastockimgb=”http://202.109.106.1/gifchartse/gif/399001.gif” 
imgsource=”http://www.stockstar.com/” 
case 1 
getastockimg=”http://money.163.com/special/100.gif?C39” 
getastockimgb=”http://money.163.com/special/101.gif?HrS” 
imgsource=”http://www.163.com” 
case 2 
getastockimg=”http://www.10jqka.com.cn/curve/realtime/index2.php?code=1a0001&w=180&h=140” 
getastockimgb=”http://www.10jqka.com.cn/curve/realtime/index2.php?code=399001&w=180&h=140” 
imgsource=”http://www.10jqka.com.cn” 
case 3 
getastockimg=”http://chart.cnlist.com/stockchart/realline.chart?1a0001&1002&SZ 180 140” 
getastockimgb=”http://chart.cnlist.com/stockchart/realline.chart?399001&1002&SZ 180 140” 
imgsource=”http://chart.cnlist.com/” 
case 4 
getastockimg=”http://image.sinajs.cn/newchart/small/ish000001.gif?1189176558328” 
getastockimgb=”http://image.sinajs.cn/newchart/small/isz399001.gif?1189176558328” 
imgsource=”http://www.sinajs.cn” 
case 5 
getastockimg=”http://218.1.72.66/cgi/pic/sh/realtime/JA000001164143.png” 
getastockimgb=”http://218.1.72.66/cgi/pic/sz/realtime/JA399001164143.png” 
imgsource=”http://www.cnstock.com/” 
case 6 
getastockimg=”http://222.73.29.85/img/000001.png” 
getastockimgb=”http://222.73.29.85/img/399001.png” 
imgsource=”http://www.eastmoney.com/” 
end select 
getastockimg=split(“”&getastockimg&”||”&getastockimgb&”||”&imgsource&”“,”||”) 
end function 
%> 
2. web-service接口 
2.1 CHINAstock的web-service： 
http://www.webxml.com.cn/WebServices/ChinaStockWebService.asmx 
中国股票行情数据 WEB 服务（支持深圳和上海股市的全部基金、债券和股票），数据即时更新。输出GIF分时走势图、日/周/月 K 线图、及时行情数据（股票名称、行情时间、最新价、昨收盘、今开盘、涨跌额、最低、最高、涨跌幅、成交量、成交额、竞买价、竞卖价、委比、买一 - 买五、卖一 - 卖五）。此WEB服务提供了如下几个接口： 
2.1.1 getStockImageByCode 
GET 股票GIF分时走势图 
INput：theStockCode = 股票代号，如：sh000001 
POST /WebServices/ChinaStockWebService.asmx HTTP/1.1 
Host: www.webxml.com.cn 
Content-Type: text/xml; charset=utf-8 
Content-Length: length 
SOAPAction: “http://WebXml.com.cn/getStockImageByCode”

string

Output： 
2.1.2 getStockImageByteByCode 
获得中国股票GIF分时走势图字节数组 
INput：theStockCode = 股票代号，如：sh000001 
POST /WebServices/ChinaStockWebService.asmx HTTP/1.1Host: www.webxml.com.cnContent-Type: text/xml; charset=utf-8Content-Length: lengthSOAPAction: “http://WebXml.com.cn/getStockImageByteByCode” string 
返回的数据如下： 
R0lGODlhIQIsAfcAAAAAAAwLBxkGBQ4ODhAQEBsSChUVFS4TDB8eGQkA9koPCDAAzy4mFVgAp2UYC0IqEUYuBVwiDEAsI1QnFX8AgDU1NUozFlgxD6cBWVY5FnIwEmQ4Gc0AMlhDHPEADlVJMEpKSm1IHOUBWpY3FZMyVY9IGXRWIEFmWGNYUmpdPXJgHQB8HK9EGGBgX4lXIACoAHhkMyt4m4VkJtstbv8A 
2.1.3 getStockImage_kByCode 
直接获得中国股票GIF日/周/月 K 线图（545*300pixel/72dpi） 
INPUT: theStockCode = 股票代号 
theType = K 线图类型（D：日[默认]、W：周、M：月）， 
POST /WebServices/ChinaStockWebService.asmx HTTP/1.1Host: www.webxml.com.cnContent-Type: text/xml; charset=utf-8Content-Length: lengthSOAPAction: “http://WebXml.com.cn/getStockImage_kByCode” string string 
比如按照下图所示输入： 
返回的结果就是周K线图： 
2.1.4 getStockImage_kByteByCode 
获得中国股票GIF日/周/月 K 线图字节数组 
Input：theStockCode = 股票代号，如：sh000001 
POST /WebServices/ChinaStockWebService.asmx HTTP/1.1Host: www.webxml.com.cnContent-Type: text/xml; charset=utf-8Content-Length: lengthSOAPAction: “http://WebXml.com.cn/getStockImage_kByteByCode” string string HTTP/1.1 200 OKContent-Type: text/xml; charset=utf-8Content-Length: length base64Binary 
比如按照下图输入： 
返回的结果就是周K线图字节数组 
R0lGODlhIQIsAfcAAAAAAAwLBxkGBQ4ODhAQEBsSChUVFS4TDB8eGQkA9koPCDAAzy4mFVgAp2UYC0IqEUYuBVwiDEAsI1QnFX8AgDU1NUozFlgxD6cBWVY5FnIwEmQ4Gc0AMlhDHPEADlVJMEpKSm1IHOUBWpY3FZMyVY9IGXRWIEFmWGNYUmpdPXJgHQB8HK9EGGBgX4lXIACoAHhkMyt4m4VkJtstbv8A 
2.1.5 getStockInfoByCode 
获得中国股票及时行情 
input：theStockCode = 股票代号 
POST /WebServices/ChinaStockWebService.asmx HTTP/1.1Host: www.webxml.com.cnContent-Type: text/xml; charset=utf-8Content-Length: lengthSOAPAction: “http://WebXml.com.cn/getStockInfoByCode” string 
返回的值一个一维字符串数组 String(24)，结构为：String(0)股票代号、String(1)股票名称、String(2)行情时间、String(3)最新价（元）、String(4)昨收盘（元）、String(5)今开盘（元）、String(6)涨跌额（元）、String(7)最低（元）、String(8)最高（元）、String(9)涨跌幅（%）、String(10)成交量（手）、String(11)成交额（万元）、String(12)竞买价（元）、String(13)竞卖价（元）、String(14)委比（%）、String(15)-String(19)买一 - 买五（元）/手、String(20)-String(24)卖一 - 卖五（元）/手。 
Web service的方法类似于现在concurrent项目的DBWS数据的获取，都是通过SOAP协议向DBWS服务器获取相关的数据。 
利用雅虎查中国股票 
http://quote.yahoo.com/d/quotes.csv?s=MSFT&f=slc1wop 
返回微软的股票价格 
“MSFT”,”4:00pm - 30.70”,+1.04,”21.46 - 30.75”,29.77,29.66 
http://quote.yahoo.com/d/quotes.csv?s=000969.SZ&f=slc1wop 
这个返回安泰科技的，一般有半个小时的延迟。 
但是s=000969.sz 这个后面的sz是深圳的意思, 沪市是SS后缀

历史股票数据接口 
这几天在网上找股票的历史数据想研究研究，最后找到一个不错的接口，来自于雅虎，是在一个博客上找到的，地址为：http://www.bizeway.net/read.php?317 。不过这个不是我第一个找到的接口，最初始我是想直接解析新浪的历史交易页面，不过那个数据不是很全，只有大概近2个多月的数据。下面我把解析的脚本贴出来，主要是为了再次演示正则表达式的无穷魅力，因为解析的关键无疑是在一句正则表达式上！ 
stockCode=600000
url = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/{0}.phtml" -f stockCode
wc = New-Object System.Net.WebClient 
content=
wc.DownloadString(url)
reg = “http://biz.finance.sina.com.cn/stock/history_min.php?symbol=shd{6}&date=d{4}-d{2}-d{2}’>s*([^s]+)s+s*s*