package com.idea.jgw.logic.btc.model;

public class TLUpdateAppData {
    private static TLUpdateAppData instance = null;

    public String beforeUpdatedAppVersion = null;

    private TLUpdateAppData() {}

    public static TLUpdateAppData instance() {
        if (instance == null) {
            instance = new TLUpdateAppData();
        }
        return instance;
    }
}
