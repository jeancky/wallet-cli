package org.tron.script;

import com.tronyes.demo.dao.LAcntDao;
import com.tronyes.demo.dao.LPlayerDao;
import com.tronyes.demo.dao.UserRoundDao;
import com.tronyes.nettyrest.exception.StatusCode;
import org.quartz.*;
import com.tronyes.nettyrest.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.utils.AbiUtil;
import org.tron.core.exception.CipherException;
import org.tron.core.exception.EncodingException;
import org.tron.walletserver.AutoClient;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;


@DisallowConcurrentExecution
public class RobJob {
    private static final Logger logger = LoggerFactory.getLogger(RobJob.class);
    private static final AutoClient cli = new AutoClient();

    private static void parseAllObjs() {

        Map<String, Object> conds = new HashMap<>();
        conds.put("alb_amt >= ", 110_000_000); // 可用余额大于100TRX
        conds.put("lb_t < ", new Timestamp(System.currentTimeMillis() - 360_000));
        conds.put("state = ", 1);

        while (true){
            try{
                LPlayerDao dao = LPlayerDao.getById(1);
                if (dao == null) {
                    throw new ApiException(StatusCode.ADDRESS_EMPTY);
                }
                cli.loadWalletDao(dao, "Star@2018");

                List<Object> params = new LinkedList<Object>(){ {
                    add("param-str");
                    add(20);
                }};

                byte[] input = AbiUtil.parseMethod("ca(address,uint16)", params, false);
                String txId = cli.triggerContract("TKsietXatoavGb8EEMSnNuDUDSTJASkmie", 0, input, 20000000, 0, null);

                // 如果以后起多个进程, 可以采用 select * from l_account where mod(id, 2) = 1; 解决多进程冲突的问题
                Map<String, Object> values = new HashMap<>();
                values.put("lot_tx", txId);
                values.put("rwd_state", 2);
                values.put("rwd_t", new Timestamp(System.currentTimeMillis()));
                UserRoundDao.updateById(values, dao.getId());
            }catch (ApiException | EncodingException | IOException | CipherException e){
                logger.warn(e.getMessage());
            }
        }
        logger.info("all done this check");
    }

    public static void main(String[] args) {
        parseAllObjs();
    }
}

