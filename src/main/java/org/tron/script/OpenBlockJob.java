package org.tron.script;

import com.tronyes.demo.dao.BlockDataDao;
import com.tronyes.demo.dao.LAcntDao;
import com.tronyes.demo.dao.UserRoundDao;
import com.tronyes.demo.utils.LuckyUtil;
import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.exception.StatusCode;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.tron.api.GrpcAPI;
import org.tron.common.utils.AbiUtil;
import org.tron.core.exception.CipherException;
import org.tron.core.exception.EncodingException;
import org.tron.protos.Protocol;
import org.tron.walletserver.AutoClient;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@DisallowConcurrentExecution
public class OpenBlockJob implements org.quartz.Job {
    private static final Logger logger = LoggerFactory.getLogger(AwdJob.class);
    private static final AutoClient cli = new AutoClient();
    static final String SIX_CONTRACT_ADDRESS = "TAJPM372tcjzGbCfGSSa3rYbaCMzJiqjPR";

    private static String shortHex(byte[] string) {
        String hexValue = Hex.toHexString(string).toUpperCase();
        return hexValue.replaceFirst("^0+(?!$)", "");
    }

    public static JobDetail getJobDetail(String jobName, String groupName) {
        return newJob(OpenBlockJob.class)
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

    private static void openBlocks() {
        while (true){
            try {
                BlockDataDao block = BlockDataDao.getBlockWithEmptyHash();
                if (block == null) {
                    break;
                }
                String params = String.format("%d", block.getB_h());
                byte[] input = Hex.decode(AbiUtil.parseMethod("getBlockHash(uint256)", params, false));
                GrpcAPI.TransactionExtention transactionExtention = (GrpcAPI.TransactionExtention) cli.triggerContract(SIX_CONTRACT_ADDRESS, 0, input, 20000000, 0, null);
                Protocol.Transaction transaction = transactionExtention.getTransaction();
                byte[] result = transactionExtention.getConstantResult(0).toByteArray();

                if ("SUCESS".equals("" + transaction.getRet(0).getRet())) {
                    if ("0".equals(shortHex(result))) {
                        block.setState(-1);
                    } else {
                        String blockHash = shortHex(result);
                        block.setBlock_hash(blockHash);

                        String lotHash = LuckyUtil.getDiceHashByBlock(blockHash);
                        BigInteger bi = new BigInteger(lotHash, 16);
                        int lotNumber = bi.mod(new BigInteger("6")).intValue() + 1;

                        block.setSix_number(lotNumber);
                        block.setLot_hash(lotHash);
                        block.saveObj(true);

                        Map<String, Object> conds = new HashMap<>();
                        conds.put("type = ", 3);
                        conds.put("gr_id = ", block.getB_h());
                        List<UserRoundDao> list = UserRoundDao.getListByCond(conds, " id DESC ", null);

                        for (UserRoundDao userRound : list) {
                            userRound.setLot_num(lotNumber + "");
                            userRound.setBlock_hash(blockHash);
                            userRound.setLot_hash(lotHash);
                            LuckyUtil.LotteryResult lotRet = LuckyUtil.lotteryDice(userRound.getBet_num(), lotNumber + "");
                            userRound.setLot_type(lotRet.getResult());
                            if (lotRet.getResult() > 0 && lotRet.getResult() < 6) {
                                userRound.setRwd_state(1);
                                userRound.setLot_val((long)(userRound.getBet_val() * LuckyUtil.lotteryDiceOdds(lotRet.getResult())));
                            }
                            userRound.setBet_state(10);
                            userRound.saveObj(true);
                        }
                    }
                }
            } catch (ApiException | EncodingException e) {
                logger.warn(e.getMessage());
            }
        }
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
            openBlocks();
        }catch (CipherException | IOException | ApiException e){
            logger.warn("exit with " + e.toString());
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            loadWallet(6,"Star@2018");
            openBlocks();
        }catch (Exception e){
            logger.warn("exit with " + e.toString());
        }
    }
}
