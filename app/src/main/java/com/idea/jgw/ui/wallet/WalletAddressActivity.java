package com.idea.jgw.ui.wallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.common.MToast;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 钱包地址显示页面
 */
@Route(path = RouterPath.WALLET_ADDRESS_ACTIVITY)
public class WalletAddressActivity extends BaseActivity implements View.OnClickListener {


    /***地址*/
    public static final String EXTRA_ADDRESS = "EthReceivedActivity.EXTRA_ADDRESS";
    /***金额*/
    public static final String EXTRA_CUR_AMOUNT = "EthReceivedActivity.EXTRA_CUR_AMOUNT";

    /*** 请求code*/
    public static final int REQ_CODE = 234 * 54;

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_send_address_label)
    TextView tvSendAddressLabel;
    @BindView(R.id.tv_send_address)
    TextView tvSendAddress;
    @BindView(R.id.tv_copy_send_address)
    TextView tvCopySendAddress;
    @BindView(R.id.rl_of_address)
    RelativeLayout rlOfAddress;
    @BindView(R.id.iv_of_my_address)
    ImageView ivOfMyAddress;
    @BindView(R.id.share_address)
    Button shareAddress;

    ClipboardManager cm;  //获取剪贴板管理器：

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_address;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.sample_myAddress);
    }

    @OnClick({R.id.btn_of_back, R.id.share_address, R.id.tv_copy_send_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy_send_address:
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", tvSendAddress.getText().toString());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                MToast.showLongToast(getResources().getString(R.string.clip_data_success));
                break;
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.share_address:
                checkStoragePermission();
                break;
        }
    }

    @Override
    public void storageGranted() {
        share(this);
    }
}
