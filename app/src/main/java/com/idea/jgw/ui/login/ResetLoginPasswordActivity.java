package com.idea.jgw.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 重置登录密码页面
 */
@Route(path = RouterPath.RESET_LOGIN_PASSWORD_ACTIVITY)
public class ResetLoginPasswordActivity extends BaseActivity {

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.et_of_pwd)
    EditText etOfPwd;
    @BindView(R.id.iBtn_of_show_pwd)
    ImageButton iBtnOfShowPwd;
    @BindView(R.id.et_of_pwd2)
    EditText etOfPwd2;
    @BindView(R.id.iBtn_of_show_pwd2)
    ImageButton iBtnOfShowPwd2;
    @BindView(R.id.btn_of_reset_now)
    Button btnOfResetNow;

    String verifyCode;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reset_login_password;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.reset_login_pwd);
        if(getIntent().hasExtra("verifyCode")) {
            verifyCode = getIntent().getStringExtra("verifyCode");
        }
        if(getIntent().hasExtra("phone")) {
            phone = getIntent().getStringExtra("phone");
        }
    }

    @OnClick({R.id.btn_of_back, R.id.iBtn_of_show_pwd, R.id.iBtn_of_show_pwd2, R.id.btn_of_reset_now})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                this.finish();
                break;
            case R.id.iBtn_of_show_pwd:
                if(iBtnOfShowPwd.isSelected()) {
                    iBtnOfShowPwd.setSelected(false);
                    CommonUtils.setTextPwdInputType(etOfPwd);
                } else {
                    iBtnOfShowPwd.setSelected(true);
                    CommonUtils.setTextInputType(etOfPwd);
                }
                break;
            case R.id.iBtn_of_show_pwd2:
                if(iBtnOfShowPwd2.isSelected()) {
                    iBtnOfShowPwd2.setSelected(false);
                    CommonUtils.setTextPwdInputType(etOfPwd2);
                } else {
                    iBtnOfShowPwd2.setSelected(true);
                    CommonUtils.setTextInputType(etOfPwd2);
                }
                break;
            case R.id.btn_of_reset_now:
                String newPwd1 = etOfPwd.getText().toString().trim();
                String newPwd2 = etOfPwd2.getText().toString().trim();
                if(TextUtils.isEmpty(newPwd1)) {
                    MToast.showToast(R.string.pwd_code_is_null);
                } else if(TextUtils.isEmpty(newPwd2) || !newPwd1.equals(newPwd2)) {
                    MToast.showToast(R.string.input_not_equal);
                } else if(!validPassword(newPwd1)) {
                    MToast.showToast(R.string.hint_of_set_pwd);
                } else {
                    findpwd(phone, newPwd1);
                }
                break;
        }
    }

    private boolean validPassword(String pwd) {
        return !TextUtils.isEmpty(pwd) && pwd.length() > 5;
    }

    private void findpwd(String phone, String newPwd) {
        disposables.add(ServiceApi.getInstance().getApiService()
                .findpwd(phone, newPwd, verifyCode)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if(baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                       setResult(RESULT_OK);
                                       finish();
                                   } else if(baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                                       reLogin();
                                   }
                                   MToast.showToast(baseResponse.getData().toString());
                               }

                               @Override
                               protected void _onError(String message) {
                                   MToast.showToast(message);
                               }
                           }
                ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
