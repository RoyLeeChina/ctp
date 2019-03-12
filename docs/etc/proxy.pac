// file:///Users/wangfeng/runtime/etc/proxy.pac
var proxy = "PROXY 127.0.0.1:8080;";
var direct = 'DIRECT;';

function FindProxyForURL(url, host) {
    if (url.indexOf('/alice/api/') > 0) {
    //if (url.indexOf('http://100.73.38.59:8010/alice/api/') == 0) {
      return proxy;
    }
    return direct;
}