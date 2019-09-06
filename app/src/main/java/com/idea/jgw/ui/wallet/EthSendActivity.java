package com.idea.jgw.ui.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.btc.model.TLCoin;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.IBAN;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by vam on 2018\6\4 0004.
 */
@Route(path = RouterPath.SEND_ETH_ACTIVITY)
public class EthSendActivity extends SendActivity {


    String mCurAmount;
    String mWalletAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mWalletAddress = intent.getStringExtra(EXTRA_ADDRESS);
        mCurAmount = intent.getStringExtra(EXTRA_CUR_AMOUNT);

        tvOfBalance.setText(TextUtils.isEmpty(mCurAmount) ? "0.00" : mCurAmount);
        tvLight.setText("wei");

        ivDigitalLogo.setImageResource(R.mipmap.icon_eth);
//        etReceivedAddress.setText("0x58970b689e65d2bdef658027346adcd899b1b0e0");
//        etSendAmount.setText("0.001");
//        mWalletAddress = "0xd917d23add23a55713b0d5c803de9a8b450e509a";
    }


    @Override
    protected String getSelfAddress() {
        return mWalletAddress;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case QrSanActivity.REQ_CODE:
                    String address = data.getExtras().getString(QrSanActivity.EXTRA_RESULT_QR);
                    if (address.contains("?") && address.contains("&") && address.contains("token")) {
                        String iban = address.substring("iban:".length(), address.indexOf("?"));
                        String amount = address.substring(address.indexOf("=") + 1, address.indexOf("&"));
                        String tokenType = address.substring(address.indexOf("token") + 6);

                        address = IBAN.IBAN2Address(iban);

                    } else if (validAddress(address)) {
                        if (address.startsWith("iban:")) {
                            address = address.replace("iban:", "");
                        }
                        if (IBAN.validateIBAN(address)) {
                            address = IBAN.IBAN2Address(address);
                        }
                        String addressNoPrefix = Numeric.cleanHexPrefix(address);
                        if (addressNoPrefix.contains("0x")) {
                            int index = addressNoPrefix.indexOf("0x") + 2;
                            addressNoPrefix = addressNoPrefix.substring(index);
                        }
                    }
                    if (address.contains("iban:"))
                        address.replace("iban:", "");
                    etReceivedAddress.setText(address);
                    break;
            }
        }
    }

    @Override
    public void onQrScanCodeClick() {
        super.onQrScanCodeClick();
        ARouter.getInstance()
                .build(RouterPath.QR_SCAN_ACTIVITY)
                .navigation(EthSendActivity.this, QrSanActivity.REQ_CODE);
    }

    @Override
    public boolean onSendClick() {
        //地址验证
        String address = etReceivedAddress.getText().toString();
        if (TextUtils.isEmpty(address)) {
            MToast.showLongToast(getResources().getString(R.string.address_empty));
            return false;
        }

        //是否是转给自己
        String tempAddress =address;
        if(!address.startsWith("0x")){
            tempAddress = "0x"+address;
        }
        if(tempAddress.endsWith(getSelfAddress())){
            MToast.showLongToast(getResources().getString(R.string.address_is_self));
            return false;
        }

        //金额验证
        String sendAmount = etSendAmount.getText().toString();
        if (TextUtils.isEmpty(sendAmount)) {
            MToast.showLongToast(getResources().getString(R.string.send_amount_empty));
            return false;
        }

        if (!validAddress(address)) {
            return false;
        }

        return true;
    }
//    protected void showPwdInputDialog() {
//        onPasswordPassed("123456");
//    }

    private boolean validAddress(String address) {
        if (!EthWalltUtils.isValidAddress(address)) {
            MToast.showLongToast(getResources().getString(R.string.address_wrong));
            return false;
        }
        return true;
    }

    @Override
    public void onPasswordPassed() {
        String pwd = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_PWD_KEY, "").toString();
        try {
            EthWalltUtils.sendCoin(EthSendActivity.this, mWalletAddress, etReceivedAddress.getText().toString(), pwd, etSendAmount.getText().toString(), 7000000000L, 21000, new TLCallback() {
                @Override
                public void onSuccess(Object obj) {

                    MToast.showLongToast(R.string.send_coin_success);
                }

                @Override
                public void onFail(Integer status, String error) {
                    MToast.showLongToast(R.string.send_coin_fail);
                }

                @Override
                public void onSetHex(String hex) {

                }

                @Override
                public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

                }
            });
        } catch (Exception e) {
            MToast.showLongToast(getResources().getString(R.string.password_wrong));
        }
    }


    LocalRceiver mLocalReceiver;
    IntentFilter mIntentFileter;
    private LocalBroadcastManager mLocalBroadcastManager;


    @Override
    protected void onResume() {
        register();
        super.onResume();
    }

    private void registerEthSend(){
        Web3j web3j = Web3jFactory.build(new HttpService(Common.Eth.URL));
        web3j.blockObservable(false).subscribe(new Action1<EthBlock>() {
            @Override
            public void call(EthBlock ethBlock) {
                String hash = ethBlock.getBlock().getHash();
                if(null != hash && !hash.isEmpty()){
                    //高度有增长
                }
            }
        });
    }

    private void register() {
        if (null == mLocalBroadcastManager)
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        if (null == mIntentFileter) {
            mIntentFileter = new IntentFilter();
            mIntentFileter.addAction(Common.Broadcast.SEND_ETH_RESULT); //发送以太的广播
        }

        if (null == mLocalReceiver)
            mLocalReceiver = new LocalRceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFileter);
    }

    public class LocalRceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Common.Broadcast.SEND_ETH_RESULT.equals(intent.getAction())) {
                boolean result = intent.getBooleanExtra(Common.Broadcast.SEND_ETH_RESULT_DATA, false);
                MToast.showLongToast(getResources().getString(result ? R.string.send_eth_result_success : R.string.send_eth_result_fail));
            }
        }
    }

    @Override
    protected void onPause() {
        if (null != mLocalBroadcastManager && null != mLocalReceiver) {
            mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
        }

        super.onPause();
    }
}
