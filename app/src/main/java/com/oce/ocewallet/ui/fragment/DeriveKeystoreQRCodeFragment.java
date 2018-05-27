package com.oce.ocewallet.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.BaseFragment;
import com.oce.ocewallet.utils.GlideImageLoader;
import com.oce.ocewallet.utils.UUi;
import com.zxing.support.library.qrcode.QRCodeEncode;

import butterknife.BindView;



public class DeriveKeystoreQRCodeFragment extends BaseFragment {
    @BindView(R.id.iv_keystore)
    ImageView ivKeystore;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_derive_keystore_qrcode;
    }

    @Override
    public void attachView() {
        Bundle arguments = getArguments();
        String walletKeystore = arguments.getString("walletKeystore");
        QRCodeEncode.Builder builder = new QRCodeEncode.Builder();
        builder.setBackgroundColor(0xffffff)
                .setOutputBitmapHeight(UUi.dip2px(270))
                .setOutputBitmapWidth(UUi.dip2px(270));
        Bitmap qrCodeBitmap = builder.build().encode(walletKeystore);
        GlideImageLoader.loadBmpImage(ivKeystore, qrCodeBitmap, -1);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }

}
