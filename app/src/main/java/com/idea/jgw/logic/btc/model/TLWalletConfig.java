package com.idea.jgw.logic.btc.model;

import com.idea.jgw.App;

public class TLWalletConfig {
    public boolean isTestnet = App.debug;

    public TLWalletConfig (boolean isTestnet){
        this.isTestnet = isTestnet;
    }
}
