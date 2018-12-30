package org.tron.script;

import com.tronyes.demo.dao.LAcntDao;
import com.tronyes.demo.dao.UserRoundDao;
import com.tronyes.nettyrest.exception.StatusCode;
import io.netty.util.internal.StringUtil;
import org.quartz.*;
import com.tronyes.nettyrest.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.utils.AbiUtil;
import org.tron.core.exception.CipherException;
import org.tron.core.exception.EncodingException;
import org.tron.keystore.StringUtils;
import org.tron.walletserver.AutoClient;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@DisallowConcurrentExecution
public class AwdJob implements org.quartz.Job {
    private static final Logger logger = LoggerFactory.getLogger(AwdJob.class);
    private static final AutoClient cli = new AutoClient();

    public static JobDetail getJobDetail(String jobName, String groupName) {
        return newJob(AwdJob.class)
                .withIdentity(jobName, groupName)
                .build();
    }

    public static Trigger getTrigger(String tiggerName, String groupName) {
        // http://www.quartz-scheduler.org/documentation/quartz-2.2.x/examples/Example3.html
        return newTrigger()
                .withIdentity(tiggerName, groupName)
                .startNow()
                // 每1秒钟 check 一次
                .withSchedule(cronSchedule("0/1 * * * * ?"))
                .build();
    }

    private static void parseAllObjs() {

        Map<String, Object> conds = new HashMap<>();
        conds.put("bet_state = ", 10);
        conds.put("lot_val > ", 0);
        conds.put("rwd_state < ", 2);

        int retry = 3;
        while (true){
            try{
                UserRoundDao dao = UserRoundDao.getByCond(conds, " id ASC ");
                if (dao == null) {
                    break;
                }

                String params = String.format("\"%s\",\"%s\",%d,%d", dao.getBet_id(), dao.getAddress(), dao.getBet_val(), dao.getLot_val());
                byte[] input = Hex.decode(AbiUtil.parseMethod("rr(bytes32,address,uint256,uint256)", params, false));
                String txId = (String) cli.triggerContract("TKsietXatoavGb8EEMSnNuDUDSTJASkmie", 0, input, 20000000, 0, null);

                Map<String, Object> values = new HashMap<>();
                if (StringUtil.isNullOrEmpty(txId) || txId.equalsIgnoreCase("null")){
                    if (--retry > 0){
                        continue;
                    }else{
                        // 如果以后起多个进程, 可以采用 select * from l_account where mod(id, 2) = 1; 解决多进程冲突的问题
                        values.put("rwd_state", 4);
                        values.put("rwd_t", new Timestamp(System.currentTimeMillis()));
                    }
                }else{
                    // 如果以后起多个进程, 可以采用 select * from l_account where mod(id, 2) = 1; 解决多进程冲突的问题
                    values.put("lot_tx", txId);
                    values.put("rwd_state", 2);
                    values.put("rwd_t", new Timestamp(System.currentTimeMillis()));
                }
                retry = 3;
                UserRoundDao.updateById(values, dao.getId());
            }catch (Exception e){
                logger.warn(e.getMessage());
            }
        }
        logger.info("all done this check");
    }

    private static void loadWallet(Integer iid, String password) throws ApiException, CipherException, IOException {
        LAcntDao dao = LAcntDao.getById(iid);
        if (dao == null) {
            throw new ApiException(StatusCode.ADDRESS_EMPTY);
        }
        cli.loadWalletDao(dao,password);
    }

    public static void main(String[] args) {
        try {
            loadWallet(6,"Star@2018");
            parseAllObjs();
        }catch (CipherException | IOException | ApiException e){
            logger.warn("exit with " + e.toString());
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            loadWallet(6,"Star@2018");
            parseAllObjs();
        }catch (CipherException | IOException | ApiException e){
            logger.warn("exit with " + e.toString());
        }
    }
}

