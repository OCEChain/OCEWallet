package com.idea.jgw.ui.wallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.zxing.qrcode.CreateQRUtils;
import com.google.zxing.qrcode.QRCodeUtil;
import com.idea.jgw.App;
import com.idea.jgw.RouterPath;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.eth.IBAN;
import com.idea.jgw.utils.DisplayUtils;
import com.idea.jgw.utils.SPreferencesHelper;

import org.web3j.utils.Numeric;

/**
 * Created by vam on 2018\6\4 0004.
 */
@Route(path = RouterPath.RECEIVED_JGW_ACTIVITY)
public class JgwReceivedActivity extends WalletAddressActivity {


    String mCurAddress;
    String mCurAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mCurAddress = intent.getStringExtra(EXTRA_ADDRESS);
        mCurAmount = intent.getStringExtra(EXTRA_CUR_AMOUNT);

        String addressNoPrefix = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_ADDRESS_KEY, "").toString();
        if (addressNoPrefix.contains("0x")) {
            int index = addressNoPrefix.indexOf("0x");
            addressNoPrefix = addressNoPrefix.substring(index);
        }
//        addressNoPrefix = addressNoPrefix + "&token=jgw";

        final String address =  "iban:"+IBAN.getIBAN("0x"+addressNoPrefix)+"?amount=0&token=oce";


        tvSendAddress.setText(addressNoPrefix);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int height = DisplayUtils.dp2px(JgwReceivedActivity.this,162);
                try{
                    final Bitmap bitmap = QRCodeUtil.createQRImage(address, height, height,null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivOfMyAddress.setImageBitmap(bitmap);
                        }
                    });
                }catch (Exception e ){

                }
            }
        }).start();
    }
}
