package com.idea.jgw.ui.createWallet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.dialog.LoadingDialog;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.TEmpActivity;
import com.idea.jgw.ui.service.ScreenListenerService;
import com.idea.jgw.utils.FingerManagerUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.view.PayPsdInputView;

import butterknife.BindView;
import butterknife.OnClick;

//生成钱包，设置密码
@Route(path = RouterPath.CHECK_FINGERP_RRINT_ACTIVITY)
public class CheckFingerPrintActivity extends BaseActivity {

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void fingerPrinter() {
        FingerManagerUtils.callFingerPrint(new FingerManagerUtils.OnCallBackListenr() {
            AlertDialog dialog;
            @Override
            public void onSupportFailed() {
                MToast.showToast(R.string.not_support_fingerprint);
                ARouter.getInstance().build(RouterPath.CHECK_PASSWORD_ACTIVITY).navigation();
                finish();
            }

            @Override
            public void onInsecurity() {
                MToast.showToast(R.string.set_password_first);
                ARouter.getInstance().build(RouterPath.CHECK_PASSWORD_ACTIVITY).navigation();
                finish();
            }

            @Override
            public void onEnrollFailed() {
                MToast.showToast(R.string.add_fingerprint_first);
                ARouter.getInstance().build(RouterPath.CHECK_PASSWORD_ACTIVITY).navigation();
                finish();
            }

            @Override
            public void onAuthenticationStart() {
//                LoadingDialog.showDialogForLoading(CheckFingerPrintActivity.this);
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if(errMsgId != 5) {
                    ARouter.getInstance().build(RouterPath.CHECK_PASSWORD_ACTIVITY).navigation();
                    finish();
                }
                MToast.showToast(errString.toString());
            }

            @Override
            public void onAuthenticationFailed() {
                MToast.showToast(R.string.fingerprint_failed);
//                fingerPrinter();
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                MToast.showToast(helpString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                MToast.showToast(R.string.fingerprint_success);
                resetScreenListener();
                finish();
            }
        });
    }

    long lastTouchTime;

    @Override
    protected void onResume() {
        super.onResume();
        fingerPrinter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_finger_print;
    }

    @Override
    public void initView() {
        btnOfBack.setVisibility(View.GONE);
        tvOfTitle.setText(R.string.finger_print_verify);
    }

    @OnClick({R.id.btn_of_back, R.id.tv_of_pwd_verify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_of_pwd_verify:
                ARouter.getInstance().build(RouterPath.CHECK_PASSWORD_ACTIVITY).navigation();
                finish();
                break;
            case R.id.btn_of_back:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        FingerManagerUtils.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
