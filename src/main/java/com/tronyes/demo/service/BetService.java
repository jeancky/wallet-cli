package com.tronyes.demo.service;

import com.tronyes.demo.dao.GameRoundDao;
import com.tronyes.demo.dao.SystemDao;
import com.tronyes.demo.dao.UserDao;
import com.tronyes.demo.dao.UserRoundDao;
import com.tronyes.demo.utils.LuckyUtil;
import com.tronyes.demo.utils.LuckyUtil.LotteryResult;
import com.tronyes.nettyrest.exception.ApiException;
import com.tronyes.nettyrest.exception.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.script.RobJob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BetService {
    private static final Logger logger = LoggerFactory.getLogger(RobJob.class);

    // 立即彩下注
    public static Long doBet5Sec(long uid, String bet, String txId, String betId, long amount) throws ApiException {
        UserDao user = UserDao.getById(uid);
        if (user == null) {
            throw new ApiException(StatusCode.USER_NOT_EXIST);
        }

//        LotteryResult lotteryResult = LuckyUtil.lottery(user.getLucky_h(), user.getLucky_m(), user.getLucky_l(), bet);
        LotteryResult lotteryResult = LuckyUtil.lottery(20000, 40000, 70000, bet);
        Integer lotteryType = lotteryResult.getResult();

        long totalValue = ((LuckyUtil.get5sLotteryRatio(lotteryType) * amount) / 10);

        UserRoundDao uRound = new UserRoundDao();
        uRound.setU_id(user.getId())
                .setAddress(user.getAddress())
                .setType(2)
                .setRtype(1) // 标识刷单
                .setBet_state(10)
                .setBet_id(betId)
                .setBet_val(amount)
                .setBet_num(bet)
                .setBet_tx(txId)
                .setLot_type(lotteryType)
                .setLot_val(totalValue)
                .setLot_num(lotteryResult.getLottery())
                .setRwd_state(lotteryResult.getRewardState());

        Long id = uRound.saveObj(true);
        if (id <= 0){
            throw new ApiException(StatusCode.BET_FAILED);
        }

        // update userBet info
        user.recordBet(amount);

        // update system info
        Integer key = 1;
        try {
            long systemVal = SystemDao.getByKey(key).val_int;
            SystemDao.saveByKey(key, systemVal + totalValue);
        } catch (ApiException e) {
            logger.warn(e.getLocalizedMessage());
        }

        return id;
    }

    // 实时彩下注
    public static Long doBet5Min(long uid, String bet, String txId, String betId, long amount, long roundId) throws ApiException {
        UserDao user = UserDao.getById(uid);
        if (user == null) {
            throw new ApiException(StatusCode.GAME_NOT_EXIST);
        }

        GameRoundDao game = GameRoundDao.get(roundId);
        if (game == null) {
            throw new ApiException(StatusCode.GAME_NOT_EXIST);
        }

        if (game.getState() != 1) {
            throw new ApiException(StatusCode.GAME_CLOSED);
        }

        List<UserRoundDao> list = UserRoundDao.getUserGameRoundList(uid, roundId);
        if (list != null && list.size() >= 5) {
            throw new ApiException(StatusCode.GAME_MORE_5);
        }

        UserRoundDao uRound = new UserRoundDao();
        uRound.setGr_id(roundId)
                .setU_id(user.getId())
                .setAddress(user.getAddress())
                .setType(1)
                .setRtype(1) // 标识刷单
                .setBet_state(1)
                .setBet_id(betId)
                .setBet_val(amount)
                .setBet_num(bet)
                .setBet_tx(txId);
        if (uRound.saveObj(true) <= 0){
            throw new ApiException(StatusCode.BET_FAILED);
        }

        user.recordBet(amount);
        game.recordBet(amount);
        return roundId;
    }

}
