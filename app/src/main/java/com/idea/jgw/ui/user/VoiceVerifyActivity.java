package com.idea.jgw.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.ShareKey;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 语音验证
 */
@Route(path = RouterPath.VOICE_VERIFY_ACTIVITY)
public class VoiceVerifyActivity extends BaseActivity {


    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_phrase_1)
    TextView tvPhrase1;
    @BindView(R.id.tv_phrase_2)
    TextView tvPhrase2;
    @BindView(R.id.tv_phrase_3)
    TextView tvPhrase3;
    @BindView(R.id.tv_phrase_4)
    TextView tvPhrase4;
    @BindView(R.id.btn_of_input)
    Button btnOfInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_voice_verify;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.voice_verify);
    }

    @OnClick({R.id.btn_of_back, R.id.btn_of_input})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                break;
            case R.id.btn_of_input:
                break;
        }
    }
}
