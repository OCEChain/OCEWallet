package com.idea.jgw.ui.user;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by idea on 2018/8/28.
 */

@Route(path = RouterPath.ENSURE_CROWD_ACTIVITY)
public class EnsureCrowdCrowdActivity extends BaseActivity {
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ensure_crowd_vote;
    }

    @Override
    public void initView() {

    }

    public List getTestDatas(int size) {
        List<String> digitalCurrencys = new ArrayList<>();
        for (int i=0;i<size;i++) {
            digitalCurrencys.add("");
        }
        return digitalCurrencys;
    }

    @OnClick({R.id.btn_of_back,R.id.btn_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_ensure:
                break;
        }
    }

}
