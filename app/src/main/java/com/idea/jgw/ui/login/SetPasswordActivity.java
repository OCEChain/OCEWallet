package com.idea.jgw.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.LoginRequest;
import com.idea.jgw.bean.RegisterRequest;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.interfaces.StorableWallet;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.logic.ic.EncryptUtils;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.idea.jgw.ui.login.LoginActivity.EXTRA_USER;

/**
 * 设置登录密码页面（注册第二步）
 */
@Route(path = RouterPath.SET_LOGIN_PASSWORD_ACTIVITY)
public class SetPasswordActivity extends BaseActivity {

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.iBtn_of_show_pwd)
    ImageButton iBtnOfShowPwd;
    @BindView(R.id.btn_of_next)
    Button btnOfRegister;
    @BindView(R.id.et_of_pwd)
    EditText etOfPwd;

    @Autowired
    String verifyCode;
    @Autowired
    String phone;
    @Autowired
    int inviteCode;
    String pwd;
    String imei = "";
    private DisposableObserver loginSubscription;
    private DisposableObserver registerSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("phone")) {
            phone = getIntent().getStringExtra("phone");
        }
        if (getIntent().hasExtra("verifyCode")) {
            verifyCode = getIntent().getStringExtra("verifyCode");
        }
        if (getIntent().hasExtra("inviteCode")) {
            inviteCode = getIntent().getIntExtra("inviteCode", 0);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_set_password;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.set_login_pwd);
        checkPhoneStatePermission();
    }

    @OnClick({R.id.btn_of_back, R.id.iBtn_of_show_pwd, R.id.btn_of_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iBtn_of_show_pwd:
                if (iBtnOfShowPwd.isSelected()) {
                    iBtnOfShowPwd.setSelected(false);
                    CommonUtils.setTextPwdInputType(etOfPwd);
                } else {
                    iBtnOfShowPwd.setSelected(true);
                    CommonUtils.setTextInputType(etOfPwd);
                }
                break;
            case R.id.btn_of_next:
                pwd = etOfPwd.getText().toString().trim();
                if (validPassword(pwd)) {
                    RegisterRequest registerRequest = getRegisterRequest();

                    register(registerRequest);
                } else {
                    MToast.showToast(R.string.hint_of_set_pwd);
                }
                break;
        }
    }

    @Override
    public void phoneStateGranted() {
        imei = CommonUtils.getIMEI(this);
    }

    @NonNull
    public RegisterRequest getRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAccount(phone);
        registerRequest.setVerifycode(verifyCode);
        registerRequest.setInvite_num(inviteCode);
        registerRequest.setIp(CommonUtils.getIp(this));
        registerRequest.setPasswd(pwd);
        registerRequest.setDevice_id(imei);
        return registerRequest;
    }

    private void register(RegisterRequest registerRequest) {
        disposables.add(ServiceApi.getInstance().getApiService()
                .register(registerRequest.getQueryMap())
//                            .register(registerRequest.getAccount(), registerRequest.getPasswd(), registerRequest.getVerifycode())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), true) {

                    @Override
                    protected void _onNext(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                            login();
                        } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                            reLogin();
                        }
                        MToast.showToast(baseResponse.getData().toString());
                    }

                    @Override
                    protected void _onError(String message) {
                        MToast.showToast(message);
                    }
                }));
    }

    private void login() {
        String pwd = etOfPwd.getText().toString().trim();
        LoginRequest loginRequest = new LoginRequest();
        String phone1 = phone.split("-")[1];
        loginRequest.setAccount(phone1);
        loginRequest.setPasswd(pwd);

        login(loginRequest);
    }

    protected void login(final LoginRequest loginRequest) {
        disposables.add(ServiceApi.getInstance().getApiService()
                .login(loginRequest.getQueryMap())
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<BaseResponse, ObservableSource<Boolean>>() {

                    @Override
                    public ObservableSource<Boolean> apply(final BaseResponse baseResponse) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(ObservableEmitter<Boolean> observableEmitter) throws Exception {
                                if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                    App.login = true;
                                    SharedPreferenceManager.getInstance().setSession(baseResponse.getData().toString());
                                    SharedPreferenceManager.getInstance().setLogin(true);
                                    SharedPreferenceManager.getInstance().setPhone(loginRequest.getAccount());
                                    SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Constant.LOGIN_PWD, CommonUtils.hashKeyForDisk(CommonUtils.hashKeyForDisk(pwd)));
                                    SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Constant.LOGIN_PASSWORDD, pwd);
                                    boolean hasWallet = BtcWalltUtils.hasSetupHDWallet();
                                    boolean hasEthWallet = EthWalltUtils.hasEthWallet();
                                    observableEmitter.onNext(hasEthWallet);
                                    observableEmitter.onComplete();

                                } else {
                                    observableEmitter.onError(new Exception(baseResponse.getData().toString()));
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<Boolean>(SetPasswordActivity.this, getResources().getString(R.string.loading), true) {

                    @Override
                    protected void _onNext(Boolean hasWallet) {
                        if (!hasWallet) {
                            ARouter.getInstance().build(RouterPath.LOAD_OR_CREATE_WALLET_ACTIVITY)
                                    .withString(EXTRA_USER, phone)
                                    .navigation();
                        } else {
                            ARouter.getInstance().build(RouterPath.MAIN_ACTIVITY).navigation();
                        }
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    protected void _onError(String message) {
                        MToast.showToast(message);
                        ARouter.getInstance().build(RouterPath.LOGIN_ACTIVITY).navigation();
                        setResult(RESULT_OK);
                        finish();
                    }
                }));
    }

    private boolean validPassword(String pwd) {
        return !TextUtils.isEmpty(pwd) && pwd.length() > 5;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
