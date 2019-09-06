package com.idea.jgw.ui.mining;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.common.MToast;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 实名认证
 */
@Route(path = RouterPath.IDENTITY_AUTHENTICATION_ACTIVITY)
public class IdentityAuthenticationActivity extends BaseActivity {

    private static final int CERTIFICATION_REQUEST = 11;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_id_number)
    EditText etIdNumber;
    @BindView(R.id.btn_of_next)
    Button btnOfNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_identity_authentication;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.authentication);
    }

    @OnClick({R.id.btn_of_back, R.id.btn_of_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_of_next:
                String idNumber = etIdNumber.getText().toString().trim();
                String name = etName.getText().toString().trim();
                if(TextUtils.isEmpty(idNumber)) {
                    MToast.showToast(R.string.id_number_is_null);
                } else if(TextUtils.isEmpty(name)) {
                    MToast.showToast(R.string.name_is_null);
                } else {
                    ARouter.getInstance().build(RouterPath.IDENTITY_AUTHENTICATION_ACTIVITY2).withString("idNumber", idNumber).withString("name", name).navigation(this, CERTIFICATION_REQUEST);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == CERTIFICATION_REQUEST) {
                finish();
            }
        }
    }
}
