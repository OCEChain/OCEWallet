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
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.LoginRequest;
import com.idea.jgw.common.Common;
import com.idea.jgw.dialog.ChooseLoginTypeDialog;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@Route(path = RouterPath.LOGIN_ACTIVITY)
public class LoginActivity extends BaseActivity implements ChooseLoginTypeDialog.OnChooseListener {

    private static final int VOICE_VERIFY = 1;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.et_of_nickname)
    EditText etOfPhone;
    @BindView(R.id.iBtn_of_delete)
    ImageButton iBtnOfDelete;
    @BindView(R.id.et_of_pwd)
    EditText etOfPwd;
    @BindView(R.id.iBtn_of_show_pwd)
    ImageButton iBtnOfShowPwd;
    @BindView(R.id.tv_of_forget_pwd)
    TextView tvOfForgetPwd;
    @BindView(R.id.tv_of_register)
    TextView tvOfRegister;
    @BindView(R.id.btn_of_login)
    Button btnOfLogin;

    public static final String EXTRA_USER = "LoginActivity_EXTRA_USER";
    ChooseLoginTypeDialog chooseLoginTypeDialog;
    @BindView(R.id.tv_of_choose_login_type)
    TextView tvOfChooseLoginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        btnOfBack.setVisibility(View.GONE);
        tvOfTitle.setText(R.string.login);
        CommonUtils.setTextPwdInputType(etOfPwd);
        etOfPhone.setText(SharedPreferenceManager.getInstance().getPhone());
        if (App.test) {
            etOfPhone.setText("15196638740");
            etOfPwd.setText("12345678");
        }
        etOfPhone.setSelection(etOfPhone.getText().length());
        if (App.isWalletDebug) {
            etOfPhone.setText("18681591321");
            etOfPwd.setText("baxxasn123");
        }
        boolean voiceVerify = (boolean) SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_VOICE_SWITCH, false);
        if (!voiceVerify || TextUtils.isEmpty(SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_CLAIMANT_ID, "").toString())) {
            return;
        }
        tvOfChooseLoginType.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.btn_of_back, R.id.iBtn_of_delete, R.id.iBtn_of_show_pwd, R.id.tv_of_forget_pwd, R.id.tv_of_register, R.id.btn_of_login, R.id.tv_of_choose_login_type})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iBtn_of_delete:
                etOfPhone.setText("");
                break;
            case R.id.iBtn_of_show_pwd:
                if (view.isSelected()) {
                    view.setSelected(false);
                    CommonUtils.setTextPwdInputType(etOfPwd);
                } else {
                    view.setSelected(true);
                    CommonUtils.setTextInputType(etOfPwd);
                }
                break;
            case R.id.tv_of_forget_pwd:
                ARouter.getInstance().build(RouterPath.GET_VERIFICATION_CODE_ACTIVITY).navigation();
                break;
            case R.id.tv_of_register:
                ARouter.getInstance().build(RouterPath.REGISTER_ACTIVITY).navigation();
                finish();
                break;
            case R.id.tv_of_choose_login_type:
                if (TextUtils.isEmpty(etOfPhone.getText().toString().trim())) {
                    MToast.showToast(R.string.phone_is_null);
                    return;
                }
                showLoginType();
                break;
            case R.id.btn_of_login:


                if (App.isIsWalletDebug2) {
                    ARouter.getInstance().build(RouterPath.MAIN_ACTIVITY)
                            .withString(EXTRA_USER, "18681591321")
                            .navigation();

                    SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_ADDRESS_KEY, "0x339b66306381b81d9dc15771059a559e5ecb838e");

                    return;
                }

                if (App.isWalletDebug) {
                    ARouter.getInstance().build(RouterPath.LOAD_OR_CREATE_WALLET_ACTIVITY)
                            .withString(EXTRA_USER, "18681591321")
                            .navigation();
                    return;
                }

                final String phone = etOfPhone.getText().toString().trim();
                String pwd = etOfPwd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    MToast.showToast(R.string.phone_is_null);
                } else if (TextUtils.isEmpty(pwd)) {
                    MToast.showToast(R.string.pwd_code_is_null);
                } else {

                    login(phone, pwd);
                }
                break;
        }
    }

    private void showLoginType() {
        if (chooseLoginTypeDialog == null) {
            chooseLoginTypeDialog = new ChooseLoginTypeDialog(this, this);
        }
        chooseLoginTypeDialog.show();
    }

    public void login(final String phone, final String pwd) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount(phone);
        loginRequest.setPasswd(pwd);
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
                                    SharedPreferenceManager.getInstance().setPhone(phone);
                                    SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Constant.LOGIN_PWD, CommonUtils.hashKeyForDisk(CommonUtils.hashKeyForDisk(pwd)));
                                    SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Constant.LOGIN_PASSWORDD, pwd);
                                    boolean hasWallet = BtcWalltUtils.hasSetupHDWallet();
                                    boolean hasEthWallet = EthWalltUtils.hasEthWallet();
                                    observableEmitter.onNext(hasEthWallet);
                                    observableEmitter.onComplete();

                                } else {
//                                                MToast.showToast(baseResponse.getData().toString());
                                    observableEmitter.onError(new Exception(baseResponse.getData().toString()));
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<Boolean>(this, getResources().getString(R.string.loading), true) {
                                   @Override
                                   protected void _onNext(Boolean hasWallet) {
                                       if (!hasWallet) {
                                           ARouter.getInstance().build(RouterPath.LOAD_OR_CREATE_WALLET_ACTIVITY)
                                                   .withString(EXTRA_USER, phone)
                                                   .navigation();
                                       } else {
//                                       ARouter.getInstance().build(RouterPath.CHECK_TRANSACTION_PIN_ACTIVITY).navigation();
                                           ARouter.getInstance().build(RouterPath.MAIN_ACTIVITY).navigation();
                                           finish();
                                       }
                                       finish();
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

    @Override
    public void choose(int which) {
        switch (which) {
            case ChooseLoginTypeDialog.CANCEL:
                chooseLoginTypeDialog.dismiss();
                break;
            case ChooseLoginTypeDialog.VOICE_LOGIN:
                chooseLoginTypeDialog.dismiss();
                boolean voiceVerify = (boolean) SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_VOICE_SWITCH, false);
                if (voiceVerify) {
                    ARouter.getInstance().build(RouterPath.VOICE_INPUT_ACTIVITY).navigation(this, VOICE_VERIFY);
                    return;
                }
                break;
            case ChooseLoginTypeDialog.PWD_LOGIN:
                chooseLoginTypeDialog.dismiss();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == VOICE_VERIFY) {
            String phone = etOfPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                MToast.showToast(R.string.phone_is_null);
                return;
            }
            String pwd = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Constant.LOGIN_PASSWORDD, "").toString();
            login(phone, pwd);
        }
    }
}
