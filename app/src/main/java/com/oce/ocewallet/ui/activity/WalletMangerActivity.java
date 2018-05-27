package com.oce.ocewallet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.BaseActivity;
import com.oce.ocewallet.domain.OCEWallet;
import com.oce.ocewallet.ui.adapter.WalletManagerAdapter;
import com.oce.ocewallet.ui.contract.WalletManagerContract;
import com.oce.ocewallet.ui.presenter.WalletManagerPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;



public class WalletMangerActivity extends BaseActivity implements WalletManagerContract.View {
    private static final int CREATE_WALLET_REQUEST = 1101;
    private static final int WALLET_DETAIL_REQUEST = 1102;
    @BindView(R.id.lv_wallet)
    ListView lvWallet;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private List<OCEWallet> walletList;
    private WalletManagerAdapter walletManagerAdapter;
    private WalletManagerContract.Presenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_manager;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.add_new_property_title);
    }

    @Override
    public void initDatas() {
        walletList = new ArrayList<>();
        mPresenter = new WalletManagerPresenter(this);
        mPresenter.loadAllWallets();
        walletManagerAdapter = new WalletManagerAdapter(this, walletList, R.layout.list_item_wallet_manager);
        lvWallet.setAdapter(walletManagerAdapter);
    }

    @Override
    public void configViews() {
        lvWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, WalletDetailActivity.class);
                OCEWallet wallet = walletList.get(position);
                intent.putExtra("walletId", wallet.getId());
                intent.putExtra("walletPwd", wallet.getPassword());
                intent.putExtra("walletAddress", wallet.getAddress());
                intent.putExtra("walletName", wallet.getName());
                intent.putExtra("walletMnemonic", wallet.getMnemonic());
                intent.putExtra("walletIsBackup", wallet.getIsBackup());
                intent.putExtra("fromList", true);
                startActivityForResult(intent, WALLET_DETAIL_REQUEST);
            }
        });
    }

    @OnClick({R.id.lly_create_wallet, R.id.lly_load_wallet})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_create_wallet:
                intent = new Intent(this, CreateWalletActivity.class);
                startActivityForResult(intent, CREATE_WALLET_REQUEST);
                break;
            case R.id.lly_load_wallet:
                intent = new Intent(this, LoadWalletActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_WALLET_REQUEST) {
            if (data != null) {
                finish();

            }
        } else if (requestCode == WALLET_DETAIL_REQUEST) {
            if (data != null) {
                mPresenter.loadAllWallets();
            }
        }
    }

    @Override
    public void showWalletList(List<OCEWallet> oceWallets) {
        walletList.clear();
        walletList.addAll(oceWallets);
        walletManagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String errorInfo) {

    }

    @Override
    public void complete() {

    }
}
