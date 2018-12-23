package org.tron.script;

import com.alibaba.fastjson.JSON;
import com.tronyes.demo.dao.*;
import com.tronyes.demo.service.BetService;
import com.tronyes.nettyrest.exception.StatusCode;
import org.quartz.*;
import com.tronyes.nettyrest.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.common.crypto.Hash;
import org.tron.common.utils.AbiUtil;
import org.tron.common.utils.ByteArray;
import org.tron.core.exception.CipherException;
import org.tron.core.exception.EncodingException;
import org.tron.keystore.WalletFile;
import org.tron.protos.Protocol.Account;
import org.tron.protos.Protocol.TransactionInfo;
import org.tron.walletserver.AutoClient;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;


@DisallowConcurrentExecution
public class RobJob {
    private static final Logger logger = LoggerFactory.getLogger(RobJob.class);
    private static final AutoClient cli = new AutoClient();

    private static String LottoContract = "TQy3pheSHVUHuxGLdkhp2H7yhJbsqUiiBn";
    private static String salt = "_&g23!@#&$asdde";

    private static String playerPswd = "Star@2018@goole&112358";

    private static final AutoClient bankClient = new AutoClient();
    static {
        try {
            LAcntDao dao = LAcntDao.getById(7);
            bankClient.loadWalletDao(dao, "Star@2018");
        } catch (ApiException | IOException | CipherException e) {
            e.printStackTrace();
        }
    }

    private static long getBetValue(){
        long betVal = 50_000_000;
        int rand = (int) (Math.random() * 10000);
        if (rand > 9900){
            betVal = 200_000_000;
        }else if (rand > 8800){
            betVal = 100_000_000;
        }else if (rand > 7500){
            betVal = 50_000_000;
        }
        return betVal;
    }

    private static Long checkBalance(LPlayerDao dao, long betValue) throws ApiException, InterruptedException {
        betValue += 3_000_000;

        Account account = cli.queryAccount();
        Long balance = account.getBalance();
        if (betValue > balance){
            String bankTxId = bankClient.sendCoin(dao.getAddress(), 200_000_000 - balance);
            if (bankTxId != null && bankTxId.length() > 0){
                Thread.sleep(3_000);
                account = cli.queryAccount();
                balance = account.getBalance();
            }
            if (betValue > balance){
                throw new ApiException(StatusCode.SCPT_SHORT_COIN, "address: " + dao.getAddress() + " balance: " + balance + " need :" + betValue);
            }
        }
        return balance;
    }

    private static GameRoundDao getGame(int retry) throws ApiException, InterruptedException {
        if (retry <= 0)
            return null;

        GameRoundDao gameRound = GameRoundDao.getOpenGame();
        if (gameRound == null){
            throw new ApiException(StatusCode.BET_FAILED);
        }
        if (gameRound.getLot_t().getTime() <= System.currentTimeMillis() + 5_000){
            Thread.sleep(10_000);
            return getGame(--retry);
        }
        return gameRound;
    }
    private static boolean doBet(LPlayerDao dao) throws EncodingException, ApiException, InterruptedException {
        long betValue = getBetValue();

        Long balance = checkBalance(dao, betValue);
        dao.setAlb_amt(balance - betValue);

        String betNum = String.format("%05d", (int) (Math.random() * 100000));
        String tmpStr = dao.getAddress() + "_" + System.currentTimeMillis() + "_" + betNum + salt;
        String betId = ByteArray.toHexString(Hash.sha3(tmpStr.getBytes()));

        List<Object> params = new LinkedList<Object>(){ {
            add(betId);
            add(betNum);
        }};

        byte[] input = AbiUtil.parseMethod("doBet(bytes32,uint256)", params, false);
        String txId = cli.triggerContract(LottoContract, betValue, input, 30000000, 0, null);
        if (txId == null){
            throw new ApiException(StatusCode.SCPT_PAY_ERR);
        }

        Optional<TransactionInfo> result = AutoClient.getTransactionInfoById(txId);
        // 如果以后起多个进程, 可以采用 select * from l_account where mod(id, 2) = 1; 解决多进程冲突的问题
        if (result.isPresent() && result.get().getResult().equals(TransactionInfo.code.SUCESS)) {
            int rand =  (int) (Math.random() * 100) ;
            long roundId;
            int betCnt5s = dao.getBet_5s_cnt();
            int betCnt5m = dao.getBet_5m_cnt();
            long gr_id = dao.getLast_gr_id();

            if (rand <= 90){
                roundId = BetService.doBet5Sec(dao.getU_id(), betNum, txId, betId, betValue);
                ++betCnt5s;
            }else {
                GameRoundDao gameRound = getGame(3);
                if (gameRound == null){
                    throw new ApiException(StatusCode.BET_FAILED);
                }

                gr_id = gameRound.getR_id();
                roundId = BetService.doBet5Min(dao.getU_id(), betNum, txId, betId, betValue, gr_id);
                ++betCnt5m;
            }

            if (roundId <= 0){
                throw new ApiException(StatusCode.BET_FAILED);
            }

            LPlayerDao update = new LPlayerDao();
            update.setId(dao.getId())
                .setU_id(dao.getU_id())
                .setAlb_amt(dao.getAlb_amt())
                .setBet_amt(dao.getBet_amt() + betValue)
                .setBet_5m_cnt(betCnt5m)
                .setBet_5s_cnt(betCnt5s)
                .setLast_gr_id(gr_id)
                .setLb_t(new Timestamp(System.currentTimeMillis()));
            update.saveObj(true);
            return true;
        }
        return false;
    }

