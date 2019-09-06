package com.idea.jgw.logic.eth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.idea.jgw.App;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.eth.data.FullWallet;
import com.idea.jgw.logic.eth.interfaces.StorableWallet;
import com.idea.jgw.logic.eth.network.EtherscanAPI;
import com.idea.jgw.logic.eth.service.TransactionService;
import com.idea.jgw.logic.eth.utils.AddressNameConverter;
import com.idea.jgw.logic.eth.utils.KeyStoreUtils;
import com.idea.jgw.logic.eth.utils.MyMnemonicUtils;
import com.idea.jgw.logic.eth.utils.SecureRandomUtils;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.logic.ic.EncryptUtils;
import com.idea.jgw.service.GetSendStatusService;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MyLog;

import org.bitcoinj.core.Base58;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;

/**
 * 以太钱包工具类
 * （总入口  createWallet()方法）
 * Created by vam on 2018\6\1 0001.
 */

public class EthWalltUtils extends WalletUtils {

    /**
     * 发送币(上层需自己判断金额够不 getCurAvailable（....）)
     *
     * @param fromAddress    从那里转出
     * @param toAddress      转给谁
     * @param password       密码
     * @param sendAmountGwei 转账金额（单位gwei）
     * @param gasPrice       手续费（单位gwei）
     * @param gasLimit       最大限制（单位gwei）
     */
    public static void sendCoin(Activity ac, final String fromAddress, final String toAddress, final String password, final String sendAmountGwei, final long gasPrice, final int gasLimit, final TLCallback callback) throws Exception {


        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 0) {
                    callback.onSuccess(msg.obj);
                } else {
                    callback.onFail(-1, null);
                }
            }
        };


        EtherscanAPI.getInstance().getNonceForAddress(fromAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                MyLog.e("sendCoin ----onFailure");
                handler.sendEmptyMessage(-1);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Web3j web3j = Web3jFactory.build(new HttpService(Common.Eth.URL));
                        try {
                            JSONObject o = new JSONObject(response.body().string());
                            BigInteger nonce = new BigInteger(o.getString("result").substring(2), 16);
                            String pubKey = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_PUBLIC_KEY, "").toString();
                            String hexValue = "";
                            String result = null;
                            //循环发送增加发送交易nonce，result 返回null时也出现了区块确认成功的情况，此时区块高度一致，单个发送返回null失败几乎百分之百
//                            for (; ; ) {
//                                if (EncryptUtils.isJGWBrand()) {
//                                    hexValue = KeyStoreUtils.signedTransactionData(toAddress, nonce.toString(), web3j.ethGasPrice().send().getGasPrice().toString(), String.valueOf(gasLimit), String.valueOf(sendAmountGwei), pubKey);
//                                } else {
//                                    hexValue = KeyStoreUtils.signedTransactionData(password, fromAddress, toAddress, nonce.toString(), web3j.ethGasPrice().send().getGasPrice().toString(), String.valueOf(gasLimit), String.valueOf(sendAmountGwei));
//                                }
//                                EthSendTransaction send = web3j.ethSendRawTransaction(hexValue).send();
//                                //result 代表区块hash，如果result = null表示hash块打包失败，可能是由于nonce重复导致，比如上一个打包的nonce还未上链，此时nonce和上一个打包的nonce一致导致失败
//                                result = send.getResult();
//                                MyLog.e("sendCoin ----result--ok---" + result);
//                                if(result != null) {
//                                    break;
//                                }
//                                nonce = nonce.add(new BigInteger("1"));
//                            }

                            if (EncryptUtils.isJGWBrand()) {
                                hexValue = KeyStoreUtils.signedTransactionData(toAddress, nonce.toString(), web3j.ethGasPrice().send().getGasPrice().toString(), String.valueOf(gasLimit), String.valueOf(sendAmountGwei), pubKey);
                            } else {
                                hexValue = KeyStoreUtils.signedTransactionData(password, fromAddress, toAddress, nonce.toString(), web3j.ethGasPrice().send().getGasPrice().toString(), String.valueOf(gasLimit), String.valueOf(sendAmountGwei));
                            }
                            EthSendTransaction send = web3j.ethSendRawTransaction(hexValue).send();
                            //result 代表区块hash，如果result = null表示hash块打包失败，可能是由于nonce重复导致，比如上一个打包的nonce还未上链，此时nonce和上一个打包的nonce一致导致失败
                            result = send.getResult();
                            MyLog.e("sendCoin ----result--ok---" + result);
                            handler.sendEmptyMessage(0);

                            GetSendStatusService.startNewService(Common.CoinTypeEnum.ETH, result);
                        } catch (Exception e) {
                            e.printStackTrace();
                            MyLog.e("sendCoin ----onResponse---exception");
                            handler.sendEmptyMessage(-1);

                        }
                    }
                }).start();
            }
        });

        if (true) return;


        //===============================================================================================================
        //===============================================================================================================
        //-------------------------------------------------下面的暂时不用。----------------------------------------------
        //===============================================================================================================
        //===============================================================================================================

