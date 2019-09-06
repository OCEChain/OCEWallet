package com.idea.jgw.logic.btc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.btc.model.CreatedTransactionObject;
import com.idea.jgw.logic.btc.model.TLAccountObject;
import com.idea.jgw.logic.btc.model.TLAppDelegate;
import com.idea.jgw.logic.btc.model.TLBitcoinjWrapper;
import com.idea.jgw.logic.btc.model.TLCoin;
import com.idea.jgw.logic.btc.model.TLColdWallet;
import com.idea.jgw.logic.btc.model.TLImportedAddress;
import com.idea.jgw.logic.btc.model.TLNotificationEvents;
import com.idea.jgw.logic.btc.model.TLSendFormData;
import com.idea.jgw.logic.btc.model.TLSpaghettiGodSend;
import com.idea.jgw.logic.btc.model.TLStealthAddress;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bitcoinj.core.Utils.HEX;


/**
 * 钱包的操作（必须先调用init方法）
 * Created by vam on 2018\6\3 0003.
 */

public class BtcWalltUtils {

    static final String TAG = "BtcWalltUtils";

//    static {
//        TLAppDelegate.instance(App.getInstance()).initAppDelegate();
//    }


    public static void init() {
        TLAppDelegate.instance(App.getInstance()).initAppDelegate();
    }

    public static boolean hasSetupHDWallet() {
        return TLAppDelegate.instance().preferences.hasSetupHDWallet();
    }


//    /**
//     * 初始化
//     *
//     * @param context
//     */
//    public void init(Context context) {
//        TLAppDelegate.instance(context).initAppDelegate();
//    }

    public static void createwWallet(final Context context, final TLCallback callback) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                callback.onSuccess(null);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadWallet(context);
                Message message = Message.obtain();
                handler.sendMessage(Message.obtain(message));
            }
        }).start();
    }

    private static void loadWallet(Context context) {
        String version = null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
            //int verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "NameNotFoundException: " + e.getLocalizedMessage());
        }

        TLAppDelegate.instance().initializeWalletApp(version, false);
        TLAppDelegate.instance().transactionListener.reconnect();
        TLAppDelegate.instance().stealthWebSocket.reconnect();
    }


    /**
     * 获取btc的助记词
     * @return
     */
    public static final String getPassphrase(){
        String passphrase = SharedPreferenceManager.getInstance().getPassphrase();
        MyLog.e("passphrase-->"+passphrase);
        if(TextUtils.isEmpty(passphrase)){
            passphrase = BtcWalltUtils.generateMnemonicPassphrase(App.getInstance());
            SharedPreferenceManager.getInstance().setPassphrase(passphrase);
        }
        return passphrase;
    }


    public static Boolean phraseIsValid(Context context,String phrase) {
        try {
            InputStream is = context.getAssets().open("bip39-wordlist.txt");
            MnemonicCode mc = new MnemonicCode(is,"ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db");
            byte[] entropy = mc.toEntropy(split(phrase));
            return true;
        } catch (IOException name) {
            return false;
        } catch (MnemonicException name) {
            return false;
        }
    }


    //助记词跟熵
    private static String generateMnemonicPassphrase(Context context ) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            BigInteger b = new BigInteger(128, secureRandom);
            //
            //
            //这里是用以太的随机数
            //
//            b = new BigInteger("87793574208385090030104583069767126443749156201006497373256486138372425411882");
//            String s = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_SHANG_KEY, "").toString();
//            b = new BigInteger(s);
            Log.e("SecureRandom", "" + b);
//            String entropy = new BigInteger(entropyBitLength, secureRandom).toString(16); //熵
            String entropy = b.toString(16);
            while (entropy.length() < 32) {
                entropy = '0' + entropy;
            }
            InputStream is = context.getAssets().open("bip39-wordlist.txt");
            MnemonicCode mc = new MnemonicCode(is,"ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db");
