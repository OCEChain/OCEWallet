package com.idea.jgw.logic.eth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
public class Settings {

    public static boolean showTransactionsWithZero = false;

    public static boolean startWithWalletTab = false;

    public static boolean walletBeingGenerated = false;

    public static boolean displayAds = true;

    public static void initiate(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        showTransactionsWithZero = prefs.getBoolean("zeroAmountSwitch", false);
        startWithWalletTab = prefs.getBoolean("startAtWallet", true);
    }

}
