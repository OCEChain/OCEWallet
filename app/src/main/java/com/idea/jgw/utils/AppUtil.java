package com.idea.jgw.utils;

import android.content.Context;
import android.content.Intent;

import com.idea.jgw.ui.main.MainActivity;

import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.KeyGenerator;

public class AppUtil {

    private static AppUtil instance = null;
    private static Context context = null;

    public static final String LOGOUT_ACTION = "com.arcbit.arcbit.LOGOUT";

    private AppUtil() {
    }

    public static AppUtil getInstance(Context ctx) {
        context = ctx;
        if (instance == null) {
            instance = new AppUtil();
        }
        return instance;
    }

    public static void restartApp() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(LOGOUT_ACTION);
        context.startActivity(intent);


    }

    /**
     * 修复 SecureRandom  每次生成的时候，相同的key，产生不同的值
     *
     * http://www.bubuko.com/infodetail-2480682.html
     */
    public void applyPRNGFixes() {

//        也可以
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
//        secureRandom.setSeed("".getBytes());
//        kgen.init(128, secureRandom);

        try {
            PRNGFixes.apply();
        } catch (Exception e0) {
            //
            // some Android 4.0 devices throw an exception when PRNGFixes is re-applied
            // removing provider before apply() is a workaround
            //
            Security.removeProvider("LinuxPRNG");
            try {
                PRNGFixes.apply();
            } catch (Exception e1) {
//                TLToast.makeText(context, context.getString(R.string.cannot_use_app_on_this_device), TLToast.LENGTH_LONG, TLToast.TYPE_ERROR);
                AppUtil.restartApp();
            }
        }
    }
}