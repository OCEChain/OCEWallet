package com.idea.jgw.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idea.jgw.R;

/**
 * description:弹窗浮动加载进度条
 * Created by xsf
 * on 2016.07.17:22
 */
public class LoadingDialog {
    /** 加载数据对话框 */
    private static Dialog mLoadingDialog;
    private static AnimationDrawable animationDrawable;
    /**
     * 显示加载对话框
     * @param context 上下文
     * @param msg 对话框显示内容
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading(Activity context, String msg, boolean cancelable) {
        if(mLoadingDialog != null) {
            return mLoadingDialog;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView loadingText = (TextView)view.findViewById(R.id.id_tv_loading_dialog_text);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_loading);
        loadingText.setText(msg);
        animationDrawable = (AnimationDrawable) imageView.getBackground();

        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mLoadingDialog.show();
        if(animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
        return  mLoadingDialog;
    }

    public static Dialog showDialogForLoading(Activity context, String msg) {
        return showDialogForLoading(context, msg, true);
    }

    public static Dialog showDialogForLoading(Activity context) {
        return showDialogForLoading(context, "加载中...");
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelDialogForLoading() {
        if(animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
            animationDrawable = null;
        }
        if(mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
    }
}
