package com.oce.ocewallet.ui.adapter;

import android.content.Context;
import android.graphics.Color;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.CommonAdapter;
import com.oce.ocewallet.base.ViewHolder;
import com.oce.ocewallet.domain.OCEWallet;

import java.util.List;


public class DrawerWalletAdapter extends CommonAdapter<OCEWallet> {

    private int currentWalletPosition = 0;

    public DrawerWalletAdapter(Context context, List<OCEWallet> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setCurrentWalletPosition(int currentWalletPosition) {
        this.currentWalletPosition = currentWalletPosition;
        notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder holder, OCEWallet wallet) {
        boolean isCurrent = wallet.getIsCurrent();
        int position = holder.getPosition();
        if (isCurrent) {
            currentWalletPosition = position;
            holder.getView(R.id.lly_wallet).setBackgroundColor(mContext.getResources().getColor(R.color.item_divider_bg_color));
        } else {
            holder.getView(R.id.lly_wallet).setBackgroundColor(Color.WHITE);
        }
        holder.setText(R.id.tv_wallet_name, wallet.getName());
    }
}
