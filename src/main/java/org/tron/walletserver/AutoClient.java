package org.tron.walletserver;

import com.demo.dao.LAcntDao;
import com.demo.nettyrest.exception.ApiException;
import com.demo.nettyrest.exception.StatusCode;
import com.google.protobuf.ByteString;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.tron.api.GrpcAPI.*;
import org.tron.common.crypto.ECKey;
import org.tron.common.crypto.Sha256Hash;
import org.tron.common.utils.*;
import org.tron.core.config.Configuration;
import org.tron.core.config.Parameter.CommonConstant;
import org.tron.core.exception.CipherException;
import org.tron.core.exception.EncodingException;
import org.tron.keystore.*;
import org.tron.protos.Contract;
import org.tron.protos.Protocol.*;
import org.tron.protos.Protocol.Transaction.Result;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


public class AutoClient {

    private static final Logger logger = LoggerFactory.getLogger("WalletApi");
    private static final String FilePath = "Wallet";
    private ECKey ecKey = null;
    private byte[] address = null;
    private static byte addressPreFixByte = CommonConstant.ADD_PRE_FIX_BYTE_TESTNET;
    private static int rpcVersion = 0;

    private static GrpcClient rpcCli = init();

    public static GrpcClient init() {
        Config config = Configuration.getByPath("config.conf");

        String fullNode = "";
        String solidityNode = "";
        if (config.hasPath("soliditynode.ip.list")) {
            solidityNode = config.getStringList("soliditynode.ip.list").get(0);
        }
        if (config.hasPath("fullnode.ip.list")) {
            fullNode = config.getStringList("fullnode.ip.list").get(0);
        }
        if (config.hasPath("net.type") && "mainnet".equalsIgnoreCase(config.getString("net.type"))) {
            AutoClient.addressPreFixByte = CommonConstant.ADD_PRE_FIX_BYTE_MAINNET;
        } else {
            AutoClient.addressPreFixByte = CommonConstant.ADD_PRE_FIX_BYTE_MAINNET;
        }
        if (config.hasPath("RPC_version")) {
            rpcVersion = config.getInt("RPC_version");
        }
        return new GrpcClient(fullNode, solidityNode);
    }

    public static int getRpcVersion() {
        return rpcVersion;
    }

    public void loadWalletDao(String pswd, Integer id) throws CipherException, IOException, ApiException {

        LAcntDao dao = LAcntDao.getById(id);
        if (dao == null) {
            throw new ApiException(StatusCode.ADDRESS_EMPTY);
        }

        WalletFile walletFile = WalletUtils.loadWalletDao(dao);
        this.address = decodeFromBase58Check(walletFile.getAddress());
        this.ecKey = Wallet.decrypt(pswd.getBytes(), walletFile);
    }

    public void loadWalletFile(int index, String pswd) throws CipherException, IOException{
        byte[] password = pswd.getBytes();
        File file = new File(FilePath);
        if (!file.exists() || !file.isDirectory()) {
            throw new IOException("No keystore file found, please use registerwallet or importwallet first!");
        }

        File[] wallets = file.listFiles();
        File wallet =  (wallets != null && wallets.length > index)? wallets[index]: null;

        if (wallet == null) {
            logger.warn("wallet is empty");
        }
        WalletFile walletFile = WalletUtils.loadWalletFile(wallet);
        this.address = decodeFromBase58Check(walletFile.getAddress());
        this.ecKey = Wallet.decrypt(password, walletFile);
    }

    public static byte[] decodeFromBase58Check(String addressBase58) {
        byte[] address = null;
        byte[] decodeCheck = Base58.decode(addressBase58);
        if (decodeCheck.length > 4) {
            byte[] decodeData = new byte[decodeCheck.length - 4];
            System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
            byte[] hash1 = Sha256Hash.hashTwice(decodeData);
            if (hash1[0] == decodeCheck[decodeData.length] &&
                    hash1[1] == decodeCheck[decodeData.length + 1] &&
                    hash1[2] == decodeCheck[decodeData.length + 2] &&
                    hash1[3] == decodeCheck[decodeData.length + 3]) {
                address = decodeData;
            }
        }
        return (address != null && address.length == CommonConstant.ADDRESS_SIZE && address[0] == WalletApi.getAddressPreFixByte()) ? address : null;
    }

