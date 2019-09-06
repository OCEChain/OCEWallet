package com.idea.jgw.ui.mining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.DigitalCurrency;
import com.idea.jgw.bean.MiningCoinData;
import com.idea.jgw.bean.PageData;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.ui.mining.adapter.MiningDetailAdapter;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.utils.glide.GlideApp;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 挖矿详情页面
 */
@Route(path = RouterPath.MINING_DETAIL_ACTIVITY)
public class MiningDetailActivity extends BaseActivity implements BaseAdapter.OnItemClickListener {

    private static final int SEND_MINING = 11;
    MiningDetailAdapter miningDetailAdapter;
    @BindView(R.id.iv_of_banner)
    ImageView ivOfBanner;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.iv_digital_logo)
    ImageView ivDigitalLogo;
    @BindView(R.id.tv_balance_label)
    TextView tvBalanceLabel;
    @BindView(R.id.tv_digital_value)
    TextView tvDigitalValue;
    @BindView(R.id.tv_cny_value)
    TextView tvCnyValue;
    @BindView(R.id.btn_digital_description)
    Button btnDigitalDescription;
    @BindView(R.id.btn_of_send)
    Button btnOfSend;
    @BindView(R.id.rv_of_detail_mining)
    XRecyclerView rvOfDetailAsset;

    double balance;//余额
    String coinType = "btc";//0币种类型，1:btc ,2:eth ,3:8phc
    String coinLogo = "";//
    private int page; //请求页码
    private int count; //总数量
    private int limit; //单页数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miningDetailAdapter = new MiningDetailAdapter();
//        miningDetailAdapter.addDatas(getTestDatas(3));
        miningDetailAdapter.setOnItemClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvOfDetailAsset.setLayoutManager(layoutManager);
        rvOfDetailAsset.setAdapter(miningDetailAdapter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_mining_detail;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.mining_detail);
        tvCnyValue.setText(0 + getString(R.string.cny));

        if (getIntent().hasExtra("coinLogo")) {
            coinLogo = getIntent().getStringExtra("coinLogo");
        }
        if (getIntent().hasExtra("coinType")) {
            coinType = getIntent().getStringExtra("coinType");
        }
        if (getIntent().hasExtra("balance")) {
            balance = getIntent().getDoubleExtra("balance", 0);
        }
        tvDigitalValue.setText(String.valueOf(balance));
        GlideApp.with(this).load(coinLogo).into(ivDigitalLogo);
        rvOfDetailAsset.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 0;
                count = 0;
                getMiningDetail(false);
            }

            @Override
            public void onLoadMore() {
                getMiningDetail(false);
            }
        });
        getMiningDetail(true);
    }

    private void getMiningDetail(boolean showDialog) {
        String token = SharedPreferenceManager.getInstance().getSession();
        disposables.add(ServiceApi.getInstance().getApiService()
                .miningList(coinType, token, page)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), showDialog) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                       Type type = new TypeToken<PageData<MiningCoinData>>(){}.getType();
                                       PageData<MiningCoinData> miningCoinDatas = JSON.parseObject(baseResponse.getData().toString(), type);
                                       List<MiningCoinData> miningCoinData = miningCoinDatas.getList();
                                       if(page == 0) {
                                           if(miningCoinData != null)
                                           miningDetailAdapter.replaceDatas(miningCoinData);
                                           rvOfDetailAsset.refreshComplete();
                                       } else {
                                           if(miningCoinData != null)
                                           miningDetailAdapter.addDatas(miningCoinData);
                                           rvOfDetailAsset.loadMoreComplete();
                                       }
                                       int totalCount = 0;
                                       if(miningCoinData != null) {
                                           totalCount = miningCoinDatas.getCount();
                                           int size = miningCoinData.size();
                                           count += size;
                                       }
                                       if(totalCount > count) {
                                           rvOfDetailAsset.setNoMore(false);
                                       } else {
                                           rvOfDetailAsset.setNoMore(true);
                                           rvOfDetailAsset.getFootView().setVisibility(View.GONE);
                                       }
                                       page++;
                                   } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                                       reLogin();
                                       MToast.showToast(baseResponse.getData().toString());
                                   }
                               }

                               @Override
                               protected void _onError(String message) {
                                   MToast.showToast(message);
                               }
                           }
                ));
    }

    @Override
    public void onItemClick(int position, Object data) {
    }

    public List getTestDatas(int size) {
        List<DigitalCurrency> digitalCurrencys = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            digitalCurrencys.add(new DigitalCurrency());
        }
        return digitalCurrencys;
    }

    @OnClick({R.id.btn_of_back, R.id.btn_digital_description, R.id.btn_of_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_digital_description:
                break;
            case R.id.btn_of_send:
                ARouter.getInstance().build(RouterPath.SEND_MINING_COIN_ACTIVITY).withString("coinLogo", coinLogo).withString("coinType", coinType).withDouble("balance", balance).navigation(this, SEND_MINING);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SEND_MINING) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
