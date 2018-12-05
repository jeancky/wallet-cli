package org.tron.script;

import com.tronyes.demo.dao.UserRoundDao;
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
        conds.put("bet_state = ", 10);
        conds.put("lot_val > ", 0);
        conds.put("rwd_state < ", 2);

        while (true){
            try{
                UserRoundDao dao = UserRoundDao.getByCond(conds, " id ASC ");
                if (dao == null) {
                    break;
                }
                String params = String.format("\"%s\",\"%s\",%d,%d", dao.getBet_id(), dao.getAddress(), dao.getBet_val(), dao.getLot_val());
                byte[] input = Hex.decode(AbiUtil.parseMethod("rr(bytes32,address,uint256,uint256)", params, false));
                String txId = cli.triggerContract("TKsietXatoavGb8EEMSnNuDUDSTJASkmie", 0, input, 20000000, 0, null);

                // 如果以后起多个进程, 可以采用 select * from l_account where mod(id, 2) = 1; 解决多进程冲突的问题
                Map<String, Object> values = new HashMap<>();
                values.put("lot_tx", txId);
                values.put("rwd_state", 2);
                values.put("rwd_t", new Timestamp(System.currentTimeMillis()));
                UserRoundDao.updateById(values, dao.getId());
            }catch (ApiException | EncodingException e){
                logger.warn(e.getMessage());
            }
        }
        logger.info("all done this check");
    }

    public static void main(String[] args) {
        try {
            cli.loadWalletDao("Star@2018", 6);
            parseAllObjs();
        }catch (CipherException | IOException | ApiException e){
            logger.warn("exit with " + e.toString());
        }
    }
}

