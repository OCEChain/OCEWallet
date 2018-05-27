package com.oce.ocewallet.ui.fragment;

import android.content.Intent;
import android.view.View;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.BaseFragment;
import com.oce.ocewallet.ui.activity.ContactsActivity;
import com.oce.ocewallet.ui.activity.MessageCenterActivity;
import com.oce.ocewallet.ui.activity.SystemSettingActivity;
import com.oce.ocewallet.ui.activity.TradingRecordActivity;
import com.oce.ocewallet.ui.activity.WalletMangerActivity;

import butterknife.OnClick;



public class MineFragment extends BaseFragment {
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
    }

    @OnClick({R.id.lly_wallet_manage, R.id.lly_contacts, R.id.lly_msg_center
            , R.id.lly_system_setting, R.id.lly_trade_recode})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_contacts:
                intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_wallet_manage:
                intent = new Intent(getActivity(), WalletMangerActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_msg_center:
                intent = new Intent(getActivity(), MessageCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_system_setting:
                intent = new Intent(getActivity(), SystemSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_trade_recode:
                intent = new Intent(getActivity(), TradingRecordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
