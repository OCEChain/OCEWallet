package com.idea.jgw.ui.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idea.jgw.R;
import com.idea.jgw.bean.CoinMining;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.utils.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by idea on 2018/5/16.
 */

public class MiningAdapter extends BaseAdapter<CoinMining, RecyclerView.ViewHolder> {

    private Context context;

    public MiningAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_mining, parent, false);
        DigitalCurrencyListHolder holder = new DigitalCurrencyListHolder(view);
        return holder;
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, CoinMining data) {
//        int coinType = data.getCoin_info().getId();
//        if(coinType == 1) {
//            ((DigitalCurrencyListHolder)viewHolder).ivOfDigitalCurrency.setImageResource(R.mipmap.icon_btc_small);
//        } else if(coinType == 2) {
//            ((DigitalCurrencyListHolder)viewHolder).ivOfDigitalCurrency.setImageResource(R.mipmap.icon_eth);
//        } else if(coinType == 3) {
//            ((DigitalCurrencyListHolder)viewHolder).ivOfDigitalCurrency.setImageResource(R.mipmap.icon_oce);
//        }
        GlideApp.with(context).load(data.getCoin_info().getFace()).into(((DigitalCurrencyListHolder)viewHolder).ivOfDigitalCurrency);
        ((DigitalCurrencyListHolder)viewHolder).tvOfDigitalNumber.setText(String.valueOf(data.getBalance()));
        ((DigitalCurrencyListHolder)viewHolder).tvOfDigitalName.setText(String.valueOf(data.getCoin_info().getCharX()));
    }

    class DigitalCurrencyListHolder extends Holder {

        @BindView(R.id.iv_of_digital_currency)
        ImageView ivOfDigitalCurrency;
        @BindView(R.id.tv_of_digital_name)
        TextView tvOfDigitalName;
        @BindView(R.id.tv_of_digital_state)
        TextView tvOfDigitalState;
        @BindView(R.id.tv_of_digital_number)
        TextView tvOfDigitalNumber;
        @BindView(R.id.tv_of_digital_cny)
        TextView tvOfDigitalCny;

        public DigitalCurrencyListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