//        EtherscanAPI.getInstance().getNonceForAddress(fromAddress, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
////                try {
////                    JSONObject o = new JSONObject(response.body().string());
////                    BigInteger nonce = new BigInteger(o.getString("result").substring(2), 16);
//////                        if(nonce.compareTo(new BigInteger("0")) ==0){
//////                            nonce =new BigInteger("1");
//////                        }
////
//////                    RawTransaction tx = RawTransaction.createTransaction(
//////                            nonce,
//////                            new BigInteger(String.valueOf(gasPrice)),
//////                            new BigInteger(String.valueOf(gasLimit)),
//////                            toAddress,
//////                            new BigDecimal(sendAmountGwei).multiply(ExchangeCalculator.ONE_ETHER).toBigInteger(),
//////                            ""
//////                    );
//////
//////                    Log.d("txx",
//////                            "Nonce: " + tx.getNonce() + "\n" +
//////                                    "gasPrice: " + tx.getGasPrice() + "\n" +
//////                                    "gasLimit: " + tx.getGasLimit() + "\n" +
//////                                    "To: " + tx.getTo() + "\n" +
//////                                    "Amount: " + tx.getValue() + "\n" +
//////                                    "Data: " + tx.getData()
//////                    );
//////
//////                    EthSendTransaction send = web3j.ethSendRawTransaction(hexValue).send();
//////                    byte[] signed = TransactionEncoder.signMessage(tx, (byte) 1, keys);
//////
//////                    forwardTX(signed);
////                } catch (IOException e) {
////                    e.printStackTrace();
//////                    error("Can't connect to network, retry it later");
////                }catch (JSONException)
//            }
//        });


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        if (TextUtils.isEmpty(fromAddress) || !EthWalltUtils.isValidAddress(fromAddress)) {

        } else if (TextUtils.isEmpty(toAddress) || !EthWalltUtils.isValidAddress(toAddress)) {

        }
