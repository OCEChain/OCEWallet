package com.idea.jgw.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 信息展示页面
 */
@Route(path = RouterPath.SHOW_ACTIVITY)
public class InfoActivity extends BaseActivity {
    public static final int UPDATE_LOG = 0;
    public static final int HOW_TO_LOAD = 2;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_right)
    TextView tvOfRight;
    @BindView(R.id.tv_content_title)
    TextView tvContentTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;

    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_info;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.about_us);
        flag = getIntent().getIntExtra("flag", 0);
        if(flag == 1) {
            tvOfTitle.setText(R.string.custom_service);
            tvContentTitle.setText(R.string.agreement_title);
            tvContent.setText(R.string.agreement_content);
        }
    }

    @OnClick({R.id.btn_of_back, R.id.tv_of_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.tv_of_right:
                break;
        }
    }
}
