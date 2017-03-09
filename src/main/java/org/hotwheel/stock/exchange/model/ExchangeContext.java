package org.hotwheel.stock.exchange.model;

import org.hotwheel.scheduling.PartitionContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exchange上下文
 *
 * Created by wangfeng on 2017/1/20.
 */
public class ExchangeContext<String> extends PartitionContext<String, ExchangeContext> /*implements FastContext<ExchangeContext>*/ {
    public List<String> allBidUuids = null;
    public Set<String> creditors = new HashSet<>();
    public Set<String> debtIds = new HashSet<>();
    public long numberOfBid = 0;
    public long numberOfOrder = 0;

    @Override
    public boolean merge(ExchangeContext context) {
        creditors.addAll(context.creditors);
        debtIds.addAll(context.debtIds);
        numberOfBid += context.numberOfBid;
        numberOfOrder += context.numberOfOrder;
        return true;
    }
}
