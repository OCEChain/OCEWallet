package com.idea.jgw.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import com.idea.jgw.App;

/**
 * Created by leafact on 2016/12/21.
 */

public class FingerManagerUtils {
    public static CancellationSignal cancellationSignal;

    public static boolean supportFingerPrint(){
        FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(App.getInstance());
        if (!managerCompat.isHardwareDetected()){ //判断设备是否支持
            return false;
        }
        KeyguardManager keyguardManager =(KeyguardManager)App.getInstance().getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {//判断设备是否处于安全保护中
            return false;
        }
        if (!managerCompat.hasEnrolledFingerprints()){ //判断设备是否已经注册过指纹
            return false;
        }
        return true;

    }

    public static  void callFingerPrint(final OnCallBackListenr listener){
        FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(App.getInstance());
        if (!managerCompat.isHardwareDetected()){ //判断设备是否支持
            if (listener != null)
                listener.onSupportFailed();
            return;
        }
        KeyguardManager keyguardManager =(KeyguardManager)App.getInstance().getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {//判断设备是否处于安全保护中
            if (listener != null)
                listener.onInsecurity();
//            return;
        }
        if (!managerCompat.hasEnrolledFingerprints()){ //判断设备是否已经注册过指纹
            if (listener != null)
                listener.onEnrollFailed(); //未注册
            return;
        }
        if (listener != null)
            listener.onAuthenticationStart(); //开始指纹识别
        cancellationSignal  = new CancellationSignal(); //必须重新实例化，否则cancel 过一次就不能再使用了
        managerCompat.authenticate(null,0,cancellationSignal,new FingerprintManagerCompat.AuthenticationCallback(){
            // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息，比如华为的提示就是：尝试次数过多，请稍后再试。
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (listener != null)
                    listener.onAuthenticationError(errMsgId ,errString );
            }

            // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
            @Override
            public void onAuthenticationFailed() {
                if (listener != null)
                    listener.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                if (listener != null)
                    listener.onAuthenticationHelp(helpMsgId,helpString);
            }

            // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                if (listener != null)
                    listener.onAuthenticationSucceeded(result);
            }
            ;
        },null);

    }

    public interface  OnCallBackListenr{
        void onSupportFailed();
        void onInsecurity();
        void onEnrollFailed();
        void onAuthenticationStart();
        void onAuthenticationError(int errMsgId, CharSequence errString);
        void onAuthenticationFailed();
        void onAuthenticationHelp(int helpMsgId, CharSequence helpString);
        void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result);
    }

    public static  void cancel(){
        if (cancellationSignal != null)
            cancellationSignal.cancel();
    }
}