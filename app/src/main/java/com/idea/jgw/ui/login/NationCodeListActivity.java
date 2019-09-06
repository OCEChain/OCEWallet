package com.idea.jgw.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.bean.Nation;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.ui.BaseRecyclerAdapter;
import com.idea.jgw.ui.login.adapter.NationCodeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 国家代码列表
 */
@Route(path = RouterPath.NATION_CODE_ACTIVITY)
public class NationCodeListActivity extends BaseActivity implements BaseAdapter.OnItemClickListener {

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.rv_of_nation_list)
    RecyclerView rvOfNationList;

    NationCodeAdapter nationCodeAdapter;
    private String nationCode = "086";
    List<Nation> nationList;
    private String nationName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nationCodeAdapter = new NationCodeAdapter();
//        nationCodeAdapter.addDatas(getTestDatas(3));
        nationCodeAdapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvOfNationList.setLayoutManager(layoutManager);
        rvOfNationList.setAdapter(nationCodeAdapter);
        String[] strings = getResources().getStringArray(R.array.nation_list);
        nationCodeAdapter.replaceDatas(strings);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_nation_list;
    }

    @Override
    public void initView() {
    }

    @Override
    public void onItemClick(int position, Object data) {
        String[] strings = data.toString().split(" ");
        if(strings.length == 2) {
            nationName = strings[0];
            nationCode = strings[1];
        }
        Intent intent = new Intent();
        intent.putExtra("nationCode", nationCode);
        intent.putExtra("nationName", nationName);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @OnClick(R.id.btn_of_back)
    public void onClick() {
        finish();
    }
}
