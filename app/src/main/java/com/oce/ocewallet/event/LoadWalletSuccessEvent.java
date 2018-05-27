package com.oce.ocewallet.event;

import com.oce.ocewallet.domain.OCEWallet;



public class LoadWalletSuccessEvent {

    public OCEWallet getEthWallet() {
        return oceWallet;
    }

    public void setEthWallet(OCEWallet oceWallet) {
        this.oceWallet = oceWallet;
    }

    private OCEWallet oceWallet;
}
