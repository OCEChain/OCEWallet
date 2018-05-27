package com.oce.ocewallet.ui.presenter;

import com.oce.ocewallet.domain.OCEWallet;
import com.oce.ocewallet.ui.contract.WalletManagerContract;
import com.oce.ocewallet.utils.WalletDaoUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class WalletManagerPresenter implements WalletManagerContract.Presenter {
    public WalletManagerPresenter(WalletManagerContract.View mView) {
        this.mView = mView;
    }

    private WalletManagerContract.View mView;

    @Override
    public void loadAllWallets() {
        Observable.create(new ObservableOnSubscribe<List<OCEWallet>>() {
            @Override
            public void subscribe(ObservableEmitter<List<OCEWallet>> e) throws Exception {
                // 将钱包信息保存到数据库
                e.onNext(WalletDaoUtils.loadAll());
//                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<OCEWallet>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<OCEWallet> oceWallets) {
                        mView.showWalletList(oceWallets);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
