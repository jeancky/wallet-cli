package org.tron.script;

import com.tronyes.demo.dao.UserRoundDao;
import org.quartz.*;
import org.tron.common.utils.ByteArray;
import com.tronyes.nettyrest.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.protos.Protocol.*;
import org.tron.walletserver.AutoClient;

import java.net.ConnectException;
import java.sql.Timestamp;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@DisallowConcurrentExecution
public class CheckTxJob implements org.quartz.Job {
    private static final Logger logger = LoggerFactory.getLogger(CheckTxJob.class);
    // "every_5_min"
    public static JobDetail getJobDetail(String jobName, String groupName) {
        return newJob(CheckTxJob.class)
                .withIdentity(jobName, groupName)
                .build();
    }

    public static Trigger getTrigger(String tiggerName, String groupName) {
        // http://www.quartz-scheduler.org/documentation/quartz-2.2.x/examples/Example3.html
        return newTrigger()
                .withIdentity(tiggerName, groupName)
                .startNow()
                // 每3秒钟 check 一次
                .withSchedule(cronSchedule("0/3 * * * * ?"))
                .build();
    }

    private static void parseAllObjs() {

        while (true){
            Map<String, Object> conds = new HashMap<>();
            conds.put("lot_tx != ", "");
            conds.put("rwd_state = ", 2);
            // 由于1分钟才会固化, 所以不能只能 check 最近一分钟的数据
            conds.put("rwd_t <= ", new Timestamp(System.currentTimeMillis() - 60_000));

            try{
                UserRoundDao dao = UserRoundDao.getByCond(conds, " id ASC ");
                if (dao == null) {
                    break;
                }
                Optional<TransactionInfo> result = AutoClient.getTransactionInfoById(dao.getLot_tx());

                // 如果以后起多个进程, 可以采用 select * from l_account where mod(id, 2) = 1; 解决多进程冲突的问题
                Map<String, Object> values = new HashMap<>();
                if (result.isPresent()
                    && result.get().getResult().equals(TransactionInfo.code.SUCESS)
                    && result.get().getContractResultCount() > 0
                    && Long.parseLong(ByteArray.toHexString(result.get().getContractResult(0).toByteArray()), 16) == 1) {
                    values.put("rwd_state", 3);
                } else {
                    logger.info("getTransactionInfoById " + dao.getLot_tx() + " failed !!");
                    if (dao.getRwd_retry() > 0){
                        values.put("rwd_state", 1);
                        values.put("rwd_retry", dao.getRwd_retry() - 1);
                    }else {
                        values.put("rwd_state", 4);
                    }
                }
                UserRoundDao.updateById(values, dao.getId());
            }catch (Exception e){
                logger.warn(e.getMessage());
            }
        }
        logger.info("all done this check");
    }

    public static void main(String[] args) {
        parseAllObjs();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        parseAllObjs();
    }
}

