package org.tron.script;

import org.tron.dao.UserRoundDao;
import com.tronyes.nettyrest.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.protos.Protocol.*;
import org.tron.walletserver.AutoClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// check transaction status
public class CTScript {
    private static final Logger logger = LoggerFactory.getLogger(CTScript.class);

    public static void main(String[] args) {
        parseAllObjs();
    }

    private static void parseAllObjs() {

        while (true){
            Map<String, Object> conds = new HashMap<>();
            conds.put("lot_tx != ", "");
            conds.put("rwd_state = ", 2);
            conds.put("rwd_t <= ", System.currentTimeMillis() + 60_000);

            try{
                UserRoundDao dao = UserRoundDao.getByCond(conds, " id ASC ");
                if (dao == null) {
                    // FIXME 这种方法并不好, 最好采用 websocket 的方式长连接
                    Thread.sleep(1000);
                    logger.info("waiting 1000 ms");
                    continue;
                }
                Optional<TransactionInfo> result = AutoClient.getTransactionInfoById(dao.getLot_tx());

                // 如果以后起多个进程, 可以采用 select * from l_account where mod(id, 2) = 1; 解决多进程冲突的问题
                Map<String, Object> values = new HashMap<>();
                if (result.isPresent() && result.get().getResult().equals(TransactionInfo.code.SUCESS)) {
                    values.put("rwd_state", 3);
                } else {
                    logger.info("getTransactionInfoById " + " failed !!");
                    values.put("rwd_state", 4);
                }
                UserRoundDao.updateById(values, dao.getId());
            }catch (ApiException e){
                logger.warn(e.getMessage());
            }catch (InterruptedException e){
                logger.warn(e.getMessage());
                break;
            }
        }
    }
}

