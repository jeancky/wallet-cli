package com.tronyes.demo.utils;

import com.tronyes.demo.dao.UserRoundDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LuckyUtil {

    public static class LotteryResult {
        private String lottery; // 中奖号码
        private Integer result; // 中奖类型，0. 未中奖，1.2.3等奖

        public String getLottery() {
            return lottery;
        }

        public Integer getRewardState() {
            return result > 0 ? 1: 0;
        }

        public void setLottery(String lottery) {
            this.lottery = lottery;
        }

        public Integer getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }
    }


    private static final Logger logger = LoggerFactory.getLogger(LuckyUtil.class);

    // 实际赔率需要除以10，为了防止浮点精度问题
    private static final int HIGH_RATE = 120000;
    private static final int MIDDLE_RATE = 1200;
    private static final int LOW_RATE = 12;

    private static final int HIGH_5S_RATE = 48;
    private static final int MIDDLE_5S_RATE = 24;
    private static final int LOW_5S_RATE = 12;

    public static int getLotteryRatio(int type) {
        switch (type) {
            case 1:
                return LuckyUtil.HIGH_RATE;
            case 2:
                return LuckyUtil.MIDDLE_RATE;
            case 3:
                return LuckyUtil.LOW_RATE;
            default:
                return 0;
        }
    }

    public static int get5sLotteryRatio(int type) {
        switch (type) {
            case 1:
                return LuckyUtil.HIGH_5S_RATE;
            case 2:
                return LuckyUtil.MIDDLE_5S_RATE;
            case 3:
                return LuckyUtil.LOW_5S_RATE;
            default:
                return 0;
        }
    }

    public static LotteryResult lottery5MinOnePerson(String bet, long amount) {
        long lucky = (long) (Math.random() * 100000);
        LotteryResult result = new LotteryResult();
        long l = 70000;
        if (lucky > 0 && lucky <= l) {
            result.setLottery(generateLow(bet));
            result.setResult(3);
        } else {
            result.setLottery(generateNone(bet));
            result.setResult(0);
        }

        return result;
    }

    public static LotteryResult lottery(long h, long m, long l, String bet) {
        // 根据lucky值开奖
        LotteryResult result = new LotteryResult();
        long lucky = (long) (Math.random() * 100000);

//        h = Math.min(5000, Math.max(0, h));
//        m = Math.min(20000, Math.max(0, m));
//        l = Math.min(50000, Math.max(0, l));

        if (lucky > 0 && lucky <= h) {
            result.setLottery(generate5sHigh(bet));
            result.setResult(1);
        } else if (lucky > 0 && lucky <= m) {
            result.setLottery(generate5sMiddle(bet));
            result.setResult(2);
        } else if (lucky > 0 && lucky <= l) {
            result.setLottery(generate5sLow(bet));
            result.setResult(3);
        } else {
            result.setLottery(generate5sNone(bet));
            result.setResult(0);
        }
        logger.info("bet 5s: " + result.lottery + ":" + bet + " : " + result.result);
        return result;
    }

    // 根据投注生成五分钟一等奖
    public static String generateHigh(String bet) {
        return bet;
    }

    // 根据投注生成五秒钟一等奖
    public static String generate5sHigh(String bet) {
        // 第一位中
        String bit1 = bet.substring(0, 1);
        // 剩下四位随机
        String bitRest = String.format("%04d", (int)(Math.random() * 10000));
        return bit1 + bitRest;
    }

    // 根据投注生成二等奖
    public static String generateMiddle(String bet) {
        // 后两位中
        String last = bet.substring(3);
        String bit1 = notTheSame(bet, 0);
        String bit2 = notTheSame(bet, 1);
        String bit3 = notTheSame(bet, 2);
        return bit1 + bit2 + bit3 + last;
    }

    // 根据投注生成五秒钟二等奖
    public static String generate5sMiddle(String bet) {
        // 第一位不中
        String bit1 = notTheSame(bet, 0);
        // 第二位或者第三位中
        String bit2 = "";
        String bit3 = "";
        String randomString = String.format("%01d", (int)(Math.random() * 10));
        if (Math.random() < 0.5) {
            bit2 = bet.substring(1, 2);
            bit3 = randomString;
        } else {
            bit2 = randomString;
            bit3 = bet.substring(2, 3);
        }

        // 最后两位随机
        String rest = String.format("%02d", (int)(Math.random() * 100));
        return bit1 + bit2 + bit3 + rest;
    }

    // 根据投注生成五秒钟三等奖
    public static String generate5sLow(String bet) {
        return generateLow(bet);
    }

    // 根据投注生成三等奖
    public static String generateLow(String bet) {
        if (Math.random() < 0.5) {
            // 最后位奇偶符合
            String bit1 = notTheSame(bet, 0);
            String bit2 = notTheSame(bet, 1);
            String bit3 = notTheSame(bet, 2);
            String bit4 = notTheSame(bet, 3);
            String bit5 = getPropertyBit(bet, 4, true);
            return bit1 + bit2 + bit3 + bit4 + bit5;
        } else {
            // 倒数第二位奇偶符合
            String bit1 = notTheSame(bet, 0);
            String bit2 = notTheSame(bet, 1);
            String bit3 = notTheSame(bet, 2);
            String bit4 = getPropertyBit(bet, 3, true);
            String bit5 = notTheSame(bet, 4);
            return bit1 + bit2 + bit3 + bit4 + bit5;
        }
    }

    // 根据投注生成不中奖
    public static String generateNone(String bet) {
        String bit1 = notTheSame(bet, 0);
        String bit2 = notTheSame(bet, 1);
        String bit3 = notTheSame(bet, 2);
        String bit4 = getPropertyBit(bet, 3, false);
        String bit5 = getPropertyBit(bet, 4, false);
        return bit1 + bit2 + bit3 + bit4 + bit5;
    }

    // 根据投注生成五秒钟不中奖
    public static String generate5sNone(String bet) {
        return generateNone(bet);
    }

    // 生成和指定位数数字相同或不同奇偶属性
    public static String getPropertyBit(String number, int n, boolean same) {
        int bit = Integer.parseInt(number.substring(n, n + 1));

        int[] odd = {1, 3, 5, 7, 9};
        int[] even = {0, 2, 4, 6, 8};

        int randomIndex = (int)(Math.random() * 5);
        if (bit % 2 == 0) {
            return (same ? even[randomIndex] : odd[randomIndex]) + "";
        } else {
            return (same ? odd[randomIndex] : even[randomIndex]) + "";
        }
    }

    // 生成一个不是指定数第n位的数
    private static String notTheSame(String number, int n) {
        return (Integer.parseInt(number.substring(n, n + 1)) + ((int) (Math.random() * 10) % 9 + 1)) % 10 + "";
    }

    // 抽奖对应的出/入
    public static double lottery(List<UserRoundDao> list, String luckyNumber) {
        long sum = 0;
        long betSum = 0;
        for (UserRoundDao item : list) {
            int type = lotteryType(item.getBet_num(), luckyNumber);
            Long amount = item.getBet_val();
            betSum += amount;
            switch (type) {
                case 1:
                    sum += amount * HIGH_RATE / 10;
                    break;
                case 2:
                    sum += amount * MIDDLE_RATE / 10;
                    break;
                case 3:
                    sum += amount * LOW_RATE / 10;
                    break;
                default:
                    break;
            }
        }

        return ((double)(sum))  / ((double)(betSum));
    }

    // 判断几等奖
    public static int lotteryType(String number, String lucky) {
        char[] array1 = number.toCharArray();
        char[] array2 = lucky.toCharArray();

        if (number.equals(lucky)) {
            return 1;
        }

        if (array1[3] == array2[3] && array1[4] == array2[4]) {
            return 2;
        }

        int flag1 = Integer.parseInt(array1[3] + "") % 2;
        int flag2 = Integer.parseInt(array2[3] + "") % 2;
        int flag3 = Integer.parseInt(array1[4] + "") % 2;
        int flag4 = Integer.parseInt(array2[4] + "") % 2;

        if (flag1 == flag2 || flag3 == flag4) {
            return 3;
        }

        return 0;
    }

    // 判断是5秒钟几等奖
    public static int lottery5sType(String number, String lucky) {
        char[] array1 = number.toCharArray();
        char[] array2 = lucky.toCharArray();

        if (array1[0] == array2[0]) {
            return 1;
        }

        if (array1[1] == array2[1] || array1[2] == array2[2]) {
            return 2;
        }

        if (lotteryType(number, lucky) == 3) {
            return 3;
        }

        return 0;
    }
}
