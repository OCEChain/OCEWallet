package com.idea.jgw.utils.baserx;

import android.app.Activity;
import android.content.Context;

import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.dialog.LoadingDialog;
import com.idea.jgw.utils.common.CommonUtils;

import java.lang.ref.WeakReference;

import io.reactivex.Observer;
import io.reactivex.observers.DisposableObserver;

/**
 * des:订阅封装
 */


public abstract class RxSubscriber<T> extends DisposableObserver<T> {

    private Context mContext;
    private String msg;
    private boolean showDialog = true;

    /**
     * 是否显示浮动dialog
     */
    public void showDialog() {
        this.showDialog = true;
    }

    public void hideDialog() {
        this.showDialog = false;
    }

    public RxSubscriber(Context context, String msg, boolean showDialog) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        this.mContext = contextWeakReference.get();
        this.msg = msg;
        this.showDialog = showDialog;
    }

    public RxSubscriber(Context context) {
        this(context, App.getInstance().getString(R.string.loading), false);
    }

    public RxSubscriber(Context context, boolean showDialog) {
        this(context, App.getInstance().getString(R.string.loading), showDialog);
    }

    @Override
    public void onComplete() {
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (showDialog) {
            try {
                LoadingDialog.showDialogForLoading((Activity) mContext, msg, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onNext(T t) {
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
        e.printStackTrace();
        //网络
        if (!CommonUtils.isNetworkAvailable(App.getInstance())) {
            _onError(App.getInstance().getString(R.string.no_net));
        }
        //服务器
        else if (e instanceof ServerException) {
            _onError(e.getMessage());
        }
        //其它
        else {
            _onError(e.getMessage());
//            _onError(App.getInstance().getString(R.string.net_error));
        }
    }

    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);

}
