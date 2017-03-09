package org.hotwheel.stock.exchange.context.wind;

/**
 * 风控内部接口响应结构
 *
 * Created by wangfeng on 16/9/18.
 */
public class WindInnerResult<T> {
    public int errno;
    public String errmsg;
    public T data;
}
