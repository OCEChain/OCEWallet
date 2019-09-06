package com.idea.jgw.ui.login.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idea.jgw.R;
import com.idea.jgw.ui.BaseAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by idea on 2018/5/16.
 */

public class NationCodeAdapter extends BaseAdapter<String, RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_of_nation_list, parent, false);
        DigitalCurrencyListHolder holder = new DigitalCurrencyListHolder(view);
        return holder;
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, String data) {
        String[] strings = data.split(" ");
        String nationName = "";
        String nationCode = "";
        if(strings.length == 2) {
            nationName = strings[0];
            nationCode = strings[1];
        }
        ((DigitalCurrencyListHolder) viewHolder).tvOfNationName.setText(nationName);
        ((DigitalCurrencyListHolder) viewHolder).tvOfNationCode.setText(nationCode);
        ((DigitalCurrencyListHolder) viewHolder).tvOfNationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    class DigitalCurrencyListHolder extends Holder {

        @BindView(R.id.tv_of_nation_name)
        TextView tvOfNationName;
        @BindView(R.id.tv_of_nation_code)
        TextView tvOfNationCode;

        public DigitalCurrencyListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener2 extends View.OnClickListener {
        void onItemClick(int position, String data);
    }
}
