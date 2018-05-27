package com.oce.ocewallet.ui.presenter;

import com.oce.ocewallet.domain.OCEWallet;
import com.oce.ocewallet.ui.contract.LoadWalletContract;
import com.oce.ocewallet.utils.OCEWalletUtils;
import com.oce.ocewallet.utils.WalletDaoUtils;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class LoadWalletPresenter implements LoadWalletContract.Presenter {
    public LoadWalletPresenter(LoadWalletContract.View mView) {
        this.mView = mView;
    }

    private LoadWalletContract.View mView;


    @Override
    public void loadWalletByMnemonic(final String bipPath, final String mnemonic, final String pwd) {
        Observable.create(new ObservableOnSubscribe<OCEWallet>() {
            @Override
            public void subscribe(ObservableEmitter<OCEWallet> e) throws Exception {
                OCEWallet oceWallet = OCEWalletUtils.importMnemonic(bipPath
                        , Arrays.asList(mnemonic.split(" ")), pwd);
                if (oceWallet != null) {
                    WalletDaoUtils.insertNewWallet(oceWallet);
                }
                e.onNext(oceWallet);
//                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OCEWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OCEWallet wallet) {
                        mView.loadSuccess(wallet);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void loadWalletByKeystore(final String keystore, final String pwd) {
        Observable.create(new ObservableOnSubscribe<OCEWallet>() {
            @Override
            public void subscribe(ObservableEmitter<OCEWallet> e) throws Exception {
                OCEWallet oceWallet = OCEWalletUtils.loadWalletByKeystore(keystore, pwd);
                if (oceWallet != null) {
                    WalletDaoUtils.insertNewWallet(oceWallet);
                }
                e.onNext(oceWallet);
//                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OCEWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OCEWallet wallet) {
                        mView.loadSuccess(wallet);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void loadWalletByPrivateKey(final String privateKey, final String pwd) {
        Observable.create(new ObservableOnSubscribe<OCEWallet>() {
            @Override
            public void subscribe(ObservableEmitter<OCEWallet> e) throws Exception {
                OCEWallet oceWallet = OCEWalletUtils.loadWalletByPrivateKey(privateKey, pwd);
                if (oceWallet != null) {
                    WalletDaoUtils.insertNewWallet(oceWallet);
                }
                e.onNext(oceWallet);
//                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OCEWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(OCEWallet wallet) {
                        mView.loadSuccess(wallet);
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
