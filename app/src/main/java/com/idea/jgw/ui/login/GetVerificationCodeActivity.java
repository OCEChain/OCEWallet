package com.idea.jgw.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MToast;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 验证码获取页面（重设登录密码时）
 */
@Route(path = RouterPath.GET_VERIFICATION_CODE_ACTIVITY)
public class GetVerificationCodeActivity extends BaseActivity {

    static final int WAIT_TIME = 60;
    private static final int RESET_PWD_REQUEST = 22;

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.et_of_nickname)
    EditText etOfPhone;
    @BindView(R.id.iBtn_of_delete)
    ImageButton iBtnOfDelete;
    @BindView(R.id.et_of_security_code)
    EditText etOfSecurityCode;
    @BindView(R.id.tv_of_get_security_code)
    TextView tvOfGetSecurityCode;
    @BindView(R.id.btn_of_next)
    Button btnOfNext;

    private String nationCode = "+86";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_get_verification_code;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.reset_login_pwd);
    }

    @OnClick({R.id.btn_of_back, R.id.iBtn_of_delete, R.id.tv_of_get_security_code, R.id.btn_of_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iBtn_of_delete:
                break;
            case R.id.tv_of_get_security_code:
//                String token = SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_SESSION, "").toString();
//                if(TextUtils.isEmpty(token)) {
//                    MToast.showToast(R.string.session_is_invalid);
//                    ARouter.getInstance().build(RouterPath.LOGIN_ACTIVITY).navigation();
//                } else {
//                    findpwdsms(token);
//                }
                String phone = etOfPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    MToast.showToast(R.string.phone_is_null);
                } else {
                    phone = nationCode + "-" + phone;
                    getSms(phone);
                }
                break;
            case R.id.btn_of_next:
                String verifyCode = etOfSecurityCode.getText().toString().trim();
                String phoneNumber = etOfPhone.getText().toString().trim();
                if (TextUtils.isEmpty(verifyCode)) {
                    MToast.showToast(R.string.verify_code_is_null);
                } else if (TextUtils.isEmpty(phoneNumber)) {
                    MToast.showToast(R.string.phone_is_null);
                } else {
                    ARouter.getInstance().build(RouterPath.RESET_LOGIN_PASSWORD_ACTIVITY).withString("verifyCode", verifyCode).withString("phone", phoneNumber).navigation(this, RESET_PWD_REQUEST);
                }
                break;
        }
    }

    private void findpwdsms(String token) {
        disposables.add(ServiceApi.getInstance().getApiService()
                .findpwdsms(token)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                       getSecurityCodeWait();
//                                   } else if(baseResponse.getCode() == 0) {
//                                       ARouter.getInstance().build(RouterPath.LOGIN_ACTIVITY).navigation();
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

    private void getSms(String phone) {
        disposables.add(ServiceApi.getInstance().getApiService()
                .sendsms(phone)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                       getSecurityCodeWait();
                                   } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
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
    Disposable disposable;

    private void getSecurityCodeWait() {
        tvOfGetSecurityCode.setClickable(false);
        tvOfGetSecurityCode.setFocusable(false);
        disposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (aLong < 60) {
                            String s = String.format(getResources().getString(R.string.resend_notice), WAIT_TIME - aLong);
                            tvOfGetSecurityCode.setText(s);
                        } else {
                            if(disposable != null && !disposable.isDisposed()) {
                                disposable.dispose();
                                disposable = null;
                            }
                            tvOfGetSecurityCode.setClickable(true);
                            tvOfGetSecurityCode.setFocusable(true);
                            tvOfGetSecurityCode.setText(R.string.send_security);
                        }
                    }
                });
        disposables.add(disposable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESET_PWD_REQUEST) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
