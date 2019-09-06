package com.idea.jgw.ui.user;

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
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rx.Subscription;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = RouterPath.NIKENAME_ACTIVITY2)
public class UpdateNicknameActivity extends BaseActivity {

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.et_of_nickname)
    EditText etOfNickname;
    @BindView(R.id.iBtn_of_delete)
    ImageButton iBtnOfDelete;
    @BindView(R.id.btn_of_update)
    Button btnOfUpdate;

    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_update_nickname;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.nickename);
        if (getIntent().hasExtra("nickname")) {
            nickname = getIntent().getStringExtra("nickname");
            etOfNickname.setText(nickname);
        }
    }

    @OnClick({R.id.btn_of_back, R.id.iBtn_of_delete, R.id.btn_of_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iBtn_of_delete:
                etOfNickname.setText("");
                break;
            case R.id.btn_of_update:
                String token = SharedPreferenceManager.getInstance().getSession();
                String nickname = etOfNickname.getText().toString().trim();
                if (TextUtils.isEmpty(nickname)) {
                    MToast.showToast(R.string.nickename_is_null);
                } else if (TextUtils.isEmpty(token)) {
                    App.finishAllActivity();
                    ARouter.getInstance().build(RouterPath.LOGIN_ACTIVITY).navigation();
                    MToast.showToast(R.string.session_is_invalid);
                } else {
                    updateNickname(token, nickname);
                }
                break;
        }
    }

    private void updateNickname(String token, final String nickname) {
        int sex = 0;//暂时没有设置性别的ui界面
        disposables.add(ServiceApi.getInstance().getApiService()
                .editinfo(token, nickname, sex)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                       Intent intent = new Intent();
                                       intent.putExtra("nickname", nickname);
                                       setResult(RESULT_OK, intent);
                                       finish();
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
}
