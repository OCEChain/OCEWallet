package com.idea.jgw.ui.user;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.ui.user.adapter.CrodAdapter;
import com.idea.jgw.utils.common.DialogUtils;
import com.idea.jgw.utils.common.MToast;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by idea on 2018/8/28.
 */

@Route(path = RouterPath.CROWD_ACTIVITY)
public class CrowdActivity extends BaseActivity implements BaseAdapter.OnItemClickListener {
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.recommend)
    RadioButton recommend;
    @BindView(R.id.last)
    RadioButton last;
    @BindView(R.id.high_popularity)
    RadioButton highPopularity;
    @BindView(R.id.radio_group_button)
    RadioGroup radioGroupButton;
    @BindView(R.id.rv_of_list)
    XRecyclerView rvOfList;

    CrodAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_crowd;
    }

    @Override
    public void initView() {
        adapter = new CrodAdapter();
//        adapter.addDatas(getTestDatas(3));
        adapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvOfList.setLayoutManager(layoutManager);
        rvOfList.setAdapter(adapter);
        adapter.addDatas(getTestDatas(4));

    }

    public List getTestDatas(int size) {
        List<String> digitalCurrencys = new ArrayList<>();
        for (int i=0;i<size;i++) {
            digitalCurrencys.add("");
        }
        return digitalCurrencys;
    }

    @OnClick({R.id.btn_of_back,R.id.btn_my_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_my_order:
                break;
        }
    }

    @Override
    public void onItemClick(int position, Object data) {
        ARouter.getInstance().build(RouterPath.CROWD_INFO_ACTIVITY).navigation();
    }
}
