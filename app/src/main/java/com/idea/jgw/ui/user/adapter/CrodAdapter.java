package com.idea.jgw.ui.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idea.jgw.R;
import com.idea.jgw.bean.MiningCoinData;
import com.idea.jgw.ui.BaseAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by idea on 2018/5/16.
 */

public class CrodAdapter extends BaseAdapter<String, RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_crowd, parent, false);
        DigitalCurrencyListHolder holder = new DigitalCurrencyListHolder(view);
        return holder;
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, String data) {
    }

    class DigitalCurrencyListHolder extends Holder {

        @BindView(R.id.iv_of_logo)
        ImageView ivOfLogo;
        @BindView(R.id.tv_of_crowd_name)
        TextView tvOfCrowdName;
        @BindView(R.id.tv_of_crowd_award)
        TextView tvOfCrowdAward;
        @BindView(R.id.tv_of_crowd_level)
        TextView tvOfCrowdLevel;
        @BindView(R.id.tv_of_crowd_schedule)
        TextView tvOfCrowdSchedule;

        public DigitalCurrencyListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
