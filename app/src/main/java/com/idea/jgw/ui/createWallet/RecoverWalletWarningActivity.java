package com.idea.jgw.ui.createWallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by idea on 2018/8/14.
 */

@Route(path = RouterPath.RECOVER_WALLET_WARNING_ACTIVITY)
public class RecoverWalletWarningActivity extends BaseActivity {
    private static final int VERIFY_PHONE = 12;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.btn_of_cancel)
    Button btnOfCancel;
    @BindView(R.id.btn_of_load_wallet)
    Button btnOfLoadWallet;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recover_wallet_warning;
    }

    @Override
    public void initView() {
        btnOfBack.setVisibility(View.GONE);
        tvOfTitle.setText(R.string.reset_transaction_pin);
    }

    @OnClick({R.id.btn_of_back, R.id.btn_of_cancel, R.id.btn_of_load_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_of_cancel:
                finish();
                break;
            case R.id.btn_of_load_wallet:
                ARouter.getInstance().build(RouterPath.LOAD_OR_CREATE_WALLET_ACTIVITY).navigation(this, VERIFY_PHONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case VERIFY_PHONE:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    }
}
