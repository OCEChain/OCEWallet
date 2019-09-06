package com.idea.jgw.ui.createWallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.logic.btc.model.TLAppDelegate;
import com.idea.jgw.ui.BaseActivity;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterPath.COPY_KEY_WORDS_ACTIVITY)
public class CopyKeyWordsActivity extends BaseActivity {

    private static final int CHECK_KEY_WORDS = 11;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_create_step)
    TextView tvOfCreateStep;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.flowlayout_keywords)
    TagFlowLayout flowlayoutKeywords;

    //测试keywokds
    private String[] srcKeywords = new String[]{"angry", "teant ", "organ ", "novel ", "angle ", "hat ", "siren ", "matter ", "mechanit ", "tent ", "biology ", "husband"};
    TagAdapter keywordsTagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        flowlayoutKeywords.setEnabled(false);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_copy_key_word;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.create_wallet);
        String passphrase = BtcWalltUtils.getPassphrase();//获取助记词
        if(TextUtils.isEmpty(passphrase)){
            return;
        }else{
            srcKeywords = passphrase.split(" ");
        }

        keywordsTagAdapter = new TagAdapter<String>(srcKeywords) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(CopyKeyWordsActivity.this).inflate(R.layout.item_flow_tv,
                        flowlayoutKeywords, false);
                tv.setText(s);
                return tv;
            }

        };
        flowlayoutKeywords.setAdapter(keywordsTagAdapter);
    }

    @OnClick({R.id.btn_of_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_next:
                ARouter.getInstance().build(RouterPath.CHECK_KEY_WORDS_ACTIVITY)
                        .navigation(this, CHECK_KEY_WORDS);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == CHECK_KEY_WORDS) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