//            MnemonicCode mc = new MnemonicCode();
            List<String> code = mc.toMnemonic(HEX.decode(entropy));
            String mnemonic = StringUtils.join(code, " ");
            return mnemonic;
        } catch (IOException name) {
            return null;
        } catch (MnemonicException name) {
            return null;
        }
    }


    //对助记词进行HEX ,也就是种子
    public static String getMasterHex(Context context,String mnemonic) {
        try {
            InputStream is = context.getAssets().open("bip39-wordlist.txt");
            MnemonicCode mc = new MnemonicCode(is,"ad90bf3beb7b0eb7e5acd74727dc0da96e0a280a258354e7293fb7e211ac03db");
//            MnemonicCode mc = new MnemonicCode();
            byte[] entropy = mc.toSeed(split(mnemonic), "");
            return HEX.encode(entropy);
        } catch (IOException name) {
        } catch (Exception e) {
        }
        return null;
    }
    private static List<String> split(String words) {
        return new ArrayList<String>(Arrays.asList(words.split("\\s+")));
    }


    /**
     * 恢复钱包
     *
     * @param mnemonic
     */
    public static void recoverHDWallet(String mnemonic) {
        TLAppDelegate.instance().recoverHDWallet(mnemonic);
    }

    /**
     * 删除钱包
     *
     * @return
     */
    public static boolean delWallet() {
        try {
            return TLAppDelegate.instance().accounts.deleteAccount(0);
        } catch (Exception e) {
            return false;
        }
    }

    public static void sendCoin(Context context, final String toAddress, final String amount, final TLCallback callback, Handler handler) {

//        //依靠websocket知道付款已经发送的时间可能不可靠，因此在一段时间后取消
//        final int TIME_TO_WAIT_TO_HIDE_HUD_AND_REFRESH_ACCOUNT = 13;
//        handler.postDelayed(sendRunnable, TIME_TO_WAIT_TO_HIDE_HUD_AND_REFRESH_ACCOUNT * 1000);


        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                    }
                }
        ).start();

        final TLAppDelegate appDelegate = TLAppDelegate.instance();

        //地址不合法
        if (!isValidAddress(toAddress, appDelegate.appWallet.walletConfig.isTestnet)) {
            callback.onFail(Common.BtcErrorEnum.ERROR_ADDRES_INVALID.getIndex(), Common.BtcErrorEnum.ERROR_ADDRES_INVALID.getName());
            return;
        }

        //金额不合法
        final TLCoin inputtedAmount = appDelegate.currencyFormat.properBitcoinAmountStringToCoin(amount);
        if (inputtedAmount.equalTo(TLCoin.zero())) {
            callback.onFail(Common.BtcErrorEnum.ERROR_AMOUNT_INVALID.getIndex(), Common.BtcErrorEnum.ERROR_AMOUNT_INVALID.getName());
            return;
        }

        //检查是否使用动态手续费  && 没有动态手续费的缓存
        if (appDelegate.preferences.enabledDynamicFee() && !appDelegate.txFeeAPI.haveUpdatedCachedDynamicFees()) {
            appDelegate.txFeeAPI.getDynamicTxFee(new TLCallback() { //获取动态费用，并且缓存起来

                @Override
                public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

                }

                @Override
                public void onSetHex(String hex) {
                }

                @Override
                public void onSuccess(Object obj) {
                    showFinalPromptReviewTx(appDelegate, inputtedAmount, callback);
                }

                @Override
                public void onFail(Integer status, String error) {
                    showFinalPromptReviewTx(appDelegate, inputtedAmount, callback);
                }
            });
        } else { //不用动态手续费
            showFinalPromptReviewTx(appDelegate, inputtedAmount, callback);
        }
    }


    private static void showFinalPromptReviewTx(final TLAppDelegate appDelegate, final TLCoin inputtedAmount, final TLCallback callback) {
        //判断是否启动了动态手续费
        if (appDelegate.preferences.enabledDynamicFee()) {

            //是否有更新UTXO
            if (!appDelegate.godSend.haveUpDatedUTXOs()) {
                appDelegate.godSend.getAndSetUnspentOutputs(new TLCallback() {

                    @Override
                    public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

                    }

                    @Override
                    public void onSetHex(String hex) {

                    }

                    @Override
                    public void onSuccess(Object obj) {
                        checkToFetchDynamicFees(appDelegate, inputtedAmount, callback);
                    }

                    @Override
                    public void onFail(Integer status, String error) {
//                        TLPrompts.promptErrorMessage(getActivity(), getString(R.string.error), getString(R.string.error_fetching_unspent_outputs_try_again_later));
                        callback.onFail(Common.BtcErrorEnum.ERROR_FETCHING_UNSPENT_OUTPUTS_TRY_AGAIN_LATER.getIndex(), Common.BtcErrorEnum.ERROR_FETCHING_UNSPENT_OUTPUTS_TRY_AGAIN_LATER.getName());
                    }
                });
            } else {
                checkToFetchDynamicFees(appDelegate, inputtedAmount, callback);
            }
        } else {
            showReviewPaymentView(inputtedAmount, false, callback);
        }
    }

    private static void checkToFetchDynamicFees(TLAppDelegate appDelegate, final TLCoin inputtedAmount, final TLCallback callback) {
        if (!appDelegate.txFeeAPI.haveUpdatedCachedDynamicFees()) {
            appDelegate.txFeeAPI.getDynamicTxFee(new TLCallback() {

                @Override
                public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

                }

                @Override
                public void onSetHex(String hex) {

                }

                @Override
                public void onSuccess(Object obj) {
                    showReviewPaymentView(inputtedAmount, true, callback);
                }

                @Override
                public void onFail(Integer status, String error) {
                    callback.onFail(Common.BtcErrorEnum.ERROR_DYNAMIC_FEES_FAIL.getIndex(), Common.BtcErrorEnum.ERROR_DYNAMIC_FEES_FAIL.getName());
//                    TLPrompts.promptErrorMessage(getActivity(), getString(R.string.error), getString(R.string.unable_to_get_dynamic_fees));
                    showReviewPaymentView(inputtedAmount, false, callback);
                }
            });
        } else {
            showReviewPaymentView(inputtedAmount, true, callback);
        }
    }


    private static void showReviewPaymentView(TLCoin inputtedAmount, boolean useDynamicFees, final TLCallback callback) {
        final TLAppDelegate appDelegate = TLAppDelegate.instance();
        TLCoin fee;
        int txSizeBytes;
        if (useDynamicFees) {
            if (appDelegate.sendFormData.useAllFunds) {
                fee = appDelegate.sendFormData.feeAmount;
            } else {
                if (appDelegate.godSend.getSelectedObjectType() == TLSendFormData.TLSelectObjectType.Account) {
                    TLAccountObject accountObject = (TLAccountObject) appDelegate.godSend.getSelectedSendObject();
                    int inputCount = accountObject.getInputsNeededToConsume(inputtedAmount);
                    //FIXME account for change output, output count likely 2 (3 if have stealth payment) cause if user dont do click use all funds because will likely have change
                    // but for now dont need to be fully accurate with tx fee, for now we will underestimate tx fee, wont underestimate much because outputs contributes little to tx size
                    // FIXME占转换输出，有可能输出数2（如果有隐形支付则为3）导致用户不点击使用所有资金因为可能会有变化
                    //  但现在不需要完全准确的Tx费用，现在我们会低估Tx费用，不会低估很多，因为输出对Tx尺寸的贡献很小
                    txSizeBytes = appDelegate.godSend.getEstimatedTxSize(inputCount, 1);
                    Log.d(TAG, "showPromptReviewTx TLAccountObject useDynamicFees inputCount txSizeBytes: " + inputCount + " " + txSizeBytes);
                } else {
                    TLImportedAddress importedAddress = (TLImportedAddress) appDelegate.godSend.getSelectedSendObject();
                    //FIXME account for change output, output count likely 2 (3 if have stealth payment) cause if user dont do click use all funds because will likely have change
                    //                    FIXME占转换输出，有可能输出数2（如果有隐形支付则为3）导致用户不点击使用所有资金因为可能会有变化

                    int inputCount = importedAddress.getInputsNeededToConsume(inputtedAmount);
                    txSizeBytes = appDelegate.godSend.getEstimatedTxSize(inputCount, 1);
                    Log.d(TAG, "showPromptReviewTx importedAddress useDynamicFees inputCount txSizeBytes: " + importedAddress.unspentOutputsCount + " " + txSizeBytes);
                }

                Long dynamicFeeSatoshis = appDelegate.txFeeAPI.getCachedDynamicFee();
                if (dynamicFeeSatoshis != null) {
                    fee = new TLCoin(txSizeBytes * dynamicFeeSatoshis);
                    Log.d(TAG, "showPromptReviewTx coinFeeAmount dynamicFeeSatoshis: " + txSizeBytes * dynamicFeeSatoshis);

                } else {
                    fee = new TLCoin(appDelegate.preferences.getTransactionFee());
                }
                appDelegate.sendFormData.feeAmount = fee;
            }
        } else {
            fee = new TLCoin(appDelegate.preferences.getTransactionFee());
            appDelegate.sendFormData.feeAmount = fee;
        }

        TLCoin amountNeeded = inputtedAmount.add(fee);
        TLCoin sendFromBalance = appDelegate.godSend.getCurrentFromBalance();
        if (amountNeeded.greater(sendFromBalance)) {

//            你有%1$s，需要%2$s。（包括交易费用）
            String errorStr = App.getInstance().getString(R.string.msore_funds_needed, appDelegate.currencyFormat.coinToProperBitcoinAmountString(sendFromBalance, true), appDelegate.currencyFormat.coinToProperBitcoinAmountString(amountNeeded, true));
            Common.BtcErrorEnum.ERROR_MORE_FUNDS_NEEDED.setName(errorStr);
            callback.onFail(Common.BtcErrorEnum.ERROR_MORE_FUNDS_NEEDED.getIndex(), Common.BtcErrorEnum.ERROR_MORE_FUNDS_NEEDED.getName());
//            String msg = getString(R.string.more_funds_needed, appDelegate.currencyFormat.coinToProperBitcoinAmountString(sendFromBalance, true), appDelegate.currencyFormat.coinToProperBitcoinAmountString(amountNeeded, true));
//            TLPrompts.promptErrorMessage(getActivity(), getString(R.string.insufficient_funds), msg);
            return;
        }
//
//        Log.d(TAG, "showPromptReviewTx accountBalance: " + sendFromBalance.toNumber());
//        Log.d(TAG, "showPromptReviewTx inputtedAmount: " + inputtedAmount.toNumber());
//        Log.d(TAG, "showPromptReviewTx fee: " + fee.toNumber());
//        appDelegate.sendFormData.fromLabel = appDelegate.godSend.getCurrentFromLabel();
        confirmPayment(appDelegate, callback);
    }


    private static final void confirmPayment(final TLAppDelegate appDelegate, final TLCallback callback) {
        //
        //
        //---------------------------- 等下再处理---------------------------------
        //
        //
//        startSendTimer();
        //
        //
        //---------------------------- 等下再处理---------------------------------
        //
        //


//        TLCoin sendAmount = appDelegate.sendFormData.toAmount;
//        String sendAmountString = appDelegate.currencyFormat.coinToProperBitcoinAmountString(sendAmount);
//        TLCoin feeAmount = appDelegate.sendFormData.feeAmount;
//        TLCoin totalAmount = sendAmount.add(feeAmount);
//
//        String fiatAmount = this.fiatAmountEditText.getText().toString(); //法币
//        String feeString = appDelegate.currencyFormat.coinToProperBitcoinAmountString(feeAmount); //费用

        if (!appDelegate.godSend.haveUpDatedUTXOs()) {
            appDelegate.godSend.getAndSetUnspentOutputs(new TLCallback() {

                @Override
                public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

                }

                @Override
                public void onSuccess(Object obj) {
                    initiateSend(appDelegate, callback);
                }

                @Override
                public void onSetHex(String hex) {

                }

                @Override
                public void onFail(Integer status, String error) {
                    cancelSend();
//                    TLPrompts.promptErrorMessage(getActivity(), getString(R.string.error), getString(R.string.error_fetching_unspent_outputs_try_again));
                    callback.onFail(Common.BtcErrorEnum.ERROR_FETCHING_UNSPENT_OUTPUTS_TRY_AGAIN.getIndex(), Common.BtcErrorEnum.ERROR_FETCHING_UNSPENT_OUTPUTS_TRY_AGAIN.getName());
                }
            });
        } else {
            initiateSend(appDelegate, callback);
        }
    }

    private static void initiateSend(final TLAppDelegate appDelegate, final TLCallback callback) {
        TLCoin unspentOutputsSum = appDelegate.godSend.getCurrentFromUnspentOutputsSum();
        if (unspentOutputsSum.less(appDelegate.sendFormData.toAmount)) {
            // can only happen if unspentOutputsSum is for some reason less then the balance computed from the transactions, which it shouldn't
            //只有在unspentOutputsSum由于某种原因而小于从交易中计算出的余额时才会发生，而不应该这样做
            cancelSend();
            String unspentOutputsSumString = appDelegate.currencyFormat.coinToProperBitcoinAmountString(unspentOutputsSum, true);
            String str = App.getInstance().getString(R.string.funds_may_be_pending_confirmation, unspentOutputsSum);
            Common.BtcErrorEnum.ERROR_FUNDS_MAY_BE_PENDING_CONFIRMATION.setName(str);
            callback.onFail(Common.BtcErrorEnum.ERROR_FUNDS_MAY_BE_PENDING_CONFIRMATION.getIndex(), Common.BtcErrorEnum.ERROR_FUNDS_MAY_BE_PENDING_CONFIRMATION.getName());
//            TLPrompts.promptErrorMessage(getActivity(), getString(R.string.insufficient_funds), getString(R.string.funds_may_be_pending_confirmation, unspentOutputsSumString));
            return;
        }

        Pair<String, TLCoin> toAddressesAndAmount = new Pair<String, TLCoin>(appDelegate.sendFormData.getAddress(), appDelegate.sendFormData.toAmount);
        List<Pair<String, TLCoin>> toAddressesAndAmounts = new ArrayList<Pair<String, TLCoin>>();
        toAddressesAndAmounts.add(toAddressesAndAmount);
        CreatedTransactionObject createdTransactionObject = null;
        try {
            boolean signTx = !appDelegate.godSend.isColdWalletAccount();
            createdTransactionObject = appDelegate.godSend.createSignedSerializedTransactionHex(toAddressesAndAmounts, appDelegate.sendFormData.feeAmount, signTx);
        } catch (TLSpaghettiGodSend.DustException e) {
            String amountCanSendString = appDelegate.currencyFormat.coinToProperBitcoinAmountString(e.spendableAmount, true);
            cancelSend();

            String str = App.getInstance().getString(R.string.insufficient_funds_dust, unspentOutputsSum);
            Common.BtcErrorEnum.ERROR_INSUFFICIENT_FUNDS_DUST.setName(str);
            callback.onFail(Common.BtcErrorEnum.ERROR_INSUFFICIENT_FUNDS_DUST.getIndex(), Common.BtcErrorEnum.ERROR_INSUFFICIENT_FUNDS_DUST.getName());
//            TLPrompts.promptErrorMessage(getActivity(), getString(R.string.error), getString(R.string.insufficient_funds_dust, amountCanSendString));
        } catch (TLSpaghettiGodSend.InsufficientFundsException e) {
            String valueSelectedString = appDelegate.currencyFormat.coinToProperBitcoinAmountString(e.valueSelected, true);
            String valueNeededString = appDelegate.currencyFormat.coinToProperBitcoinAmountString(e.valueNeeded, true);
            cancelSend();
            String str = App.getInstance().getString(R.string.insufficient_funds_account_balance, valueSelectedString, valueNeededString);
            Common.BtcErrorEnum.ERROR_INSUFFICIENT_FUNDS_ACCOUNT_BALANCE.setName(str);
            callback.onFail(Common.BtcErrorEnum.ERROR_INSUFFICIENT_FUNDS_ACCOUNT_BALANCE.getIndex(), Common.BtcErrorEnum.ERROR_INSUFFICIENT_FUNDS_ACCOUNT_BALANCE.getName());

//            TLPrompts.promptErrorMessage(getActivity(), getString(R.string.error), getString(R.string.insufficient_funds_account_balance, valueSelectedString, valueNeededString));
        } catch (TLSpaghettiGodSend.DustOutputException e) {
            String dustAmountBitcoins = appDelegate.currencyFormat.coinToProperBitcoinAmountString(e.dustAmount, true);
            cancelSend();
            String str = App.getInstance().getString(R.string.cannot_create_transactions_with_outputs_less_then, dustAmountBitcoins);
            Common.BtcErrorEnum.ERROR_CANNOT_CREATE_TRANSACTIONS_WITH_OUTPUTS_LESS_THEN.setName(str);
            callback.onFail(Common.BtcErrorEnum.ERROR_CANNOT_CREATE_TRANSACTIONS_WITH_OUTPUTS_LESS_THEN.getIndex(), Common.BtcErrorEnum.ERROR_CANNOT_CREATE_TRANSACTIONS_WITH_OUTPUTS_LESS_THEN.getName());
//            TLPrompts.promptErrorMessage(getActivity(), getString(R.string.error), getString(R.string.cannot_create_transactions_with_outputs_less_then, dustAmountBitcoins));
        } catch (TLSpaghettiGodSend.InsufficientUnspentOutputException e) {
            Log.d(TAG, "SendFragment createSignedSerializedTransactionHex InsufficientUnspentOutputException " + e.getLocalizedMessage());
            callback.onFail(Common.BtcErrorEnum.ERROR_UNDEFINABLE_EXCEPTION.getIndex(), Common.BtcErrorEnum.ERROR_UNDEFINABLE_EXCEPTION.getName());
        } catch (TLSpaghettiGodSend.CreateTransactionException e) {
            cancelSend();
//            TLPrompts.promptErrorMessage(getActivity(), getString(R.string.error), e.getLocalizedMessage());
            callback.onFail(Common.BtcErrorEnum.ERROR_UNDEFINABLE_EXCEPTION.getIndex(), Common.BtcErrorEnum.ERROR_UNDEFINABLE_EXCEPTION.getName());
        } finally {
            if (createdTransactionObject == null) {
                callback.onFail(Common.BtcErrorEnum.ERROR_UNDEFINABLE_EXCEPTION.getIndex(), Common.BtcErrorEnum.ERROR_UNDEFINABLE_EXCEPTION.getName());
                return;
            }
        }


        String txHex = createdTransactionObject.txHex;
        List<String> realToAddresses = createdTransactionObject.realToAddresses;

        if (txHex == null) {
            //should not reach here, because I check sum of unspent outputs already,
            // unless unspent outputs contains dust and are require to filled the amount I want to send
            //不应该到达此处，因为我已经检查了未使用输出的总和，
            //除非未使用的输出包含灰尘，并且需要填写我想发送的数量
            cancelSend();
            callback.onFail(Common.BtcErrorEnum.ERROR_UNDEFINABLE_EXCEPTION.getIndex(), Common.BtcErrorEnum.ERROR_UNDEFINABLE_EXCEPTION.getName());
            return;
        }

        //如果是在线钱包
        if (appDelegate.godSend.isColdWalletAccount()) {
            cancelSend();

            JSONArray txInputsAccountHDIdxes = createdTransactionObject.txInputsAccountHDIdxes;
            JSONArray inputScripts = createdTransactionObject.inputHexScripts;
            promptToSignTransaction(callback, appDelegate, txHex, inputScripts, txInputsAccountHDIdxes);
            return;
        }

        String txHash = createdTransactionObject.txHash;
        prepAndBroadcastTx(appDelegate, txHex, txHash, realToAddresses, callback);
    }


    static void prepAndBroadcastTx(final TLAppDelegate appDelegate, String txHex, String txHash, List<String> realToAddresses, TLCallback callback) {
        if (appDelegate.sendFormData.getAddress() == appDelegate.godSend.getStealthAddress()) {
            appDelegate.pendingSelfStealthPaymentTxid = txHash;
        }
        TLCoin amountMovedFromAccount;
        if (appDelegate.godSend.isPaymentToOwnAccount(appDelegate.sendFormData.getAddress())) {
            amountMovedFromAccount = appDelegate.sendFormData.feeAmount;
        } else {
            amountMovedFromAccount = appDelegate.sendFormData.toAmount.add(appDelegate.sendFormData.feeAmount);
        }

        callback.onAmountMoveFromAccount(amountMovedFromAccount);

        for (String address : realToAddresses) {
            appDelegate.transactionListener.listenToIncomingTransactionForAddress(address);
        }

        String inputtedToAddress = appDelegate.sendFormData.getAddress();
        TLCoin inputtedToAmount = appDelegate.sendFormData.toAmount;
        String sendTxHash = txHash;
        callback.onSetHex(sendTxHash);
        Log.d(TAG, "showPromptReviewTx txHex: " + txHex);
        Log.d(TAG, "showPromptReviewTx txHash: " + txHash);
        broadcastTx(appDelegate, txHex, txHash, inputtedToAddress, callback);
    }


    static void promptToSignTransaction(TLCallback callback, final TLAppDelegate appDelegate, String unSignedTx, JSONArray inputScripts, JSONArray txInputsAccountHDIdxes) {
        String extendedPublicKey = appDelegate.godSend.getExtendedPubKey();
        String airGapDataBase64 = TLColdWallet.createSerializedUnsignedTxAipGapData(unSignedTx, extendedPublicKey, inputScripts, txInputsAccountHDIdxes);
        if (airGapDataBase64 != null) {
            callback.onFail(Common.BtcErrorEnum.ERROR_DOES_NOT_SUPPORT_CLOUD_WALLET.getIndex(), Common.BtcErrorEnum.ERROR_DOES_NOT_SUPPORT_CLOUD_WALLET.getName());


//           List<String> airGapDataBase64PartsArray = TLColdWallet.splitStringToArray(airGapDataBase64);
//            TLPrompts.promptForOKCancel(getActivity(), getString(R.string.spending_from_a_cold_wallet_account),
//                    getString(R.string.spending_from_a_cold_wallet_account_description), new TLPrompts.PromptCallback() {
//                        @Override
//                        public void onSuccess(Object obj) {
//                            showNextUnsignedTxPartQRCode();
//                        }
//
//                        @Override
//                        public void onCancel() {
//
//                        }
//                    });
        }
    }

    static void broadcastTx(final TLAppDelegate appDelegate, String txHex, final String txHash, final String toAddress, final TLCallback callback) {

        appDelegate.pushTxAPI.sendTx(txHex, txHash, toAddress, new TLCallback() {

            @Override
            public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

            }

            @Override
            public void onSetHex(String hex) {

            }

            @Override
            public void onSuccess(Object obj) {
                String txid = (String) obj;
                Log.d(TAG, "showPromptReviewTx pushTx: success " + txid);

                if (TLStealthAddress.isStealthAddress(toAddress, appDelegate.appWallet.walletConfig.isTestnet) == true) {
                    // doing stealth payment with push tx insight get wrong hash back??
                    Log.d(TAG, "showPromptReviewTx pushTx: success txid" + txid);
                    Log.d(TAG, "showPromptReviewTx pushTx: success txHash " + txHash);
                    if (!txid.equals(txHash)) {
                        Log.d(TAG, "API Error: txid return does not match txid in app " + txHash);
                    }
                }

                String label = appDelegate.appWallet.getLabelForAddress(toAddress);
                if (label != null) {
                    appDelegate.appWallet.setTransactionTag(txHash, label);
                }
                clearSendForm(appDelegate);
//                LocalBroadcastManager.getInstance(appDelegate.context).sendBroadcast(new Intent(TLNotificationEvents.EVENT_SEND_PAYMENT));
                callback.onSuccess(null);
            }


            @Override
            public void onFail(Integer status, String error) {
                Log.d(TAG, "showPromptReviewTx pushTx: failure " + status + " " + error);
                if (status == 200) {
                    clearSendForm(appDelegate);
//                    LocalBroadcastManager.getInstance(appDelegate.context).sendBroadcast(new Intent(TLNotificationEvents.EVENT_SEND_PAYMENT));
                    callback.onFail(Common.BtcErrorEnum.ERROR_DOES_NOT_SUPPORT_CLOUD_WALLET.getIndex(), Common.BtcErrorEnum.ERROR_DOES_NOT_SUPPORT_CLOUD_WALLET.getName());
                } else {
//                    TLPrompts.promptErrorMessage(getActivity(), getString(R.string.error), error);
                    callback.onFail(Common.BtcErrorEnum.ERROR_DOES_NOT_SUPPORT_CLOUD_WALLET.getIndex(), Common.BtcErrorEnum.ERROR_DOES_NOT_SUPPORT_CLOUD_WALLET.getName());
                    cancelSend();
                }
            }
        });
    }

    static void clearSendForm(final TLAppDelegate appDelegate) {
        appDelegate.sendFormData.setAddress(null);
        appDelegate.sendFormData.setAmount(null);
//        updateSendForm(appDelegate);
    }

    static void cancelSend() {
//        sendHandler.removeCallbacks(sendRunnable);
//        TLHUDWrapper.hideHUD();
    }

    private static void updateSendForm(final TLAppDelegate appDelegate) {

//        this.toAddressEditText.setText(appDelegate.sendFormData.getAddress());
        if (appDelegate.sendFormData.getAmount() != null) {
//            setAmountEditTextWrapper(appDelegate.sendFormData.getAmount());
//            updateFiatAmountTextFieldExchangeRate();
        } else if (appDelegate.sendFormData.getFiatAmount() != null) {
//            setFiatAmountEditTextWrapper(appDelegate.sendFormData.getFiatAmount());
//            updateAmountTextFieldExchangeRate();
        }
    }


