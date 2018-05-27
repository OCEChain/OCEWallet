package com.oce.ocewallet.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.BaseActivity;
import com.oce.ocewallet.utils.GlideImageLoader;
import com.oce.ocewallet.utils.ToastUtils;
import com.oce.ocewallet.utils.UUi;
import com.zxing.support.library.qrcode.QRCodeEncode;

import butterknife.BindView;
import butterknife.OnClick;



public class GatheringQRCodeActivity extends BaseActivity {

    @BindView(R.id.iv_gathering_qrcode)
    ImageView ivGatheringQrcode;
    @BindView(R.id.btn_copy_address)
    Button btnCopyAddress;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.et_gathering_money)
    EditText etGatheringMoney;
    private String walletAddress;
    private QRCodeEncode.Builder builder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_gathering_qrcode;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        walletAddress = getIntent().getStringExtra("walletAddress");
        tvWalletAddress.setText(walletAddress);
        initAddressQRCode();
    }

    private void initAddressQRCode() {
        builder = new QRCodeEncode.Builder();
        builder.setBackgroundColor(0xffffff)
                .setOutputBitmapHeight(UUi.dip2px(270))
                .setOutputBitmapWidth(UUi.dip2px(270));
        Bitmap qrCodeBitmap = builder.build().encode(walletAddress);
        GlideImageLoader.loadBmpImage(ivGatheringQrcode, qrCodeBitmap, -1);
    }

    @Override
    public void configViews() {
        etGatheringMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String gatheringMoney = etGatheringMoney.getText().toString().trim();
                Bitmap qrCodeBitmap = builder.build().encode(walletAddress);
                GlideImageLoader.loadBmpImage(ivGatheringQrcode, qrCodeBitmap, -1);
            }
        });
    }

    @OnClick({R.id.lly_back, R.id.btn_copy_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
            case R.id.btn_copy_address:
                copyWalletAddress();
                break;
        }
    }

    private void copyWalletAddress() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        if (cm != null) {
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", walletAddress);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }
        ToastUtils.showToast(R.string.gathering_qrcode_copy_success);
        btnCopyAddress.setText(R.string.gathering_qrcode_copy_success);
    }
}
