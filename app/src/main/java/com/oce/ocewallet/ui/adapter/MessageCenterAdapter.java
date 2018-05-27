package com.oce.ocewallet.ui.adapter;

import android.content.Context;

import com.oce.ocewallet.base.CommonAdapter;
import com.oce.ocewallet.base.ViewHolder;

import java.util.List;


public class MessageCenterAdapter extends CommonAdapter<String> {
    public MessageCenterAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, String s) {

    }
}
