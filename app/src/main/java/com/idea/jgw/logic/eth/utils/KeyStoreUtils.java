package com.idea.jgw.logic.eth.utils;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idea.jgw.App;
import com.idea.jgw.logic.ic.EncryptUtils;
import com.idea.jgw.utils.common.MyLog;
import com.xdja.SafeKey.JNIAPI;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by pc on 2018/1/25.
 */

public class KeyStoreUtils {
    public static final String DEFAULTKEY = "DEFAULT";
//    public static final String KEYSTORE_PATH = App.getInstance().getFilesDir().getPath() + "/keystore";

    /**
     * 在内置存储生成keystore方便选择
     * @param ecKeyPair
     * @return
     */
    public static String genKeyStore2Files(ECKeyPair ecKeyPair) {


        try {
            File file =getKeyStorePathFile();
            String s = WalletUtils.generateWalletFile(DEFAULTKEY, ecKeyPair, file, false);
            Log.e("gen",s);
            return s;

        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Credentials getCredentials(String pwd,String tagetAddress) throws FileNotFoundException {

        File keystorePath = new File(App.getInstance().getFilesDir(), "");

        if(tagetAddress.startsWith("0x")){
            tagetAddress = tagetAddress.substring(2);
        }

        File[] files = keystorePath.listFiles();
        for (File file : files) {
            String name = file.getName();
            String address = name.substring(name.lastIndexOf("--") + 2, name.lastIndexOf("."));
            if (tagetAddress.equals(address)) {
                ObjectMapper mapper = new ObjectMapper();

                try {
                    WalletFile walletFile = mapper.readValue(file, WalletFile.class);
                    return Credentials.create(Wallet.decrypt(pwd, walletFile));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        throw new FileNotFoundException("not found keystore(找不到keystore文件)");
    }

    public static String signedTransactionData(String pwd,String from, String to, String nonce, String gasPrice, String gasLimit, String value) throws FileNotFoundException {
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                new BigInteger(nonce),
                new BigInteger(gasPrice),
                new BigInteger(gasLimit),
                to,
                new BigDecimal(value).multiply(new BigDecimal(10).pow(18)).toBigInteger());

        Credentials credentials = KeyStoreUtils.getCredentials(pwd,from);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        return Numeric.toHexString(signedMessage);

    }

    public static String signedTransactionData(String to, String nonce, String gasPrice, String gasLimit, String value, String pubKey) throws Exception {
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                new BigInteger(nonce),
                new BigInteger(gasPrice),
                new BigInteger(gasLimit),
                to,
                new BigDecimal(value).multiply(new BigDecimal(10).pow(18)).toBigInteger());
        byte[] encode = TransactionEncoder.encode(rawTransaction);
        byte[] messageHash = Hash.sha3(encode);
//        byte[] signedMessage = sign(messageHash);
        byte[] signedMessage = EncryptUtils.sign(messageHash);

        signedMessage = processAfterSign(pubKey, rawTransaction, messageHash, signedMessage);

        return Numeric.toHexString(signedMessage);

    }

    private static byte[] processAfterSign(String pubKey, RawTransaction rawTransaction, byte[] messageHash, byte[] signedMessage) throws Exception {
        //将IC签名后的16进制串分成2个32位,分别给r和s
        byte[] bytes1 = EncryptUtils.subBytes(signedMessage, 0, 32);
        byte[] bytes2 = EncryptUtils.subBytes(signedMessage, 32, 32);
        BigInteger rr= Numeric.toBigInt(bytes1);
        BigInteger ss=Numeric.toBigInt(bytes2);

        ECDSASignature sig=new ECDSASignature(rr, ss).toCanonicalised();

        //签名后处理,需要用到公钥匙
        BigInteger publicKey = new BigInteger(pubKey);
        System.out.println(Numeric.toHexString(publicKey.toByteArray()));
        int recId = -1;
        for (int i = 0; i < 4; i++) {
            try {
                Class clazz = Class.forName("org.web3j.crypto.Sign");
                Method method = clazz.getDeclaredMethod("recoverFromSignature", int.class, ECDSASignature.class, byte[].class);
                method.setAccessible(true);
                BigInteger k = (BigInteger) method.invoke(null, i, sig, messageHash);
                if (k != null && k.equals(publicKey)) {
                    recId = i;
                    break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (recId == -1) {
            Log.e("TEmpActivity", "Could not construct a recoverable key. This should never happen.");
            throw new RuntimeException(
                    "Could not construct a recoverable key. This should never happen.");
        }

        int headerByte = recId + 27;

        // 1 header + 32 bytes for R + 32 bytes for S
        byte v = (byte) headerByte;
        byte[] r = Numeric.toBytesPadded(sig.r, 32);
        byte[] s = Numeric.toBytesPadded(sig.s, 32);

        Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);
        //----签名结束
        return com.idea.jgw.test.TransactionEncoder.encode(rawTransaction, signatureData);
    }

    public static File getKeyStorePathFile() {
        File file =  new File(App.getInstance().getFilesDir(), "");
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.e("files", file.getAbsolutePath());
        return file;
    }
}
