package com.idea.jgw.logic.eth.interfaces;

import okhttp3.Response;

public interface NetworkUpdateListener {

    public void onUpdate(Response s);
}
