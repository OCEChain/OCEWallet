package com.idea.jgw.ui.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.view.LocusPassWordView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置手势密码
 */
@Route(path = RouterPath.GESTURE_PASSWORD_ACTIVITY)
public class GesturePasswordActivity extends BaseActivity implements LocusPassWordView.OnCompleteListener {

    static final int SET_GESTURE_PWD = 0;
    static final int ENSURE_GESTURE_PWD = 1;
    static final int VERIFY_GESTURE_PWD = 2;

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_gesture_msg)
    TextView tvGestureMsg;
    @BindView(R.id.lpwd_gesture_pwd)
    LocusPassWordView lpwdGesturePwd;
    
    String gesturePwd;
    int step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gesture_password;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.set_gesture_security);
        lpwdGesturePwd.setOnCompleteListener(this);

        gesturePwd = SharedPreferenceManager.getInstance().getGesturePwd();
        if(TextUtils.isEmpty(gesturePwd)) {
            step = SET_GESTURE_PWD;
            tvGestureMsg.setText(R.string.draw_gesture_security);
        } else {
            step = VERIFY_GESTURE_PWD;
            tvGestureMsg.setText(R.string.draw_exist_gesture_security);
        }
    }

    @OnClick(R.id.btn_of_back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
        }
    }

    @Override
    public void onComplete(String password) {
        switch (step) {
            case SET_GESTURE_PWD:
                gesturePwd = password;
                tvGestureMsg.setText(R.string.draw_gesture_security_again);
                step = ENSURE_GESTURE_PWD;
                break;
            case ENSURE_GESTURE_PWD:
                if (gesturePwd.equals(password)) {
                    SharedPreferenceManager.getInstance().setGesturePwd(gesturePwd);
                    MToast.showToast(R.string.set_gesture_security_success);
                    finish();
                } else {
                    MToast.showToast(R.string.second_gesture_security_error);
                    tvGestureMsg.setText(R.string.draw_gesture_security);
//                    lpwdGesturePwd.clearPassword();
                    gesturePwd = "";
                }
                break;
            case VERIFY_GESTURE_PWD:
                if (gesturePwd.equals(password)) {
                    step = SET_GESTURE_PWD;
                    tvGestureMsg.setText(R.string.draw_gesture_security);
                } else {
                    MToast.showToast(R.string.gesture_security_error);
                    tvGestureMsg.setText(R.string.draw_exist_gesture_security);
//                    lpwdGesturePwd.clearPassword();
                }
                break;
        }
        lpwdGesturePwd.clearPassword();
    }
}
