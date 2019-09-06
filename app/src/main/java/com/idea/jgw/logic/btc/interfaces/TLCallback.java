package com.idea.jgw.logic.btc.interfaces;

import com.idea.jgw.logic.btc.model.TLCoin;

public interface TLCallback {
    public void onSuccess(Object obj);
    public void onFail(Integer status, String error);
    public void onSetHex(String hex);
    public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount);
}
