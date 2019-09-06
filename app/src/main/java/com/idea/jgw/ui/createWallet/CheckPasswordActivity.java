package com.idea.jgw.ui.createWallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.common.Common;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.FingerManagerUtils;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.view.PayPsdInputView;

import butterknife.BindView;
import butterknife.OnClick;

//生成钱包，设置密码
@Route(path = RouterPath.CHECK_PASSWORD_ACTIVITY)
public class CheckPasswordActivity extends BaseActivity implements PayPsdInputView.OnPasswordListener {

    private static final int FINGER_PRINT_REQUEST = 12;
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 13;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_create_step)
    TextView tvOfCreateStep;
    @BindView(R.id.et_of_password)
    EditText etOfPassword;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.iBtn_of_show_pwd)
    ImageButton iBtnOfShowPwd;

    boolean printFinger;
    public static final String PRINT_FINGER = "finger_print";
    @BindView(R.id.btn_of_ok)
    Button btnOfOk;
    @BindView(R.id.tv_of_finger_print)
    TextView tvOfFingerPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etOfPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(etOfPassword, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
        if (getIntent().hasExtra(PRINT_FINGER)) {
            printFinger = getIntent().getBooleanExtra(PRINT_FINGER, false);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_password;
    }

    @Override
    public void initView() {
        btnOfBack.setVisibility(View.GONE);
//        tvDescription.setVisibility(View.GONE);
        tvOfTitle.setText(R.string.password_verify);
        CommonUtils.setTextPwdInputType(etOfPassword);
    }

    @OnClick({R.id.btn_of_back, R.id.tv_of_finger_print, R.id.btn_of_ok, R.id.iBtn_of_show_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_of_finger_print:
                ARouter.getInstance().build(RouterPath.CHECK_FINGERP_RRINT_ACTIVITY).navigation();
                finish();
                break;
            case R.id.btn_of_ok:
                String pws = etOfPassword.getText().toString().trim();
                pws = CommonUtils.hashKeyForDisk(CommonUtils.hashKeyForDisk(pws));
                if (TextUtils.isEmpty(pws)) {
                    MToast.showToast(R.string.pwd_code_is_null);
                } else if (pws.equals(SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Constant.LOGIN_PWD, ""))) {
                    resetScreenListener();
                    finish();
                } else {
                    MToast.showToast(R.string.pwd_pwd_invalid);
                }
                break;
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iBtn_of_show_pwd:
                if (view.isSelected()) {
                    view.setSelected(false);
                    CommonUtils.setTextPwdInputType(etOfPassword);
                } else {
                    view.setSelected(true);
                    CommonUtils.setTextInputType(etOfPassword);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(etOfPassword.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (printFinger) {
            printFinger = false;
        }
        if (FingerManagerUtils.supportFingerPrint()) {
            tvOfFingerPrint.setVisibility(View.VISIBLE);
        } else {
            tvOfFingerPrint.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FINGER_PRINT_REQUEST) {
                finish();
            } else if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
                finish();
            }
        }
    }

    @Override
    public void inputFinished(String inputPsd) {
        if (SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Constant.LOGIN_PWD, "").equals(CommonUtils.hashKeyForDisk(CommonUtils.hashKeyForDisk(inputPsd)))) {
            finish();
        } else {
            etOfPassword.setText("");
            MToast.showToast(R.string.payment_pwd_invalid);
        }
    }
}
