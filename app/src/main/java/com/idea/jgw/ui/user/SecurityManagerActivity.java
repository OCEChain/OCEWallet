package com.idea.jgw.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.mining.ShareActivity;
import com.idea.jgw.ui.service.ScreenListenerService;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 安全管理页面
 */
@Route(path = RouterPath.SECURITY_MANAGER_ACTIVITY)
public class SecurityManagerActivity extends BaseActivity {

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.iBtn_gesture_security)
    ImageButton iBtnGestureSecurity;
    @BindView(R.id.rl_gesture_pwd)
    RelativeLayout rlGesturePwd;
    @BindView(R.id.ll_gesture_security)
    LinearLayout llGestureSecurity;
    @BindView(R.id.ll_reset_pwd)
    LinearLayout llResetPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_security_manager;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.security_manager);
        boolean takeOnGesturePwd = SharedPreferenceManager.getInstance().isTakeOnGesturePwd();
        iBtnGestureSecurity.setSelected(takeOnGesturePwd);
    }

    @OnClick({R.id.btn_of_back, R.id.ll_gesture_security, R.id.ll_reset_pwd, R.id.iBtn_gesture_security})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iBtn_gesture_security:
                if(view.isSelected()) {
                    view.setSelected(false);
                    stopService(new Intent(this, ScreenListenerService.class));
                } else {
                    if(TextUtils.isEmpty(SharedPreferenceManager.getInstance().getGesturePwd())) {
                        MToast.showToast(R.string.set_gesture_security_first);
                    } else {
                        view.setSelected(true);
                        startService(new Intent(this, ScreenListenerService.class));
                    }
                }
                SharedPreferenceManager.getInstance().setTakeOnGesturePwd(view.isSelected());
                break;
            case R.id.ll_gesture_security:
                ARouter.getInstance().build(RouterPath.GESTURE_PASSWORD_ACTIVITY).navigation();
                break;
            case R.id.ll_reset_pwd:
                ARouter.getInstance().build(RouterPath.GET_VERIFICATION_CODE_ACTIVITY).navigation();
                break;
        }
    }
}