//        else if (sendAmountGwei == 0 || sendAmountGwei < 0) {
//
//        }
//        else if (gas == 0 || gas < 0) {
//            BigDecimal curTxCost = new BigDecimal("0.000252");
//        }
        else {

            BigInteger gasLimitInt = null;
            if (gasLimit <= 0) {
                gasLimitInt = new BigInteger("21000");
            } else {
                try {
                    gasLimitInt = new BigInteger(String.valueOf(gasLimit));
                } catch (Exception e) {
                    gasLimitInt = new BigInteger("21000");
                }
            }

            Intent txService = new Intent(ac, TransactionService.class);
            txService.putExtra("FROM_ADDRESS", fromAddress);
            txService.putExtra("TO_ADDRESS", toAddress);
            txService.putExtra("AMOUNT", sendAmountGwei); // In ether, gets converted by the service itself //在以太网中，由服务本身进行转换
            txService.putExtra("GAS_PRICE", String.valueOf(gasPrice));// (new BigDecimal(realGas + "").multiply(new BigDecimal("1000000000")).toBigInteger()).toString());// "21000000000");
            txService.putExtra("GAS_LIMIT", gasLimitInt.toString());
            txService.putExtra("PASSWORD", password);
            txService.putExtra("DATA", "");
            ac.startService(txService);

//            //金额不足
//           double  realGas = (gas - 8);
//            if (gas < 10)
//                realGas = (double) (gas + 1) / 10d;
//             BigDecimal curTxCost = new BigDecimal("0.000252");
//            BigInteger gaslimit = new BigInteger("21000");
//            curTxCost = (new BigDecimal(gaslimit).multiply(new BigDecimal(realGas + ""))).divide(new BigDecimal("1000000000"), 6, BigDecimal.ROUND_DOWN);
//            //
//
//            BigDecimal curAmount = BigDecimal.ZERO;
//            BigDecimal  b =  curAmount.add(curTxCost, MathContext.DECIMAL64);
//
//            if(b.compareTo(curAvailable) < 0){
//
//            }
        }
    }


    /**
     * 创建钱包
     *
     * @param context
     * @param passphrase 助记词 （用BtcWalltUtils.getPassphrase()生成）
     * @param callback   回调
     */
    public static void createEthWallet2(Context context, String passphrase, CreateUalletCallback callback) {
        String filePath = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.FILE_DIR, "").toString();
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File[] wallets = new File(filePath).listFiles();
                if (null != wallets)
                    for (File f : wallets) {
                        f.delete();
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String masterHex = BtcWalltUtils.getMasterHex(context, passphrase);
        if (null == masterHex) {
            masterHex = passphrase;
        }
        String pwd = Base58.encode(masterHex.getBytes());
        File file = new File(context.getFilesDir(), "");
        try {
            Bip39Wallet wallet = generateBip39Wallet(passphrase, pwd, file);
            String mnemonic = wallet.getMnemonic();
            String filename = wallet.getFilename();
            int lastIndex = filename.lastIndexOf("--") + 2;
            filename = filename.substring(lastIndex, filename.length() - 5);

            WalletStorage.getInstance(context).add(new FullWallet("0x" + filename, filename), context);
            AddressNameConverter.getInstance(context).put("0x" + filename, "Wallet " + ("0x" + filename).substring(0, 6), context);
            SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_ADDRESS_KEY, filename);
            SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_PWD_KEY, pwd);
            SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.FILE_DIR, file.getPath());
            SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.FILE_NAME, wallet.getFilename());
            if (null != callback) {
                callback.onSuccess(filename);
            }
        } catch (Exception e) {
            if (null != callback) {
                callback.onFaild();
            }
        }
    }


    /**
     * 创建钱包(IC方式)
     *
     * @param context
     * @param passphrase 助记词 （用BtcWalltUtils.getPassphrase()生成）
     * @param callback   回调
     */
    public static void createEthWalletFromIC(Context context, String passphrase, CreateUalletCallback callback) {
//        String filePath = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.FILE_DIR,"").toString();
//        if(!TextUtils.isEmpty(filePath)){
//            try {
//                File[] wallets = new File(filePath).listFiles();
//                if (null != wallets)
//                    for (File f : wallets) {
//                        f.delete();
//                    }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        String masterHex = BtcWalltUtils.getMasterHex(context, passphrase);
        if (null == masterHex) {
            masterHex = passphrase;
        }
        String pwd = Base58.encode(masterHex.getBytes());
        File file = new File(context.getFilesDir(), "");
        boolean result = false;
        String filename = null;
        if (EncryptUtils.isJGWBrand()) {
            result = generateBip39WalletFromIC(passphrase, pwd);
        } else {
            try {
                Bip39Wallet wallet = generateBip39Wallet(passphrase, pwd, file);
                String mnemonic = wallet.getMnemonic();
                filename = wallet.getFilename();
                int lastIndex = filename.lastIndexOf("--") + 2;
                filename = filename.substring(lastIndex, filename.length() - 5);

                WalletStorage.getInstance(context).add(new FullWallet("0x" + filename, filename), context);
                AddressNameConverter.getInstance(context).put("0x" + filename, "Wallet " + ("0x" + filename).substring(0, 6), context);
                SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_ADDRESS_KEY, filename);
                SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_PWD_KEY, pwd);
                SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.FILE_DIR, file.getPath());
                SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.FILE_NAME, wallet.getFilename());
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        }
        if (null != callback) {
            if (result) {
                callback.onSuccess(filename);
            } else {
                callback.onFaild();
            }
        }
    }

    public static boolean hasEthWallet() {
        if (EncryptUtils.isJGWBrand())
            return !TextUtils.isEmpty(SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_PUBLIC_KEY, "").toString());
        List<StorableWallet> list = WalletStorage.getInstance(App.getInstance()).get();
        return list != null && list.size() > 0;
    }

    public static String getEthAddress() {
        if (EncryptUtils.isJGWBrand())
            return SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_ADDRESS_KEY, "").toString();
        List<StorableWallet> list = WalletStorage.getInstance(App.getInstance()).get();
        return list.get(0).getPubKey();
    }


    static SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    public static boolean generateBip39WalletFromIC(String mnemonic, String password) {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);
        byte[] seed = MyMnemonicUtils.generateSeed(mnemonic, password);
        ECKeyPair privateKey = ECKeyPair.create(Hash.sha256(seed));
        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_PUBLIC_KEY, privateKey.getPublicKey().toString());
        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_ADDRESS_KEY, Keys.getAddress(privateKey));
        return EncryptUtils.writePrivateKey(privateKey);
    }

    public static Bip39Wallet generateBip39Wallet(String mnemonic, String password, File destinationDirectory) throws CipherException, IOException {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);
