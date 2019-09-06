package com.idea.jgw.ui.createWallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.interfaces.StorableWallet;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.view.PayPsdInputView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.idea.jgw.ui.createWallet.InputKeyWordActivity.PASSPHRASE;
import static com.idea.jgw.ui.login.LoginActivity.EXTRA_USER;

//生成钱包，设置密码
public abstract class TransactionPinActivity extends BaseActivity implements PayPsdInputView.OnPasswordListener {
    private static final int LOAD_WALLET_REQUEST = 12;

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_create_step)
    TextView tvOfCreateStep;
    @BindView(R.id.piv_of_password)
    PayPsdInputView pivOfPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pivOfPassword.setOnPasswordListener(this);
        pivOfPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(pivOfPassword, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_transcation_pin;
    }

    @OnClick({R.id.btn_of_back, R.id.tv_of_forget_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_of_forget_pwd:
                ARouter.getInstance().build(RouterPath.INPUT_KEY_WORDS_ACTIVITY).navigation(this, LOAD_WALLET_REQUEST);
                break;
            case R.id.btn_of_back:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(pivOfPassword.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOAD_WALLET_REQUEST:
                    finish();
                    break;
            }
        }
    }
}