    private static void createAccounts(int count) throws CipherException, ApiException {
        while (count-- > 0){
            WalletFile walletFile = cli.generateAddress(playerPswd);

            LPlayerDao dao = new LPlayerDao();
            dao.setAddress(walletFile.getAddress())
                .setCrypto(JSON.toJSONString(walletFile.getCrypto()))
                .setVersion(walletFile.getVersion())
                .setUdid(walletFile.getId())
                .setState(1)
                .setLb_t(new Timestamp(0));
            dao.saveObj(false);
        }
    }

    private static void doAllBets(int betCnt) throws ApiException, CipherException {

        Map<String, Object> conds = new HashMap<>();
        conds.put("lb_t < ", new Timestamp(System.currentTimeMillis() - 360_000));
        conds.put("state = ", 1);
        LPlayerDao dao = null;


        while (true){
            LPlayerDao update;
            try{
                dao = /*LPlayerDao.getById(107);*/LPlayerDao.getRandom(conds);
                if (dao == null) {
                    logger.info("user not exist");
                    break;
                }
                cli.loadWalletDao(dao, playerPswd);

                if (dao.getU_id() <= 0){
                    UserDao userDao = UserDao.getByAddress(dao.getAddress());
                    if (userDao == null) {
                        userDao = new UserDao();
                        userDao.setAddress(dao.getAddress())
                            .setLucky_h(2000)
                            .setLucky_m(30000)
                            .setLucky_l(50000)
                            .setState(1)
                            .setRtype(1); // 标识刷单
                        dao.setU_id(userDao.saveObj(true));
                    }else{
                        dao.setU_id(userDao.getId());
                    }
                }

                int gameCount = (int) (Math.random() * 2) + 2;
                for (int i = 0; i < gameCount; i++) {
                    if (!doBet(dao)){
                        logger.warn("bet failed: " + dao.getAddress());
                        break;
                    }
                    logger.info("bet: " + dao.getAddress());
                    if (--betCnt <= 0){
                        break;
                    }
                    Thread.sleep((int)(Math.random() * 120_000) + 10_000);
                }
                if (betCnt <= 0){
                    break;
                }
            }catch (ApiException | EncodingException | IOException | CipherException | InterruptedException e){
                logger.error("", e);
                if (dao != null) {
                    update = new LPlayerDao();
                    update.setId(dao.getId()).setState(2).setU_id(dao.getU_id()).setAlb_amt(dao.getAlb_amt());
                    update.saveObj(true);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
//            createAccounts(100);
            doAllBets(450);
        } catch (ApiException | CipherException e) {
            logger.error("main exist", e);
        }
    }
}

