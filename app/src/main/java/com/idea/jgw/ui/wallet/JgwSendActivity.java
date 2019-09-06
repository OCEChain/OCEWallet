package com.idea.jgw.ui.wallet;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.btc.model.TLCoin;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.IBAN;
import com.idea.jgw.logic.jgw.JgwUtils;
import com.idea.jgw.service.MessageEvent;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import org.greenrobot.eventbus.EventBus;
import org.web3j.utils.Numeric;

/**
 * Created by vam on 2018\6\4 0004.
 */
@Route(path = RouterPath.SEND_JGW_ACTIVITY)
public class JgwSendActivity extends SendActivity {


    public static final String EXTRA_BALANCE_KEY = "JgwSendActivity_EXTRA_BALANCE_KEY";
    public static final String EXTRA_ADDRESS_KEY = "JgwSendActivity_EXTRA_ADDRESS_KEY";


    String address;

    @Override
    public void initView() {
        super.initView();

        address = getIntent().getStringExtra(EXTRA_ADDRESS_KEY);
        String amont = getIntent().getStringExtra(EXTRA_BALANCE_KEY);
        tvOfBalance.setText(TextUtils.isEmpty(amont) ? "0.00" : amont);
        tvLight.setText("jgw");
        ivDigitalLogo.setImageResource(R.mipmap.icon_oce_small);
    }

    @Override
    protected String getSelfAddress() {
        return address;
    }

    @Override
    public void onQrScanCodeClick() {
        super.onQrScanCodeClick();
        ARouter.getInstance()
                .build(RouterPath.QR_SCAN_ACTIVITY)
                .navigation(JgwSendActivity.this, QrSanActivity.REQ_CODE);
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
//                                MToast.showLongToast(R.string.token_type_err);
//                                return;
//                            }

                            address = IBAN.IBAN2Address(iban);

                        } else if (validAddress(address)) {
                            if(address.startsWith("iban:")){
                                address = address.replace("iban:","");
                            }
                            if(IBAN.validateIBAN(address)){
                                address = IBAN.IBAN2Address(address);
                            }
                            String addressNoPrefix = Numeric.cleanHexPrefix(address);
                            if (addressNoPrefix.contains("0x")) {
                                int index = addressNoPrefix.indexOf("0x") + 2;
                                addressNoPrefix = addressNoPrefix.substring(index);
                            }

                        }
                        if(address.contains("iban:"))
                    address.replace("iban:","");
                    etReceivedAddress.setText(address);
//                    }
                    break;
            }
        }
    }

    private boolean validAddress(String address) {
        if (address.indexOf("&") > 0) {
            int index = address.lastIndexOf("&");
            address = address.substring(0, index);
        }

        if (!EthWalltUtils.isValidAddress(address)) {
            MToast.showLongToast(getResources().getString(R.string.address_wrong));
            return false;
        }
        return true;
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


    @Override
    public void onPasswordPassed() {
        try {
            JgwUtils ju = new JgwUtils();
            ju.sendCoin(address,etReceivedAddress.getText().toString(), etSendAmount.getText().toString(), new TLCallback() {
                @Override
                public void onSuccess(Object obj) {
                    MToast.showLongToast(R.string.jgw_send_coin_succes);
                    MessageEvent m = new MessageEvent(Common.CoinTypeEnum.JGW,1,"");
                    EventBus.getDefault().post(m);
                }

                @Override
                public void onFail(Integer status, String error) {
                    if(status ==-2){
                        MToast.showLongToast(R.string.jgw_send_coin_fair_not_have_eth);
                    }else{
                        MToast.showLongToast(R.string.jgw_send_coin_fair);
                    }
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

}
