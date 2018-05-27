package com.oce.ocewallet.ui.adapter;

import android.content.Context;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.CommonAdapter;
import com.oce.ocewallet.base.ViewHolder;
import com.oce.ocewallet.domain.OCEWallet;

import java.util.List;



public class WalletManagerAdapter extends CommonAdapter<OCEWallet> {
    public WalletManagerAdapter(Context context, List<OCEWallet> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, OCEWallet wallet) {
        holder.setText(R.id.tv_wallet_name,wallet.getName());
        holder.setText(R.id.tv_wallet_address,wallet.getAddress());
    }
}
