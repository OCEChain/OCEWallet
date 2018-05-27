package com.oce.ocewallet.ui.contract;

import com.oce.ocewallet.base.BaseContract;


public interface PropertyDetailContract extends BaseContract {
    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter {
        void etcTransfer(String fromAddress,String toAddress, String transferAmount, String miningCost);

        void etcTransferCustomMiningCost(String fromAddress,String toAddress, String transferAmount
                , String gasPrice, String gas);
    }
}
