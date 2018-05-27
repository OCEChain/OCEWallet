package com.oce.ocewallet.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;



public class DeriveKeystoreStringFragment extends BaseFragment {
    @BindView(R.id.tv_keystore)
    TextView tvKeystore;
    @BindView(R.id.btn_copy)
    TextView btnCopy;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_derive_keystore_string;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        Bundle arguments = getArguments();
        String walletKeystore = arguments.getString("walletKeystore");
        tvKeystore.setText(walletKeystore);
    }

    @Override
    public void configViews() {

    }

    @OnClick(R.id.btn_copy)
    public void copyText() {
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        if (cm != null) {
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", tvKeystore.getText().toString());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }
        btnCopy.setText(R.string.derive_private_key_already_copy_btn);
    }

}
