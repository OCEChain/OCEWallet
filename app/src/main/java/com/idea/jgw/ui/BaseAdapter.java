package com.idea.jgw.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>XRecyclerView的基本适配器</p>
 * Created by dc on 2016/5/17.
 */

public abstract class BaseAdapter<T, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K> {

    List<T> mDatas = new ArrayList<>();

    protected OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    /**
     * <p>添加一个集合数据</p>
     *
     * @param datas 要添加的数据集合
     */
    public void addDatas(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * <p>添加一个数组数据</p>
     *
     * @param datas 要添加的数据集合
     */
    public void addDatas(T[] datas) {
        addDatas(Arrays.asList(datas));
    }

    /**
     * 用新的集合数据取代原有数据
     *
     * @param datas 要添加的数据集合
     */
    public void replaceDatas(List<T> datas) {
        mDatas.clear();
        addDatas(datas);
    }

    /**
     * 用新的集合数据取代原有数据
     *
     * @param datas 要添加的数据集合
     */
    public void replaceDatas(T[] datas) {
        mDatas.clear();
        addDatas(datas);
    }

    @Override
    public K onCreateViewHolder(ViewGroup parent, final int viewType) {
        return onCreate(parent, viewType);
    }


    @Override
    public void onBindViewHolder(K viewHolder, final int position) {

        final T data = mDatas.get(position);
        onBind(viewHolder, position, data);

        if (mListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(position, data);
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(K holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(holder.getLayoutPosition() == 0);
        }
    }

    public List<T> getmDatas() {
        return mDatas;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public abstract K onCreate(ViewGroup parent, final int viewType);

    public abstract void onBind(K viewHolder, int realPosition, T data);

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data);
    }
}