//    private static final Runnable sendRunnable = new Runnable() {
//        @Override
//        public void run() {
//            retryFinishSend();
//        }
//    };
//
//    @UiThread
//    void retryFinishSend() {
//        Log.d(TAG, "retryFinishSend " + sendTxHash);
//        if (sendTxHash == null) {
//            return;
//        }
//        if (!appDelegate.webSocketNotifiedTxHashSet.contains(sendTxHash)) {
//            TLCoin nonUpdatedBalance = appDelegate.godSend.getCurrentFromBalance();
//            TLCoin accountNewBalance = nonUpdatedBalance.subtract(amountMovedFromAccount);
//            Log.d(TAG, "retryFinishSend 2 " + sendTxHash);
//            appDelegate.godSend.setCurrentFromBalance(accountNewBalance);
//        }
//
//        if (!showedPromptedForSentPaymentTxHashSet.contains(sendTxHash)) {
//            showedPromptedForSentPaymentTxHashSet.add(sendTxHash);
//            sendHandler.removeCallbacks(sendRunnable);
//            showPromptPaymentSent(sendTxHash, inputtedToAddress, inputtedToAmount);
//        }
//    }


    /**
     * 检查地址
     *
     * @param address   地址
     * @param isTestnet 是否测试网络
     * @return
     */
    public static Boolean isValidAddress(String address, Boolean isTestnet) {
        try {
            NetworkParameters params = TLBitcoinjWrapper.getNetwork(isTestnet);
            Address addr = Address.fromBase58(params, address);
            return true;
        } catch (AddressFormatException name) {
            if (TLStealthAddress.isStealthAddress(address, isTestnet)) {
                return true;
            }
            return false;
        }
    }
}
