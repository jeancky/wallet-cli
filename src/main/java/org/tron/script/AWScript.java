package org.tron.script;

import org.tron.dao.UserRoundDao;
import com.tronyes.nettyrest.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.tron.common.utils.AbiUtil;
import org.tron.core.exception.CipherException;
import org.tron.core.exception.EncodingException;
import org.tron.walletserver.AutoClient;

import java.io.IOException;
import java.util.*;

public class AWScript {
    private static final Logger logger = LoggerFactory.getLogger(AWScript.class);
    private static final AutoClient cli = new AutoClient();

    public static void main(String[] args) {
        try {
            cli.loadWalletDao("Star@2018", 1);
            parseAllObjs();
        }catch (CipherException | IOException | ApiException e){
            logger.warn("exit with " + e.toString());
        }
    }

    private static void parseAllObjs() {

        Map<String, Object> conds = new HashMap<>();
        conds.put("bet_state = ", 10);
        conds.put("lot_val > ", 0);
        conds.put("rwd_state < ", 2);

        while (true){
            try{
                UserRoundDao dao = UserRoundDao.getByCond(conds, " id ASC ");
                if (dao == null) {
                    // FIXME 这种方法并不好, 最好采用 websocket 的方式长连接
                    Thread.sleep(500);
                    logger.info("waiting 500 ms");
                    continue;
                }

                String params = String.format("\"%s\",\"%s\",%ld,%ld", dao.getAddress(), dao.getBet_id(), dao.getBet_val(), dao.getLot_val());
                byte[] input = Hex.decode(AbiUtil.parseMethod("rr(address,bytes32,uint256,uint256)", params, false));
                String txId = cli.triggerContract("TYq2YNFB4tFfs4bit8kfAxspD7fnN7Vi9b", 0, input, 1000000000, 0, null);

                // 如果以后起多个进程, 可以采用 select * from l_account where mod(id, 2) = 1; 解决多进程冲突的问题
                Map<String, Object> values = new HashMap<>();
                values.put("lot_tx", txId);
                values.put("rwd_state", 2);
                UserRoundDao.updateById(values, dao.getId());
            }catch (ApiException | EncodingException e){
                logger.warn(e.getMessage());
            }catch (InterruptedException e){
                logger.warn(e.getMessage());
                break;
            }
        }
    }
}

