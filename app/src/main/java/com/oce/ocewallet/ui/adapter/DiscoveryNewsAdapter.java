package com.oce.ocewallet.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.ViewHolder;

import java.util.List;



public class DiscoveryNewsAdapter extends BaseAdapter {


    private Context mContext;
    private int layoutId;
    private List<String> datas;

    public DiscoveryNewsAdapter(Context context, int layoutId, List<String> datas) {
        this.mContext = context;
        this.layoutId = layoutId;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if (datas.size() == 0) {
            layoutId = R.layout.discovery_list_item_empty;
            return 1;
        }
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
                layoutId, position);

        return holder.getConvertView();
    }


}
