package com.idea.jgw.ui.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by idea on 2018/8/28.
 */

@Route(path = RouterPath.CROWD_INFO_ACTIVITY)
public class CrowdInfoActivity extends BaseActivity {
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_crowd_name)
    TextView tvOfCrowdName;
    @BindView(R.id.tv_of_crowd_des)
    TextView tvOfCrowdDes;
    @BindView(R.id.tv_crowd_value)
    TextView tvCrowdValue;
    @BindView(R.id.tv_award_value)
    TextView tvAwardValue;
    @BindView(R.id.tv_current_crowd)
    TextView tvCurrentCrowd;
    @BindView(R.id.tv_current_validity)
    TextView tvCurrentValidity;
    @BindView(R.id.tv_current_person)
    TextView tvCurrentPerson;
    @BindView(R.id.tv_crowd_level1)
    TextView tvCrowdLevel1;
    @BindView(R.id.tv_crowd_level2)
    TextView tvCrowdLevel2;
    @BindView(R.id.tv_crowd_level3)
    TextView tvCrowdLevel3;
    @BindView(R.id.tv_crowd_level4)
    TextView tvCrowdLevel4;
    @BindView(R.id.tv_crowd_sub)
    TextView tvCrowdSub;
    @BindView(R.id.tv_crowd_sum)
    TextView tvCrowdSum;
    @BindView(R.id.tv_crowd_apply)
    TextView tvCrowdApply;
    @BindView(R.id.tv_vote_level1)
    TextView tvVoteLevel1;
    @BindView(R.id.tv_vote_level2)
    TextView tvVoteLevel2;
    @BindView(R.id.tv_vote_level3)
    TextView tvVoteLevel3;
    @BindView(R.id.tv_vote_level4)
    TextView tvVoteLevel4;
    @BindView(R.id.tv_vote_sub)
    TextView tvVoteSub;
    @BindView(R.id.tv_vote_sum)
    TextView tvVoteSum;
    @BindView(R.id.tv_vote_apply)
    TextView tvVoteApply;
    @BindView(R.id.btn_support)
    Button btnSupport;

    @Override
    public int getLayoutId() {
        return R.layout.activity_crowd_info;
    }

    @Override
    public void initView() {
        tvCurrentValidity.setText(9 + getString(R.string.day));
    }

    public List getTestDatas(int size) {
        List<String> digitalCurrencys = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            digitalCurrencys.add("");
        }
        return digitalCurrencys;
    }

    @OnClick({R.id.btn_of_back, R.id.btn_support})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_support:
                ARouter.getInstance().build(RouterPath.ENSURE_CROWD_ACTIVITY).navigation();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
