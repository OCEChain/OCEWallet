package com.oce.ocewallet.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.BaseActivity;
import com.oce.ocewallet.utils.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.OnClick;



public class CurrencyUnitSettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    TextView tvBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.iv_cny)
    ImageView ivCNY;
    @BindView(R.id.iv_usd)
    ImageView ivUSD;
    private int currencyUnit = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_currency_unit_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_language);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
    }

    @Override
    public void initDatas() {
        currencyUnit = SharedPreferencesUtil.getInstance().getInt("currencyUnit", 0);
        if (currencyUnit == 0) {
            ivCNY.setVisibility(View.VISIBLE);
            ivUSD.setVisibility(View.GONE);
        } else if (currencyUnit == 1) {
            ivCNY.setVisibility(View.GONE);
            ivUSD.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_cny, R.id.rl_usd, R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_cny:
                currencyUnit = 0;
                ivCNY.setVisibility(View.VISIBLE);
                ivUSD.setVisibility(View.GONE);
                break;
            case R.id.rl_usd:
                currencyUnit = 1;
                ivCNY.setVisibility(View.GONE);
                ivUSD.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_btn:
                SharedPreferencesUtil.getInstance().putInt("currencyUnit", currencyUnit);
                finish();
                break;
        }
    }
}
