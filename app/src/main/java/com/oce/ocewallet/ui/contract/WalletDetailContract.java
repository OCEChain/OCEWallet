package com.oce.ocewallet.ui.contract;

import com.oce.ocewallet.base.BaseContract;
import com.oce.ocewallet.domain.OCEWallet;



public interface WalletDetailContract extends BaseContract {
    interface View extends BaseView {
        void modifySuccess();

        void modifyPwdSuccess(OCEWallet wallet);

        void showDerivePrivateKeyDialog(String privateKey);

        void showDeriveKeystore(String keystore);

        void deleteSuccess(boolean isDelete);
    }

    interface Presenter extends BasePresenter {
        void modifyWalletName(long walletId, String name);

        void modifyWalletPwd(long walletId, String walletName, String oldPassword, String newPassword);

        void deriveWalletPrivateKey(long walletId, String password);

        void deriveWalletKeystore(long walletId, String password);

        void deleteWallet(long walletId);
    }
}
