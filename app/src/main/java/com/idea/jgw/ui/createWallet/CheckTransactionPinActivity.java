package com.idea.jgw.ui.createWallet;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.view.PayPsdInputView;

import butterknife.BindView;
import butterknife.OnClick;

//生成钱包，设置密码
@Route(path = RouterPath.CHECK_TRANSACTION_PIN_ACTIVITY)
public class CheckTransactionPinActivity extends TransactionPinActivity {
    public static final String LOAD_WALLET = "loadWallet";

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_create_step)
    TextView tvOfCreateStep;
    @BindView(R.id.piv_of_password)
    PayPsdInputView pivOfPassword;

    boolean loadWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra(LOAD_WALLET)) {
            loadWallet = getIntent().getBooleanExtra(LOAD_WALLET, false);
        }
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.hint_input_transaction_pin);
        tvOfCreateStep.setText(R.string.hint_input_transaction_pin);
        if(loadWallet) {
            btnOfBack.setVisibility(View.VISIBLE);
        } else {
            btnOfBack.setVisibility(View.GONE);
        }
    }

//    @OnClick(R.id.btn_of_back)
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_of_back:
//                finish();
//                break;
//        }
//    }

    @Override
    public void inputFinished(String inputPsd) {
        if(SharedPreferenceManager.getInstance().getPaymentPwd().equals(inputPsd)) {
            if(loadWallet) {
                ARouter.getInstance().build(RouterPath.INPUT_KEY_WORDS_ACTIVITY).withBoolean(LOAD_WALLET, loadWallet).navigation();
            } else {
                ARouter.getInstance().build(RouterPath.MAIN_ACTIVITY).navigation();
            }
            finish();
        } else {
            pivOfPassword.cleanPsd();
            MToast.showToast(R.string.payment_pwd_invalid);
        }
    }
}