    private String processTransactionExtention(TransactionExtention transactionExtention){
        if (transactionExtention == null) {
            return null;
        }
        Return ret = transactionExtention.getResult();
        if (!ret.getResult()) {
            logger.warn("Code = " + ret.getCode() + "\n" + ret.getMessage().toStringUtf8());
            return null;
        }
        Transaction transaction = transactionExtention.getTransaction();
        if (transaction == null || transaction.getRawData().getContractCount() == 0) {
            logger.warn("Transaction is empty");
            return null;
        }
        if (transaction.getRawData().getTimestamp() == 0) {
            transaction = TransactionUtils.setTimestamp(transaction);
        }
        transaction = TransactionUtils.sign(transaction, this.ecKey);
        String txId = ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray()));
        logger.info("Receive txid = " + txId);
        return rpcCli.broadcastTransaction(transaction)? txId : null;
    }

    public String triggerContract(String contractAddr, long callValue, byte[] data, long feeLimit, long tokenValue, String tokenId) {

        Contract.TriggerSmartContract.Builder builder = Contract.TriggerSmartContract.newBuilder();
        builder.setOwnerAddress(ByteString.copyFrom(address));
        builder.setContractAddress(ByteString.copyFrom(decodeFromBase58Check(contractAddr)));
        builder.setData(ByteString.copyFrom(data));
        builder.setCallValue(callValue);
        if (tokenId != null && !tokenId.equals("")) {
            builder.setCallTokenValue(tokenValue);
            builder.setTokenId(Long.parseLong(tokenId));
        }
        Contract.TriggerSmartContract triggerContract = builder.build();
        TransactionExtention transactionExtention = rpcCli.triggerContract(triggerContract);
        if (transactionExtention == null || !transactionExtention.getResult().getResult()) {
            logger.warn("RPC create call trx failed! \n" + transactionExtention.getResult().getCode() + "\n" + transactionExtention.getResult().getMessage().toStringUtf8());
            return null;
        }

        Transaction transaction = transactionExtention.getTransaction();
        if (transaction.getRetCount() != 0 &&
                transactionExtention.getConstantResult(0) != null &&
                transactionExtention.getResult() != null) {
            byte[] result = transactionExtention.getConstantResult(0).toByteArray();
            logger.info("message:" + transaction.getRet(0).getRet() + "\n" + ByteArray.toStr(transactionExtention.getResult().getMessage().toByteArray()));
            logger.info("Result:" + Hex.toHexString(result));
            return ByteArray.toHexString(transactionExtention.getTxid().toByteArray());
        }

        TransactionExtention.Builder texBuilder = TransactionExtention.newBuilder();
        Transaction.Builder transBuilder = Transaction.newBuilder();
        transBuilder.setRawData(transactionExtention.getTransaction().getRawData().toBuilder().setFeeLimit(feeLimit));
        for (int i = 0; i < transactionExtention.getTransaction().getSignatureCount(); i++) {
            ByteString s = transactionExtention.getTransaction().getSignature(i);
            transBuilder.setSignature(i, s);
        }
        for (int i = 0; i < transactionExtention.getTransaction().getRetCount(); i++) {
            Result r = transactionExtention.getTransaction().getRet(i);
            transBuilder.setRet(i, r);
        }
        texBuilder.setTransaction(transBuilder);
        texBuilder.setResult(transactionExtention.getResult());
        texBuilder.setTxid(transactionExtention.getTxid());
        transactionExtention = texBuilder.build();

        return processTransactionExtention(transactionExtention);
    }

    public static Optional<TransactionInfo> getTransactionInfoById(String txID) {
        return rpcCli.getTransactionInfoById(txID);
    }

    public static void main(String[] args) {
        AutoClient cli = new AutoClient();
        try {
//            cli.loadWalletFile(0, "Star@2018");
            cli.loadWalletDao("Star@2018", 1);
            byte[] input = Hex.decode(AbiUtil.parseMethod("ca(address,uint16)", "\"TMfnsYJJfkNCUhWEcT25aG6uEbToQfoyXG\",20", false));
            String txId = cli.triggerContract("TC1tzxXDV63YJXsd4ho5kVL6APhRg533Wz", 0, input, 1000000000, 0, null);

            Optional<TransactionInfo> result = cli.getTransactionInfoById(txId);
            if (result.isPresent()) {
                TransactionInfo transactionInfo = result.get();
                logger.info(Utils.printTransactionInfo(transactionInfo));
            } else {
                logger.info("getTransactionInfoById " + " failed !!");
            }
        }catch (EncodingException | IOException | CipherException | ApiException e){
            System.out.println(e);
        }
    }
}
