package com.oce.ocewallet.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.ViewHolder;

import java.util.List;

import butterknife.OnClick;



public class AddNewICOAdapter extends BaseAdapter {
    private Context mContext;
    private int layoutId;
    private List<String> datas;

    public AddNewICOAdapter(Context context, List<String> datas, int layoutId) {
        this.mContext = context;
        this.layoutId = layoutId;
        this.datas = datas;
    }

    @Override
    public int getCount() {
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
        if (position == 0) {
            holder.getView(R.id.lly_item).setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.setVisible(R.id.add_switch, false);
        }else{
            holder.getView(R.id.lly_item).setBackgroundColor(mContext.getResources().getColor(R.color.add_property_gray_bg_color));
            holder.setVisible(R.id.add_switch, true);
        }

        return holder.getConvertView();
    }


}
