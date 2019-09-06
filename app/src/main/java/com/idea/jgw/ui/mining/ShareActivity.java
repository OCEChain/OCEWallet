package com.idea.jgw.ui.mining;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.request.RequestOptions;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.UserInfo;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.main.fragment.MineFragment;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.utils.glide.GlideApp;
import com.joker.api.Permissions4M;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.idea.jgw.api.OkhttpApi.BASE_HOST;

/**
 * 分享
 */
@Route(path = RouterPath.SHARE_ACTIVITY)
public class ShareActivity extends BaseActivity {


    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.iv_content_bg)
    ImageView ivContentBg;
    @BindView(R.id.btn_of_share)
    Button btnOfShare;
    @BindView(R.id.tv_invite_code)
    TextView tvInviteCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_share;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.share_to_friends);
        String inviteCode = SharedPreferenceManager.getInstance().getInvite_code();
        tvInviteCode.setText(inviteCode);
        if(TextUtils.isEmpty(inviteCode)) {
            getInfo();
        }
    }

    private void getInfo() {
        String token = SharedPreferenceManager.getInstance().getSession();
        disposables.add(ServiceApi.getInstance().getApiService()
                .getinfo(token)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(ShareActivity.this, getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if(baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                       UserInfo userInfo = JSON.parseObject(baseResponse.getData().toString(), UserInfo.class);
                                       String phone = SharedPreferenceManager.getInstance().getPhone();
                                       SharedPreferenceManager.getInstance().setInvite_code(userInfo.getInvite_num());
                                       SharedPreferenceManager.getInstance().setInvite_num(userInfo.getInvite_man_num());
                                       SharedPreferenceManager.getInstance().setInvite_url(userInfo.getInvite_url());
                                       SharedPreferenceManager.getInstance().setFace(userInfo.getFace());
                                       SharedPreferenceManager.getInstance().setNickname(userInfo.getNickname());
                                       tvInviteCode.setText(userInfo.getInvite_num());
                                   } else if(baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
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

    @OnClick({R.id.btn_of_back, R.id.btn_of_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_of_share:
                checkStoragePermission();
                break;
        }
    }

    @Override
    public void storageGranted() {
        share(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
