package com.oce.ocewallet.ui.contract;

import com.oce.ocewallet.base.BaseContract;
import com.oce.ocewallet.domain.OCEWallet;



public interface CreateWalletContract extends BaseContract {

    interface View extends BaseContract.BaseView {
        void jumpToWalletBackUp(OCEWallet wallet);
    }

    interface Presenter extends BaseContract.BasePresenter {
        void createWallet(String name, String pwd, String confirmPwd, String pwdReminder);

        boolean walletNameRepeatChecking(String name);
    }
}
