package com.oce.ocewallet.ui.presenter;

import com.oce.ocewallet.domain.OCEWallet;
import com.oce.ocewallet.ui.contract.CreateWalletContract;
import com.oce.ocewallet.utils.OCEWalletUtils;
import com.oce.ocewallet.utils.LogUtils;
import com.oce.ocewallet.utils.WalletDaoUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class CreateWalletPresenter implements CreateWalletContract.Presenter {

    public CreateWalletPresenter(CreateWalletContract.View mView) {
        this.mView = mView;
    }

    private CreateWalletContract.View mView;

    @Override
    public void createWallet(final String name, final String pwd, String confirmPwd, String pwdReminder) {
        Observable.create(new ObservableOnSubscribe<OCEWallet>() {
            @Override
            public void subscribe(ObservableEmitter<OCEWallet> e) throws Exception {
//                if (WalletDaoUtils.walletNameChecking(name)) {
                OCEWallet oceWallet = OCEWalletUtils.generateMnemonic(name, pwd);
                WalletDaoUtils.insertNewWallet(oceWallet);
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
                        mView.jumpToWalletBackUp(wallet);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError("钱包创建失败");
                        LogUtils.e("CreateWalletPresenter", e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public boolean walletNameRepeatChecking(final String name) {

        return false;
    }

}