//        String mnemonic = generateMnemonic(initialEntropy); //直接传过来
        byte[] seed = MyMnemonicUtils.generateSeed(mnemonic, password);
        ECKeyPair privateKey = ECKeyPair.create(Hash.sha256(seed));
        String walletFile = WalletUtils.generateWalletFile(password, privateKey, destinationDirectory, false);
        return new Bip39Wallet(walletFile, mnemonic);
    }

    public static String generateMnemonic(byte[] initialEntropy) {
        MyMnemonicUtils.validateInitialEntropy(initialEntropy);
        int ent = initialEntropy.length * 8;
        int checksumLength = ent / 32;
        byte checksum = MyMnemonicUtils.calculateChecksum(initialEntropy);
        boolean[] bits = MyMnemonicUtils.convertToBits(initialEntropy, checksum);
        int iterations = (ent + checksumLength) / 11;
        StringBuilder mnemonicBuilder = new StringBuilder();

        for (int i = 0; i < iterations; ++i) {
            int index = MyMnemonicUtils.toInt(MyMnemonicUtils.nextElevenBits(bits, i));
            mnemonicBuilder.append((String) MyMnemonicUtils.WORD_LIST.get(index));
            boolean notLastIteration = i < iterations - 1;
            if (notLastIteration) {
                mnemonicBuilder.append(" ");
            }
        }

        return mnemonicBuilder.toString();
    }


    public static void createEthWallet(Context context, CreateUalletCallback callback) {

        ArrayList<StorableWallet> storedwallets = new ArrayList<StorableWallet>(WalletStorage.getInstance(context).get());

        for (StorableWallet s : storedwallets) {
            EthWalltUtils.delWallet(context, s.getPubKey());
        }
        //删除姨太的第二种方式
        try {
            File[] wallets = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Lunary/").listFiles();
            if (null != wallets)
                for (File f : wallets) {
                    f.delete();
                }
        } catch (Exception e) {

        }

        String passphrase = BtcWalltUtils.getPassphrase();
        String masterHex = BtcWalltUtils.getMasterHex(context, passphrase);
        if (null == masterHex)
            masterHex = passphrase;
        String pwd = Base58.encode(masterHex.getBytes());
//        pwd= "EthWalltUtilsEthWalltUtils";
        EthWalltUtils.createWallet(context, null, pwd, callback);

    }


    /**
     * 创建钱包的总入口（创建的是一个正常钱包）
     *
     * @param context    上下文
     * @param privatekey 私钥 ，可以为空
     * @param password   创建钱包的密码
     * @param callback   回调接口
     */
    public static void createWallet(Context context, String privatekey, String password, CreateUalletCallback callback) {

        try {
            String walletAddress;
            if (TextUtils.isEmpty(privatekey)) { // Create new key
                walletAddress = EthWalltUtils.generateNewWalletFile(password, new File(context.getFilesDir(), ""), true);
            } else { // Privatekey passed
                ECKeyPair keys = ECKeyPair.create(Hex.decode(privatekey));
                MyLog.e("keys.getPrivateKey()--->>" + keys.getPrivateKey());
                MyLog.e("keys.getPublicKey()-->>>" + keys.getPublicKey());
//                ECKeyPair keys = ECKeyPair.create(Hex.decode("f7bf7d387e9a9e576e70b3ae902731a935655dd78828224b3f5c8eb807b0ef3dd260a3c5f3a3053a7da01e0b1a075f975de05a54ba2a990b21cfebd16fb819bf"));
                walletAddress = EthWalltUtils.generateWalletFile(password, keys, new File(context.getFilesDir(), ""), true);
            }
            WalletStorage.getInstance(context).add(new FullWallet("0x" + walletAddress, walletAddress), context);
            AddressNameConverter.getInstance(context).put("0x" + walletAddress, "Wallet " + ("0x" + walletAddress).substring(0, 6), context);

            MyLog.e("adddres--->>" + walletAddress);


            SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_ADDRESS_KEY, walletAddress);


            callback.onSuccess(walletAddress);
        } catch (Exception e) {
            callback.onFaild();
        }
    }


    public static String parseBalance(String balance, int comma) {
        if (balance.equals("0")) return "0";
        return new BigDecimal(balance).divide(new BigDecimal(1000000000000000000d), comma, BigDecimal.ROUND_UP).toPlainString();
    }

    /**
     * 获取可用的姨太币数量
     *
     * @param address  地址
     * @param callback 回调
     */
    public static final void getCurAvailable(final Activity ac, String address, final TLCallback callback) {

        if (!address.contains("0x")) {
            address = "0x" + address;
        }

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 0) {
                    callback.onSuccess(msg.obj);
                } else {
                    callback.onFail(-1, null);
                }
            }
        };


