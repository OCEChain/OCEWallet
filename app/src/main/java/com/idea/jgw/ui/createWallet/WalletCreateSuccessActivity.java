package com.idea.jgw.ui.createWallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.logic.btc.model.TLAppDelegate;
import com.idea.jgw.logic.btc.model.TLHDWalletWrapper;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.interfaces.StorableWallet;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;

import org.bitcoinj.core.Base58;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterPath.WALLET_CREATE_SUCCESS_ACTIVITY)
public class WalletCreateSuccessActivity extends BaseActivity {

    private static final int COPY_KEY_WORDS = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_create_success;
    }

    @Override
    public void initView() {

    }

    @OnClick({R.id.btn_restore_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_restore_pwd:

                if(App.isWalletDebug)
                {
                    ARouter.getInstance().build(RouterPath.MAIN_ACTIVITY).navigation();
                    return;
                }

                ARouter.getInstance().build(RouterPath.COPY_KEY_WORDS_ACTIVITY).navigation(this, COPY_KEY_WORDS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == COPY_KEY_WORDS) {
                finish();
            }
        }
    }

}
