package com.idea.jgw.ui.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idea.jgw.R;
import com.idea.jgw.bean.CoinData;
import com.idea.jgw.bean.CoinPrice;
import com.idea.jgw.common.Common;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.ui.BaseRecyclerAdapter;
import com.idea.jgw.ui.main.fragment.WalletFragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by idea on 2018/5/16.
 */

public class DigitalCurrencysAdapter extends BaseAdapter<CoinData, RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_detail_asset, parent, false);
        CoinDataListHolder holder = new CoinDataListHolder(view);
        return holder;
    }


    DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, CoinData data) {

        CoinDataListHolder v = (CoinDataListHolder) viewHolder;
        v.tvOfDigitalTotalPrice.setVisibility(View.INVISIBLE);
        switch (data.getCoinTypeEnum()){
            case BTC:
                v.tvOfDigitalName.setText("BTC");
                break;
            case ETH:
                v.tvOfDigitalName.setText("ETH");
                v.ivOfDigitalCurrency.setImageResource(R.mipmap.icon_eth);
                BigDecimal bd =getSumPrice(data,data.getPrice());
                v.tvOfDigitalTotalPrice.setText(bd.doubleValue()+ WalletFragment.MONEY_TYPE);
                v.tvOfDigitalTotalPrice.setVisibility(View.VISIBLE);
                break;
            case JGW:
                v.ivOfDigitalCurrency.setImageResource(R.mipmap.icon_oce);
                v.tvOfDigitalName.setText("JGW");
                break;
            case OCE:
                v.ivOfDigitalCurrency.setImageResource(R.mipmap.oce);
                v.tvOfDigitalName.setText("OCE");
                break;
        }
        v.tvOfDigitalNumber.setText(TextUtils.isEmpty(data.getCount())?"0.0":df.format(new BigDecimal(data.getCount().replace(",", "")).doubleValue()));
        v.tvOfDigitalUnitPrice.setVisibility(View.INVISIBLE);


    }


    private BigDecimal getSumPrice(CoinData cd,CoinPrice cp ){
        if(null == cp){
            return new BigDecimal("0");
        }
        String amount = cd.getCount();
        BigDecimal bd = new BigDecimal(amount);
        return  bd.multiply(new BigDecimal(cp.getLast())).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    class CoinDataListHolder extends Holder {

        @BindView(R.id.iv_of_digital_currency)
        ImageView ivOfDigitalCurrency;
        @BindView(R.id.tv_of_digital_name)
        TextView tvOfDigitalName;
        @BindView(R.id.tv_of_digital_unit_price)
        TextView tvOfDigitalUnitPrice;
        @BindView(R.id.tv_of_digital_number)
        TextView tvOfDigitalNumber;
        @BindView(R.id.tv_of_digital_total_price)
        TextView tvOfDigitalTotalPrice;

        public CoinDataListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
