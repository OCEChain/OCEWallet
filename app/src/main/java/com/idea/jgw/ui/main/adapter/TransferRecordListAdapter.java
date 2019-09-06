package com.idea.jgw.ui.main.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.eth.data.TransactionDisplay;
import com.idea.jgw.logic.eth.utils.AddressNameConverter;
import com.idea.jgw.logic.eth.utils.Settings;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.ui.BaseRecyclerAdapter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by idea on 2018/5/16.
 */

public class TransferRecordListAdapter extends BaseAdapter {


    private static final int CONTENT = 0;
    private static final int AD = 1;
    private static final int LIST_AD_DELTA = 9;

    List<TransactionDisplay> boxlist;


    public static int calculateBoxPosition(int position) {
        if (!Settings.displayAds) return position;
        if (position < LIST_AD_DELTA) return position - 1;
        return position - (position / LIST_AD_DELTA) - 1;
    }

    /**
     * 用新的集合数据取代原有数据
     *
     * @param datas 要添加的数据集合
     */
    public void replaceData(List<TransactionDisplay> datas) {
        getmDatas().clear();
        boxlist = datas;
        addDatas(datas);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_transfer_record, parent, false);
        DigitalCurrencyListHolder holder = new DigitalCurrencyListHolder(view);

        df.setMaximumFractionDigits(18);

        return holder;
    }


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    BigDecimal bd = new BigDecimal(10).pow(18);
    DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();


    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, Object data) {

        TransactionDisplay box = boxlist.get(realPosition);
        DigitalCurrencyListHolder v = (DigitalCurrencyListHolder) viewHolder;
        v.ivOfDigitalCurrency.setImageResource(box.getAmount() > 0 ? R.mipmap.banlance_receive : R.mipmap.banlance_send);
        String toName = AddressNameConverter.getInstance(App.getInstance()).get(box.getToAddress());
        v.tvOfTransferAddress.setText(toName == null ? box.getToAddress() : toName + " (" + box.getToAddress().substring(0, 10) + ")");
        v.tvOfTransferTime.setText(sdf.format(new Date(box.getDate())));
        if (box.getConfirmationStatus() < 13) {
            v.ivOfTransferState.setVisibility(View.VISIBLE);
            v.ivOfTransferState.setImageResource(R.mipmap.send_wait);
        } else {
            v.ivOfTransferState.setVisibility(View.VISIBLE);
            v.ivOfTransferState.setImageResource(R.mipmap.send_success);
        }
        if(box.isError()) {
            v.ivOfTransferState.setVisibility(View.VISIBLE);
            v.ivOfTransferState.setImageResource(R.mipmap.icon_send_failed);
        }
        BigDecimal amount = new BigDecimal(box.getAmountNative()).divide(bd);
        if(amount.doubleValue() > 0){
            v.tvOfTransferValue.setText( "+"+df.format(amount.doubleValue()));
        }else{
            v.tvOfTransferValue.setText(df.format(amount.doubleValue()));
        }


        Common.CoinTypeEnum coinType = box.getCoinType();
        switch (coinType) {
            case JGW:
                v.ivOfDigitalCurrency.setImageResource(R.mipmap.icon_oce_small);
                break;
            case ETH: v.ivOfDigitalCurrency.setImageResource(R.mipmap.icon_eth);
                break;

            case BTC:
                v.ivOfDigitalCurrency.setImageResource(R.mipmap.icon_btc_small);
                break;
        }
    }

    @NonNull
    public String getBigDecimalText(BigDecimal amount) {
        return String.valueOf(amount.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
    }

    class DigitalCurrencyListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_of_digital_currency)
        public ImageView ivOfDigitalCurrency;
        @BindView(R.id.tv_of_transfer_value)
        public TextView tvOfTransferValue;
        @BindView(R.id.tv_of_transfer_cny)
        public TextView tvOfTransferAddress;
        @BindView(R.id.tv_of_transfer_time)
        public TextView tvOfTransferTime;
        @BindView(R.id.iv_of_transfer_state)
        public ImageView ivOfTransferState;

        public DigitalCurrencyListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
