package com.tronyes.nettyrest.script;

import com.tronyes.lottery.dao.UserRoundDao;
import com.tronyes.nettyrest.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AwardScript {
    private static final Logger logger = LoggerFactory.getLogger(AwardScript.class);

    public static void main(String[] args) {
        try {
            parseAllRewards();
        }catch (ApiException e){
            logger.warn("exit with " + e.toString());
        }
    }

    private static boolean parseAllRewards() throws ApiException {
        int limit = 10;
        int escapeCount = 0;

        Map<String, Object> conds = new HashMap<>();
        conds.put("bet_state = ", 10);
        conds.put("lot_val > ", 0);
        conds.put("rwd_state < ", 2);

        while (true){
            List<UserRoundDao> all = UserRoundDao.getListByCond(conds, " id ASC ", limit, escapeCount);
            for (UserRoundDao uRound: all) {

            }
        }
    }
}

