package com.oce.ocewallet.ui.contract;

import com.oce.ocewallet.base.BaseContract;
import com.oce.ocewallet.domain.OCEWallet;


public interface LoadWalletContract extends BaseContract {
    interface View extends BaseContract.BaseView {
        void loadSuccess(OCEWallet wallet);

//        void walletAlreadyExist(String tip);
    }

    interface Presenter extends BaseContract.BasePresenter {
        void loadWalletByMnemonic(String bipPath, String mnemonic, String pwd);

        void loadWalletByKeystore(String keystore, String pwd);

        void loadWalletByPrivateKey(String privateKey, String pwd);
    }
}
