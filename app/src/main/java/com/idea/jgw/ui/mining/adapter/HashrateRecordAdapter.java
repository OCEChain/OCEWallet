package com.idea.jgw.ui.mining.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idea.jgw.R;
import com.idea.jgw.bean.CaculateRecord;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.ui.BaseRecyclerAdapter;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by idea on 2018/5/16.
 */

public class HashrateRecordAdapter extends BaseAdapter<CaculateRecord, RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_hashrate_record_list, parent, false);
        DigitalCurrencyListHolder holder = new DigitalCurrencyListHolder(view);
        return holder;
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, CaculateRecord data) {
        ((DigitalCurrencyListHolder) viewHolder).tvOfNum.setText(String.valueOf(data.getNum()));
        ((DigitalCurrencyListHolder) viewHolder).tvOfDate.setText(DateUtils.longToString(data.getTime()*1000, DateUtils.DATE_STYLE1));
        int type = data.getTypeid();
        if(type == 1) {
            ((DigitalCurrencyListHolder) viewHolder).tvOfDigitalName.setText(R.string.complete_user_info);
        } else if(type == 2) {
            ((DigitalCurrencyListHolder) viewHolder).tvOfDigitalName.setText(R.string.auth);
        } else if(type == 3) {
            ((DigitalCurrencyListHolder) viewHolder).tvOfDigitalName.setText(R.string.invite_friends);
        }
    }

    class DigitalCurrencyListHolder extends Holder {

        @BindView(R.id.tv_of_digital_name)
        TextView tvOfDigitalName;
        @BindView(R.id.tv_of_date)
        TextView tvOfDate;
        @BindView(R.id.tv_of_num)
        TextView tvOfNum;

        public DigitalCurrencyListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
