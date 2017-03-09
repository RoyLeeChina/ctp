package org.hotwheel.stock.exchange.context.jdb.v2;

import java.util.Date;

/**
 * Created by wangfeng on 2017/1/18.
 */
public class MiniDebt implements Comparable{
    public String uuid = "";
    public Date endTime = new Date(0);

    @Override
    public int compareTo(Object o) {
        MiniDebt dest = (MiniDebt)o;
        return this.uuid.compareTo(dest.uuid);
    }
}
