package com.idea.jgw.ui.wallet;

import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSONObject;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.OceApi;
import com.idea.jgw.api.retrofit.OceServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.btc.model.TLCoin;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.IBAN;
import com.idea.jgw.logic.eth.data.TransactionDisplay;
import com.idea.jgw.logic.jgw.JgwUtils;
import com.idea.jgw.service.GetSendStatusService;
import com.idea.jgw.service.MessageEvent;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import org.greenrobot.eventbus.EventBus;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.idea.jgw.ui.main.fragment.WalletFragment.OCE_ADDRESS;
import static com.idea.jgw.ui.main.fragment.WalletFragment.OCE_PRIVATE_KEY;

/**
 * Created by vam on 2018\6\4 0004.
 */
@Route(path = RouterPath.SEND_OCE_ACTIVITY)
public class OceSendActivity extends SendActivity {


    public static final String EXTRA_BALANCE_KEY = "JgwSendActivity_EXTRA_BALANCE_KEY";
    public static final String EXTRA_ADDRESS_KEY = "JgwSendActivity_EXTRA_ADDRESS_KEY";


    String address;

    @Override
    public void initView() {
        super.initView();

        address = getIntent().getStringExtra(EXTRA_ADDRESS_KEY);
        String amont = getIntent().getStringExtra(EXTRA_BALANCE_KEY);
        tvOfBalance.setText(TextUtils.isEmpty(amont) ? "0.00" : amont);
        tvLight.setText("oce");
        ivDigitalLogo.setImageResource(R.mipmap.oce);
        etSendAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    protected String getSelfAddress() {
        return (String) SPreferencesHelper.getInstance(this).getData(OCE_ADDRESS, "");
    }

    @Override
    public void onQrScanCodeClick() {
        super.onQrScanCodeClick();
        ARouter.getInstance()
                .build(RouterPath.QR_SCAN_ACTIVITY)
                .navigation(OceSendActivity.this, QrSanActivity.REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case QrSanActivity.REQ_CODE:
                    String qrString = data.getExtras().getString(QrSanActivity.EXTRA_RESULT_QR);
//                    if (validAddress(qrString)) {
                    String address = data.getExtras().getString(QrSanActivity.EXTRA_RESULT_QR);
                    if (address.contains("?") && address.contains("&") && address.contains("token")) {
                        String iban = address.substring("iban:".length(), address.indexOf("?"));
                        String amount = address.substring(address.indexOf("=") + 1, address.indexOf("&"));
                        String tokenType = address.substring(address.indexOf("token") + 6);

//                            if(!tokenType.equals("jgw")){
//                                MToast.showToast(R.string.token_type_err);
//                                return;
//                            }

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
//                    }
                    break;
            }
        }
    }

    private boolean validAddress(String address) {

        return true;
    }


    @Override
    public boolean onSendClick() {
        //地址验证
        String address = etReceivedAddress.getText().toString();
        if (TextUtils.isEmpty(address)) {
            MToast.showToast(getResources().getString(R.string.address_empty));
            return false;
        }

        //是否是转给自己
        String tempAddress = address;
        if (!address.startsWith("0x")) {
            tempAddress = "0x" + address;
        }
        if (tempAddress.endsWith(getSelfAddress())) {
            MToast.showToast(getResources().getString(R.string.address_is_self));
            return false;
        }

        //金额验证
        String sendAmount = etSendAmount.getText().toString();
        if (TextUtils.isEmpty(sendAmount)) {
            MToast.showToast(getResources().getString(R.string.send_amount_empty));
            return false;
        }

        if (!validAddress(address)) {
            return false;
        }

        return true;
    }


    @Override
    public void onPasswordPassed() {
        String sendAmount = etSendAmount.getText().toString();
        String privateKey = (String) SPreferencesHelper.getInstance(this).getData(OCE_PRIVATE_KEY, "");
        String formAddress = (String) SPreferencesHelper.getInstance(this).getData(OCE_ADDRESS, "");
        String toAddress = etReceivedAddress.getText().toString();

        MToast.showToast(R.string.transferring);

        disposables.add(OceServiceApi.getInstance(OceApi.URL).getApiService()
                .tran(Integer.valueOf(sendAmount), formAddress, getString(R.string.transfer), toAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(OceSendActivity.this) {
                    @Override
                    protected void _onNext(BaseResponse response) {

                        if (response.getCode() == 1) {
                            MToast.showToast(R.string.send_sucess);

                            MessageEvent m = new MessageEvent(Common.CoinTypeEnum.OCE, 1, "");
                            EventBus.getDefault().post(m);

                            OceSendActivity.this.finish();
                        } else {
                            MToast.showToast(R.string.send_coin_fail);
                        }

                    }

                    @Override
                    protected void _onError(String message) {
                        MyLog.e("xxx", message);
                    }
                }));
    }

}
