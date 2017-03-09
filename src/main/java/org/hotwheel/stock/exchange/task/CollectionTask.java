package org.hotwheel.stock.exchange.task;

import com.google.common.collect.Lists;
import org.hotwheel.stock.exchange.context.Runtime;
import org.hotwheel.stock.exchange.context.trade.OverdueJobState;
import org.hotwheel.stock.exchange.dao.stock.IOverdueErrorDao;
import org.hotwheel.stock.exchange.http.InnerApi;
import org.hotwheel.stock.exchange.http.DebtApi;
import org.hotwheel.stock.exchange.http.TradeInnerApi;
import org.hotwheel.stock.exchange.task.data.DebtorIdTask;
import org.hotwheel.stock.exchange.task.data.ShardContext;
import org.hotwheel.scheduling.ThreadPool;
import org.hotwheel.spring.scheduler.SchedulerContext;
import org.mymmsc.api.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;


/**
 * 催收采集侧交易数据批量处理引擎, 初始
 *
 * Created by wangfeng on 16/8/14.
 * @since 3.0.3
 */
@Service("CollectServiceTask")
public class CollectionTask extends SchedulerContext {
    private static final Logger logger = LoggerFactory.getLogger(CollectionTask.class);

    @Autowired
    private IOverdueErrorDao overdueErrorDao;

    protected void service() {
        InnerApi.notify("债务和相关数据，开始执行");
        DebtApi.init();
        // 债务人ID集合
        Set<String> oldDebtors = new HashSet<>();
        Set<String> allDebtors = new HashSet<>();
        // 0. 获取债务系统就绪状态
        OverdueJobState overdueJobState = null;
        /*
        while (overdueJobState == null || overdueJobState.state != 1) {
            overdueJobState = TradeInnerApi.getJobState();
            if (overdueJobState != null && overdueJobState.state == 1) {
                break;
            } else {
                Api.sleep(1000 * 60 * 1);
            }
        }
        */

        // 1. 从催收系统拉取全部未结清标的债务人
        List<String> csList = overdueErrorDao.getAllDebtors();
        if (csList != null) {
            oldDebtors.addAll(csList);
            allDebtors.addAll(csList);
        }
        // 2. 从催收系统拉取全部错误的标的债务人
        // 3. 从交易接口获取新的逾期的债务人

        logger.info("notice: ermas debtors {} rows", allDebtors.size());

        // 从交易系统拉取债务人id, overdue_day=1
        List<Integer> shardList = new ArrayList<Integer>();
        for (int shard = 0; shard < Runtime.kShardMax; shard++) {
            shardList.add(shard);
        }
        long rtm = System.currentTimeMillis();
        Set<String> newDebtors = new HashSet<>();
        ThreadPool tpDebotId = new ThreadPool(Runtime.threadNum);
        DebtorIdTask debtorIdTask = new DebtorIdTask(Runtime.kShardMax, 1, 1, "DebtorIdTask");
        debtorIdTask.init(0, Runtime.kShardMax, shardList);
        //执行一个任务
        Future<ShardContext> futureContext = tpDebotId.submit(debtorIdTask);
        Api.nanoSleep(1);
        if(futureContext != null) {
            ShardContext ret = null;
            try {
                ret = futureContext.get();
                if(ret != null && ret.rows != null) {
                    for (String debtorId : ret.rows) {
                        if (!Api.isEmpty(debtorId)) {
                            //logger.error("1-debtor={}", debtorId);
                            allDebtors.add(debtorId);
                            if (!oldDebtors.contains(debtorId)) {
                                newDebtors.add(debtorId);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("FJP get failed.", e);
            }
        }
        tpDebotId.close();
        long n = allDebtors.size();
        long ums = (System.currentTimeMillis() - rtm);
        logger.info("use                  : " + ums + "ms");
        logger.info("Time taken for tests : " + (ums/1000) +" seconds");
        logger.info("process              : " + (ums/n) + "ms/peer");
        logger.info("Requests per second  : " + (n * 1000/ ums) + " [#/sec] (mean)");
        InnerApi.notify("债务人列表,Requests per second: " + (n * 1000/ ums) + " [#/sec] (mean)");

        DebtApi.push(Lists.newArrayList(allDebtors));
        logger.info("notice: all debtors {} rows", allDebtors.size());
        TradeInnerApi.finishedDebtors();
    }
}
