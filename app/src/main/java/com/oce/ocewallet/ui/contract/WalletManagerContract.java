package com.oce.ocewallet.ui.contract;

import com.oce.ocewallet.base.BaseContract;
import com.oce.ocewallet.domain.OCEWallet;

import java.util.List;


public interface WalletManagerContract extends BaseContract {

    interface View extends BaseContract.BaseView {
        void showWalletList(List<OCEWallet> oceWallets);
    }

    interface Presenter extends BaseContract.BasePresenter {
        void loadAllWallets();
    }
}
