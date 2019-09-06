package com.idea.jgw.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.request.RequestOptions;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.UserInfo;
import com.idea.jgw.ui.BaseFragment;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.DialogUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.utils.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.idea.jgw.api.OkhttpApi.BASE_HOST;
import static com.idea.jgw.ui.createWallet.CheckTransactionPinActivity.LOAD_WALLET;
import static com.idea.jgw.ui.user.VoiceInputActivity.REGISTRATION_TAG;

/**
 * <p>钱包tab</p>
 * Created by idea on 2018/5/16.
 */

public class MineFragment extends BaseFragment {

    private static final int UPDATE_INFO_REQUEST = 11;
    private static final int VOICE_INPUT = 12;

    @BindView(R.id.tv_of_name)
    TextView tvOfName;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.ll_security_manager)
    LinearLayout llSecurityManager;
    @BindView(R.id.ll_share)
    LinearLayout llShare;
    @BindView(R.id.ll_help)
    LinearLayout llHelp;
    @BindView(R.id.ll_about_us)
    LinearLayout llAboutUs;

    UserInfo userInfo;
    @BindView(R.id.iv_voice_verify)
    ImageView ivVoiceVerify;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = SharedPreferenceManager.getInstance().getSession();
        disposables.add(ServiceApi.getInstance().getApiService()
                .getinfo(token)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(getActivity(), getResources().getString(R.string.loading), true) {
                                   @Override
                                   protected void _onNext(BaseResponse baseResponse) {
                                       if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                           userInfo = JSON.parseObject(baseResponse.getData().toString(), UserInfo.class);
                                           tvOfName.setText(userInfo.getNickname());
                                           String phone = SharedPreferenceManager.getInstance().getPhone();
                                           tvPhone.setText(CommonUtils.replace(phone, "****"));
                                           if (!TextUtils.isEmpty(userInfo.getFace())) {
                                               GlideApp.with(MineFragment.this).load(BASE_HOST + userInfo.getFace()).apply(RequestOptions.circleCropTransform()).placeholder(R.mipmap.icon_default_photo).into(ivPhoto);
                                           }
                                           SharedPreferenceManager.getInstance().setInvite_code(userInfo.getInvite_num());
                                           SharedPreferenceManager.getInstance().setInvite_num(userInfo.getInvite_man_num());
                                           SharedPreferenceManager.getInstance().setInvite_url(userInfo.getInvite_url());
                                           SharedPreferenceManager.getInstance().setFace(userInfo.getFace());
                                           SharedPreferenceManager.getInstance().setNickname(userInfo.getNickname());
                                       } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                                           reLogin();
                                       } else {
                                           MToast.showToast(baseResponse.getData().toString());
                                       }
                                   }

                                   @Override
                                   protected void _onError(String message) {
                                       MToast.showToast(message);
                                   }
                               }
                ));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_mine, null);
        ButterKnife.bind(this, view);
        ivVoiceVerify.setSelected((Boolean) SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_VOICE_SWITCH, false));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userInfo != null && !SharedPreferenceManager.getInstance().getFace().equals(userInfo.getFace())) {
            GlideApp.with(MineFragment.this).load(BASE_HOST + SharedPreferenceManager.getInstance().getFace()).apply(RequestOptions.circleCropTransform()).into(ivPhoto);
            userInfo.setFace(SharedPreferenceManager.getInstance().getFace());
        }
        if (userInfo != null && !SharedPreferenceManager.getInstance().getNickname().equals(userInfo.getNickname())) {
            userInfo.setNickname(SharedPreferenceManager.getInstance().getNickname());
            tvOfName.setText(userInfo.getNickname());
        }
        ivVoiceVerify.setSelected((Boolean) SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_VOICE_SWITCH, false));
    }

    @OnClick({R.id.ll_security_manager, R.id.ll_load_wallet, R.id.ll_share, R.id.ll_help, R.id.ll_about_us, R.id.ll_feedback, R.id.iv_photo, R.id.ll_crowd, R.id.iv_voice_verify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_security_manager:
                ARouter.getInstance().build(RouterPath.SECURITY_MANAGER_ACTIVITY).navigation();
                break;
            case R.id.ll_load_wallet:
                ARouter.getInstance().build(RouterPath.CHECK_TRANSACTION_PIN_ACTIVITY).withBoolean(LOAD_WALLET, true).navigation();
                break;
            case R.id.iv_voice_verify:
                view.setSelected(!view.isSelected());
                String claimantId = SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_CLAIMANT_ID, "").toString();
                if (!TextUtils.isEmpty(claimantId)) {
                    SPreferencesHelper.getInstance(App.getInstance()).saveData(ShareKey.KEY_OF_VOICE_SWITCH, view.isSelected());
                    return;
                }
                if (!view.isSelected()) {
                    return;
                }
                ARouter.getInstance().build(RouterPath.VOICE_INPUT_ACTIVITY).withBoolean(REGISTRATION_TAG, true)
                        .navigation(getActivity(), VOICE_INPUT);
                break;
            case R.id.ll_share:
                ARouter.getInstance().build(RouterPath.SHARE_ACTIVITY).navigation();
                break;
            case R.id.ll_help:
                ARouter.getInstance().build(RouterPath.HELP_ACTIVITY).navigation();
                break;
            case R.id.ll_about_us:
                ARouter.getInstance().build(RouterPath.ABOUT_ACTIVITY).navigation();
                break;
            case R.id.ll_feedback:
                ARouter.getInstance().build(RouterPath.FEEDBACK_ACTIVITY2).navigation();
                break;
            case R.id.ll_crowd:
                DialogUtils.showAlertDialog(getActivity(), R.string.function_is_developing, null);
//                ARouter.getInstance().build(RouterPath.CROWD_ACTIVITY).navigation();
                break;
            case R.id.iv_photo:
                ARouter.getInstance().build(RouterPath.USER_INFO_ACTIVITY).withParcelable("userInfo", userInfo).navigation(getActivity(), UPDATE_INFO_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == UPDATE_INFO_REQUEST) {
                if (data.hasExtra("nickname")) {
                    String nickname = data.getStringExtra("nickname");
                    tvOfName.setText(nickname);
                }
                if (data.hasExtra("face")) {
                    String face = data.getStringExtra("face");
                    GlideApp.with(MineFragment.this).load(BASE_HOST + face).apply(RequestOptions.circleCropTransform()).into(ivPhoto);
                }
                return;
            }
            if (requestCode == VOICE_INPUT) {
                SPreferencesHelper.getInstance(App.getInstance()).saveData(ShareKey.KEY_OF_VOICE_SWITCH, ivVoiceVerify.isSelected());
            }
        } else {
            if (requestCode == VOICE_INPUT) {
                ivVoiceVerify.setSelected((Boolean) SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_VOICE_SWITCH, false));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