//        new Thread(new Runnable() {
//            @Override
//            public void run() {

        final Message msg = handler.obtainMessage();
//                try {
//                    Web3j web3j = Web3jFactory.build(new HttpService(Common.Eth.URL));
//                    Request<?, EthGetBalance> ethGetBalanceRequest = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST);
//                    BigInteger balance = ethGetBalanceRequest.sendAsync().get().getBalance();
//
//                    msg.obj = balance.toString();
//                    msg.what = 0;
//                    MyLog.e("balance--getCurAvailable>>>?" + balance.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
        //另外一种方式
        try {
            EtherscanAPI.getInstance().getBalance(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    msg.what = -1;
                    handler.sendMessage(msg);
                }

                @Override
                public void onResponse(final Call call, final Response response) throws IOException {
//                                callback.onResponse(call, response);
//                                {"status":"1","message":"OK","result":"0"}
                    try {
                        msg.what = 0;
                        msg.obj = new JSONObject(response.body().string()).optString("result");
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        msg.what = -1;
                        handler.sendMessage(msg);
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            if (null != callback) {

            }
            msg.what = -1;
        } finally {
        }

//                } finally {
//                    handler.sendMessage(msg);
//                }
//            }
//        }).start();


        //////////////////////////////////////////////////////////////


    }

    /**
     * 删除钱包
     *
     * @param context
     * @param address
     */
    public static void delWallet(Context context, String address) {
        WalletStorage.getInstance(context).removeWallet(address, context);
    }


    /**
     * @param ethAddress 接受的地址（也就是publicKey）
     * @param amount     需要接受的数量，可以为0（单位gwei）
     */
    public static String requestCoin(String ethAddress, long amount) {
        String iban = "iban:" + ethAddress;
        if (amount > 0) {
            iban += "?amount=" + amount;
        }
        return iban;
    }

    public static interface CreateUalletCallback {
        public void onSuccess(String address);

        public void onFaild();
    }

    // OVERRIDING THOSE METHODS BECAUSE OF CUSTOM WALLET NAMING (CUTING ALL THE TIMESTAMPTS FOR INTERNAL STORAGE)

    public static String generateFullNewWalletFile(String password, File destinationDirectory) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        return generateNewWalletFile(password, destinationDirectory, true);
    }

    public static String generateLightNewWalletFile(String password, File destinationDirectory) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {

        return generateNewWalletFile(password, destinationDirectory, false);
    }

    public static String generateNewWalletFile(String password, File destinationDirectory, boolean useFullScrypt)
            throws CipherException, IOException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = ECKeyPair.create(createSecp256k1KeyPair());// Keys.createEcKeyPair(); //createSecp256k1KeyPair() //测试

        MyLog.e("keys.getPrivateKey()--->>" + ecKeyPair.getPrivateKey());
        MyLog.e("keys.getPublicKey()-->>>" + ecKeyPair.getPublicKey());
        return generateWalletFile(password, ecKeyPair, destinationDirectory, useFullScrypt);
    }

    static KeyPair createSecp256k1KeyPair() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
