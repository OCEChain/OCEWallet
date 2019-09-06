package com.idea.jgw.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 意见反馈
 */
@Route(path = RouterPath.FEEDBACK_ACTIVITY2)
public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.et_feedback_content)
    EditText etFeedbackContent;
    @BindView(R.id.et_feedback_contact)
    EditText etFeedbackContact;
    @BindView(R.id.btn_of_submit)
    Button btnOfSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.feedback);
    }

    @OnClick({R.id.btn_of_back, R.id.btn_of_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_of_submit:
                String content = etFeedbackContent.getText().toString().trim();
                String contact = etFeedbackContact.getText().toString().trim();
                String token = SharedPreferenceManager.getInstance().getSession();
                if(TextUtils.isEmpty(content)) {
                    MToast.showToast(R.string.feedback_content_is_null);
//                } else if(TextUtils.isEmpty(contact)) {
//                    MToast.showToast(R.string.feedback);
                } else if(TextUtils.isEmpty(token)) {
                    MToast.showToast(R.string.session_is_invalid);
                } else {
                    feedback(content, contact, token);
                }
                break;
        }
    }

    private void feedback(String content, String contact, String token) {
        disposables.add(ServiceApi.getInstance().getApiService()
                .feedback(token, content, contact)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if(baseResponse.getCode() == BaseResponse.RESULT_OK) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
