package com.idea.jgw.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.BaseCallback;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.dialog.ChooseDialog;
import com.idea.jgw.ui.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 帮助中心
 */
@Route(path = RouterPath.HELP_ACTIVITY)
public class HelpActivity extends BaseActivity implements BaseCallback {

    private static final int CUSTOMER_SERVICE = 11;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_right)
    TextView tvOfRight;
    @BindView(R.id.ll_how_to_load)
    LinearLayout llHowToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_help;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.help);
    }

    @OnClick({R.id.btn_of_back, R.id.tv_of_right, R.id.ll_how_to_load, R.id.ll_custom_service, R.id.ll_how_to_load_oce})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.ll_how_to_load_oce:
                ARouter.getInstance().build(RouterPath.LOAD_OCE).navigation();
                break;
            case R.id.tv_of_right:
                showCustomerServiceDialog();
                break;
            case R.id.ll_how_to_load:
                ARouter.getInstance().build(RouterPath.SHOW_ACTIVITY).withInt("contentType", InfoActivity.HOW_TO_LOAD).navigation();
                break;
            case R.id.ll_custom_service:
                ARouter.getInstance().build(RouterPath.SHOW_ACTIVITY).withInt("flag", 1).navigation();
                break;
        }
    }

    private void showCustomerServiceDialog() {
        ChooseDialog chooseDialog = new ChooseDialog(this, CUSTOMER_SERVICE, this);
        chooseDialog.show();
    }

    @Override
    public void callback(Object... parameters) {
        if (parameters.length > 1) {
            int requestCode = Integer.parseInt(parameters[0].toString().trim());
            int resultCode = Integer.parseInt(parameters[1].toString().trim());
            if (requestCode == CUSTOMER_SERVICE) {
                if (resultCode == ChooseDialog.SELECTED_OK) {
                    String jaw_service = "4000888888";
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + jaw_service));//跳转到拨号界面，同时传递电话号码
                    startActivity(dialIntent);
                }
            }
        }
    }

}