//        KeyPairGenerator keyPairGenerator2 = new org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi.ECDSA();
        KeyPairGenerator keyPairGenerator = new org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi.ECDSA();// KeyPairGenerator.getInstance("ECDSA", "SC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");


        //遍历所有的属性，动态修改engine的具体实现类
        //  org.spongycastle.jcajce.provider.asymmetric.ec.KeyPairGeneratorSpi.engine
        // ECKeyPairGenerator 变为ECKeyPairGenerator2
        Class<?> clazz = keyPairGenerator.getClass();
        try {
            for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    int mod = field.getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object val = field.get(keyPairGenerator);
                    if (val instanceof org.spongycastle.crypto.generators.ECKeyPairGenerator) {
                        ECKeyPairGenerator2 ec = new ECKeyPairGenerator2();
                        field.set(keyPairGenerator, ec);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(e.getMessage());
        }
        keyPairGenerator.initialize(ecGenParameterSpec, new SecureRandom());
        KeyPair kp = keyPairGenerator.generateKeyPair();
        BigInteger i = ECKeyPairGenerator2.shangInt;
        return kp;
    }

    public static String generateWalletFile(String password, ECKeyPair ecKeyPair, File destinationDirectory, boolean useFullScrypt) throws CipherException, IOException {

        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }
//               ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        //在外置卡生成
        String filename = WalletUtils.generateWalletFile(password, ecKeyPair, destinationDirectory, false);

        KeyStoreUtils.genKeyStore2Files(ecKeyPair);

        String msg = "fileName:\n" + filename
                + "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
                + "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());
        MyLog.e("地址信息>>>", msg);

        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_PRIVET_KEY, ecKeyPair.getPrivateKey().toString());
        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_PWD_KEY, password);

        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.FILE_DIR, destinationDirectory.getPath());
        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.FILE_NAME, filename);


        int lastIndex = filename.lastIndexOf("--") + 2;
        filename = filename.substring(lastIndex, filename.length() - 5);
        return filename;


// ----------下面的代码会内存溢出----------------------------------------------
//        WalletFile walletFile;
//        if (useFullScrypt) {
//            walletFile = Wallet.createStandard(password, ecKeyPair);
//        } else {
//            walletFile = Wallet.createLight(password, ecKeyPair);
//        }
//
//        //保存私钥跟密码
//        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_PRIVET_KEY, ecKeyPair.getPrivateKey().toString());
//        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_PWD_KEY, password);
//
//
//        String fileName = getWalletFileName(walletFile);
//        File destination = new File(destinationDirectory, fileName);
//
//        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
//        objectMapper.writeValue(destination, walletFile);
//        return fileName;
    }

    private static String getWalletFileName(WalletFile walletFile) {
        return walletFile.getAddress();
    }


    public static boolean isValidAddress(String address) {
        String addressNoPrefix = Numeric.cleanHexPrefix(address);
        return addressNoPrefix.length() == ADDRESS_LENGTH_IN_HEX;
    }

    public void createWallet(Context context, String pwd) {
        File file = new File(context.getFilesDir(), "");
    }
}
