package com.oce.ocewallet.ui.contract;

import com.oce.ocewallet.base.BaseContract;
import com.oce.ocewallet.domain.OCEWallet;

import java.util.List;



public interface PropertyContract extends BaseContract {
    interface View extends BaseContract.BaseView {
        void showWallet(OCEWallet wallet);

        void switchWallet(int currentPosition, OCEWallet wallet);

        void showDrawerWallets(List<OCEWallet> oceWallets);

        void showCurrentWalletProrperty(String balance);
    }

    interface Presenter extends BaseContract.BasePresenter {
        void refreshWallet();

        void switchCurrentWallet(int position, long walletId);

        void loadAllWallets();

        void selectBalance(String walletAddress);
    }
}
