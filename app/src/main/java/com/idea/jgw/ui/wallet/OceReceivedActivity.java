package com.idea.jgw.ui.wallet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.zxing.qrcode.QRCodeUtil;
import com.idea.jgw.RouterPath;
import com.idea.jgw.logic.eth.IBAN;
import com.idea.jgw.utils.DisplayUtils;

/**
 * Created by vam on 2018\6\4 0004.
 */
@Route(path = RouterPath.RECEIVED_OCE_ACTIVITY)
public class OceReceivedActivity extends WalletAddressActivity {


    String mCurAddress;
    String mCurAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mCurAddress = intent.getStringExtra(EXTRA_ADDRESS);
        mCurAmount = intent.getStringExtra(EXTRA_CUR_AMOUNT);

//        String addressNoPrefix = mCurAddress;
//        if (addressNoPrefix.contains("0x")) {
//            int index = addressNoPrefix.indexOf("0x");
//            addressNoPrefix = addressNoPrefix.substring(index);
//        }
////        addressNoPrefix = addressNoPrefix + "&token=jgw";
//
//        final String address =  "iban:"+IBAN.getIBAN("0x"+addressNoPrefix)+"?amount=0&token=oce";


        tvSendAddress.setText(mCurAddress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int height = DisplayUtils.dp2px(OceReceivedActivity.this,162);
                try{
                    final Bitmap bitmap = QRCodeUtil.createQRImage(mCurAddress, height, height,null);
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
