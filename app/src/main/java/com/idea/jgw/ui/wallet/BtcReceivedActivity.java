package com.idea.jgw.ui.wallet;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.zxing.qrcode.CreateQRUtils;
import com.idea.jgw.RouterPath;
//import com.idea.jgw.logic.btc.model.TLAppDelegate;
//import com.idea.jgw.logic.btc.model.TLSelectedObject;


/**
 * Created by vam on 2018\6\4 0004.
 */
@Route(path = RouterPath.RECEIVED_BTC_ACTIVITY)
public class BtcReceivedActivity extends WalletAddressActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        TLAppDelegate appDelegate = TLAppDelegate.instance();
//
//
//
//        if (appDelegate != null && appDelegate.receiveSelectedObject != null) {
//
//        }
//
//        TLSelectedObject to = appDelegate.receiveSelectedObject;
//
//        appDelegate.receiveSelectedObject.getBalanceForSelectedObject();
//
//            appDelegate.receiveSelectedObject.getSelectedObject();
//
//        int receivingAddressesCount = appDelegate.receiveSelectedObject.getReceivingAddressesCount();
////        receiveAddresses = new ArrayList<>(receivingAddressesCount);
//
//        String address =null;
//        for (int i = 0; i < receivingAddressesCount; i++) {
//             address = appDelegate.receiveSelectedObject.getReceivingAddressForSelectedObject(i);
//            if (TextUtils.isEmpty(address)) {
//                break;
//            }
//            //            receiveAddresses.add(address);
//        }


//        if(TextUtils.isEmpty(address)){
//
//        }else{
//            tvSendAddress.setText(TextUtils.isEmpty(address)?"":address);
//            final int addressWeight = ivOfMyAddress.getMeasuredWidth();//图片的实际大小
//            final int adressHeight = ivOfMyAddress.getMeasuredHeight();
//            final String add = address;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    final Bitmap bitmap = CreateQRUtils.create2DCode(add, addressWeight, adressHeight);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ivOfMyAddress.setImageBitmap(bitmap);
//                        }
//                    });
//                }
//            }).start();
//        }



    }
}
