package org.tron.script;

import com.tronyes.demo.dao.BlockDataDao;
import com.tronyes.demo.dao.LAcntDao;
import com.tronyes.demo.dao.UserRoundDao;
import com.tronyes.demo.utils.LuckyUtil;
import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.exception.StatusCode;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@DisallowConcurrentExecution
public class OpenBlockTask implements org.quartz.Job {
    private static final Logger logger = LoggerFactory.getLogger(AwdJob.class);
    private static final AutoClient cli = new AutoClient();
    static final String SIX_CONTRACT_ADDRESS = "TTyFQSQfJh4ZUM9hJLhh8QppjtYMQ3BSAi";

    private static String shortHex(byte[] string) {
        String hexValue = Hex.toHexString(string).toUpperCase();
        return hexValue.replaceFirst("^0+(?!$)", "");
    }

    private static void openBlocks() {
        try {
            BlockDataDao block = BlockDataDao.getBlockWithEmptyHash();
            if (block != null) {
                String params = String.format("%d", block.getB_h());
                byte[] input = Hex.decode(AbiUtil.parseMethod("getBlockHash(uint256)", params, false));
                GrpcAPI.TransactionExtention transactionExtention = (GrpcAPI.TransactionExtention) cli.triggerContract(SIX_CONTRACT_ADDRESS, 0, input, 20000000, 0, null);
                Protocol.Transaction transaction = transactionExtention.getTransaction();
                byte[] result = transactionExtention.getConstantResult(0).toByteArray();

                if ("SUCESS".equals("" + transaction.getRet(0).getRet())) {
                    if ("0".equals(shortHex(result))) {
                        block.setState(-1);
                    } else {
                        String strResult = Hex.toHexString(result);
                        block.setBlock_hash(strResult);

                        int lotNumber = LuckyUtil.getDiceNumberByBlock(strResult);

                        block.setSix_number(lotNumber);
                        block.saveObj(true);

                        Map<String, Object> conds = new HashMap<>();
                        conds.put("type = ", 3);
                        conds.put("gr_id = ", block.getB_h());
                        List<UserRoundDao> list = UserRoundDao.getListByCond(conds, " id DESC ", null);

                        for (UserRoundDao userRound : list) {
                            userRound.setLot_num(lotNumber + "");
                            LuckyUtil.LotteryResult lotRet = LuckyUtil.lotteryDice(userRound.getBet_num(), lotNumber + "");
                            userRound.setLot_type(lotRet.getResult());
                            if (lotRet.getResult() > 0 && lotRet.getResult() < 6) {
                                userRound.setRwd_state(1);
                                userRound.setLot_val((long)(userRound.getBet_val() * LuckyUtil.lotteryDiceOdds(lotRet.getResult())));
                            }
                            userRound.saveObj(true);
                        }
                    }
                }
            }
        } catch (ApiException | EncodingException e) {
            logger.warn(e.getMessage());
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
        }catch (CipherException | IOException | ApiException e){
            logger.warn("exit with " + e.toString());
        }
    }
}
