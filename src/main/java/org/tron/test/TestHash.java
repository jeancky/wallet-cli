package org.tron.test;

import com.tronyes.demo.utils.LuckyUtil;

import java.math.BigInteger;

public class TestHash {
  public static void main(String[] args){
    String blockHash = "68fb94e37aa9636fd449dda75be035bbb426de573334c543011f";
    String lotHash = LuckyUtil.getDiceHashByBlock(blockHash);
    BigInteger bi = new BigInteger(lotHash, 16);
    int lotNumber = bi.mod(new BigInteger("6")).intValue() + 1;
    System.out.println(lotNumber);
  }
}
