package com.idea.jgw.ui.createWallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.btc.model.TLAppDelegate;
import com.idea.jgw.logic.btc.model.TLCoin;
import com.idea.jgw.logic.btc.model.TLHDWalletWrapper;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.interfaces.StorableWallet;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.main.MainActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.view.PayPsdInputView;

import org.bitcoinj.core.Base58;
import org.bitcoinj.crypto.MnemonicCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.idea.jgw.ui.createWallet.InputKeyWordActivity.PASSPHRASE;
import static com.idea.jgw.ui.login.LoginActivity.EXTRA_USER;

//生成钱包，设置密码
@Route(path = RouterPath.SET_TRANSACTION_PIN_ACTIVITY)
public class SetTransactionPinActivity extends TransactionPinActivity {

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_create_step)
    TextView tvOfCreateStep;
    @BindView(R.id.piv_of_password)
    PayPsdInputView pivOfPassword;

    String pwd;

    String userPhone = "";
    private String passphrase;
    boolean checkPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pivOfPassword.setOnPasswordListener(this);
        userPhone = getIntent().getStringExtra(EXTRA_USER);
        if(getIntent().hasExtra(PASSPHRASE)) {
            passphrase = getIntent().getStringExtra(PASSPHRASE);
        }
    }

    @Override
    public void initView() {
        if(TextUtils.isEmpty(passphrase)) {
            tvOfTitle.setText(R.string.create_wallet);
        } else {
            tvOfTitle.setText(R.string.load_wallet);
        }
    }

    @OnClick(R.id.btn_of_back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
        }
    }

    @Override
    public void inputFinished(String inputPsd) {
        //判断2次密码是否一致，如果一致，就创建钱包信息
        if (TextUtils.isEmpty(pwd)) {
            pwd = inputPsd;
            tvOfCreateStep.setText(R.string.ensure_transaction_pin);
            pivOfPassword.cleanPsd();
        } else if (pwd.equals(inputPsd)) {

            SharedPreferenceManager.getInstance().setPaymentPwd(pwd);
            if(!TextUtils.isEmpty(passphrase)) {
                MToast.showToast(R.string.recover_wallet_wait);
                recoverWallet(passphrase);
            } else {
                cretaeEthWallet();
            }
        } else {
            MToast.showToast(R.string.input_not_equal);
            tvOfCreateStep.setText(R.string.set_transaction_pin);
            pivOfPassword.cleanPsd();
            pwd = "";
        }
    }

    private void cretaeEthWallet() {

//        ArrayList<StorableWallet> storedwallets = new ArrayList<StorableWallet>(WalletStorage.getInstance(this).get());
//        if (storedwallets.size() > 0) {
//            MToast.showToast(R.string.wallet_is_exist);
//        }

        boolean hasEthWallet = EthWalltUtils.hasEthWallet();
        if (hasEthWallet) {
            MToast.showToast(R.string.wallet_is_exist);
            return;
        }

        String  s = BtcWalltUtils.getPassphrase();
        EthWalltUtils.createEthWalletFromIC(SetTransactionPinActivity.this,s, new EthWalltUtils.CreateUalletCallback() {
            @Override
            public void onSuccess(String address) {
                ARouter.getInstance().build(RouterPath.WALLET_CREATE_SUCCESS_ACTIVITY).navigation();
                setResult(RESULT_OK);
                finish();
//                //存储pwd
//                SPreferencesHelper.getInstance(App.getInstance()).saveData(ShareKey.KEY_OF_GESTURE_PWD, pwd);
            }

            @Override
            public void onFaild() {
                MToast.showToast(R.string.wallet_build_failed);
            }
        });
    }

    private void recoverWallet(final String mnemonicPassphrase) {
        EthWalltUtils.createEthWalletFromIC(SetTransactionPinActivity.this, mnemonicPassphrase, new EthWalltUtils.CreateUalletCallback() {
            @Override
            public void onSuccess(String address) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ARouter.getInstance().build(RouterPath.MAIN_ACTIVITY).navigation();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }

            @Override
            public void onFaild() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MToast.showToast(R.string.recover_faild);
                    }
                });
            }
        });
    }
}
