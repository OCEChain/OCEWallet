package com.idea.jgw.ui.mining;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.view.LocusPassWordView;

import butterknife.BindView;

/**
 * 验证手势密码页面
 */
@Route(path = RouterPath.VERIFY_GESTURE_PASSWORD_ACTIVITY)
public class VerifyGesturePasswordActivity extends BaseActivity implements LocusPassWordView.OnCompleteListener {

    @BindView(R.id.tv_verify_gesture_msg)
    TextView tvVerifyGestureMsg;
    @BindView(R.id.lpwd_gesture_pwd)
    LocusPassWordView lpwdGesturePwd;

    String gesturePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_gesture_password;
    }

    @Override
    public void initView() {
        lpwdGesturePwd.setOnCompleteListener(this);
        gesturePwd = SharedPreferenceManager.getInstance().getGesturePwd();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return true;
    }

    @Override
    public void onComplete(String password) {
        if (gesturePwd.equals(password)) {
            lpwdGesturePwd.setOnCompleteListener(null);
            finish();
        } else {
            tvVerifyGestureMsg.setText(R.string.gesture_security_error);
        }
        lpwdGesturePwd.clearPassword();
    }
}
