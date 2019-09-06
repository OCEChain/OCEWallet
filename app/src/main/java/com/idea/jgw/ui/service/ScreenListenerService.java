package com.idea.jgw.ui.service;

import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.ui.createWallet.CheckPasswordActivity;
import com.idea.jgw.ui.login.LoginActivity;
import com.idea.jgw.ui.login.StartActivity;
import com.idea.jgw.utils.FingerManagerUtils;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import static com.idea.jgw.ui.createWallet.CheckPasswordActivity.PRINT_FINGER;

/**
 * Created by idea on 2018/6/5.
 */

public class ScreenListenerService extends Service {
    public static final String ACTION_TOUCH_SCREEN = "action_touch_screen";
    public static final String ACTION_ONRESUME = "action_onresume";
    public static final String ACTION_RESET = "action_reset";
    static final int LOCK_SECOND = 5 * 60;
    static final int NORMAL_SLEEP = 1 * 1000;
    static final int LOCK_WAIT_SLEEP = 1 * 200;
    public static final int NOTICE_ID = 100;
    int lockScreenSecond = LOCK_SECOND;
    Thread lockScreenThread;
    boolean running;
    long sleepTime = NORMAL_SLEEP;

    BroadcastReceiver screenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(TextUtils.isEmpty(action)) {
                return;
            }
            if(intent.getAction().equals(ACTION_ONRESUME)) {
                if (lockScreenSecond < 0) {
                    verifyGesturePwd();
                }
            } else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                sleepTime = NORMAL_SLEEP;
            } else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if(verify) {
                    sleepTime = LOCK_WAIT_SLEEP;
                } else {
                    sleepTime = NORMAL_SLEEP;
                }
            } else if(intent.getAction().equals(ACTION_TOUCH_SCREEN)) {
                verify = false;
                lockScreenSecond = LOCK_SECOND;
                sleepTime = NORMAL_SLEEP;
            } else if(intent.getAction().equals(ACTION_TOUCH_SCREEN)) {
                lockScreenSecond = LOCK_SECOND;
                sleepTime = NORMAL_SLEEP;
            }
        }
    };
    private boolean verify;

    private void verifyGesturePwd() {
        if(!SharedPreferenceManager.getInstance().isLogin()) {
            return;
        }
        boolean frontApp = CommonUtils.isForeground(App.getInstance());
        if(!frontApp) {
            return;
        }
        boolean hasActivity = !App.activityStack.empty();
        if(!hasActivity) {
            return;
        }
        boolean supportFinger = FingerManagerUtils.supportFingerPrint();
        boolean frontActivity = CommonUtils.isFrontActivity(App.getInstance(), CheckPasswordActivity.class.getName());
        for(Activity activity : App.activityStack) {
            String activityName = activity.getClass().getName();
            if(activityName.equals("com.idea.jgw.ui.createWallet.CheckPasswordActivity") || activityName.equals("com.idea.jgw.ui.createWallet.CheckFingerPrintActivity")) {
                frontActivity = true;
                break;
            }
        }
        String gesturePwd = SharedPreferenceManager.getInstance().getGesturePwd();
        boolean hasWallet = EthWalltUtils.hasEthWallet();
        if(hasWallet && !frontActivity) {
            if(supportFinger) {
                ARouter.getInstance().build(RouterPath.CHECK_FINGERP_RRINT_ACTIVITY).withBoolean(PRINT_FINGER, true).navigation();
            } else {
                ARouter.getInstance().build(RouterPath.CHECK_PASSWORD_ACTIVITY).navigation();
            }
            verify = false;
            sleepTime = NORMAL_SLEEP;
            lockScreenSecond = LOCK_SECOND;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("KeepAppAlive");
            builder.setContentText("DaemonService is runing...");
            startForeground(NOTICE_ID, builder.build());
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            Intent intent = new Intent(this, CancelNoticeService.class);
            startService(intent);
        } else {
            startForeground(NOTICE_ID, new Notification());
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(ACTION_TOUCH_SCREEN);
        filter.addAction(ACTION_ONRESUME);
        filter.addAction(ACTION_RESET);
        registerReceiver(screenBroadcastReceiver, filter);

//        startLockCountDown();
    }

    private void startLockCountDown() {
//        verify = false;
        lockScreenSecond = LOCK_SECOND;
//        sleepTime = NORMAL_SLEEP;
        if(lockScreenThread == null || !lockScreenThread.isAlive()) {
            running = true;
            lockScreenThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(sleepTime == NORMAL_SLEEP) {
                            if (lockScreenSecond == 0) {
                                verify = true;
                            }
                            lockScreenSecond--;
                        }
                        if(verify) {
                            verifyGesturePwd();
                        }
                    }
                }
            });
            lockScreenThread.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        unregisterReceiver(screenBroadcastReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startLockCountDown();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
