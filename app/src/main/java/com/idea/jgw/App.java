package com.idea.jgw;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.voicevault.vvlibrary.ViGoLibrary;

import java.util.Stack;

//import cn.smssdk.SMSSDK;

/**
 * 当前Application
 * Created by 9 on 2016/5/21.
 */

public class App extends Application {
    public static App mApp;

    public static App getInstance() {
        return mApp;
    }

    public static Stack<Activity> activityStack;//App所有页面堆栈

    //开关类
    public static boolean debug = true;  //是否测试
    public static String APP_KEY = "1ac3660a73e00";
    public static String APP_SECRET = "28051a43a5283acea68e0e13b0b4e76c";


    //是否已登录状态，默认false，即app被杀死后需要重新验证登录
    public static boolean login = false;
    //测试IP
    public static boolean testIP = true;
    //功能测试
    public static boolean test = false;
    //钱包测试
    public static boolean isWalletDebug = false;

    //钱包测试，直接跳转到首页
    public static boolean isIsWalletDebug2 = false;

    /* VIGO 参数*/
    private final static String VIGO_SERVER_URL = "https://a10i1.voicevault.net/FusionApi/";

    // ViGo Application ID
    private final static String VIGO_APP_ID = "b70f8231-7d52-4b12-96b1-3a2bf9be7b3f";
//    private final static String VIGO_APP_ID = "LMH BlockChain";

    // ViGo application credentials
    private final static String VIGO_CREDENTIAL_ID = "DFSSFeBHWJRY4VksCS5b";
    private final static String VIGO_CREDENTIAL_PWD = "XPgk4GanqHXcgFtdBJ6w8ZZjsTMvVt";

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        MyLog.setDebug(debug);

        //bug收集
//        CrashReport.initCrashReport(this, "5c69a04d04", debug);

        //初始化自定义全局异常捕捉器
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());

        BtcWalltUtils.init();

        if (debug) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//            initLeakcanary(this);
        }
        ARouter.init(this); // 尽可能早，推荐在Application中初始化

        //VigoLibrary 初始化
        ViGoLibrary.getInstance().init(
                VIGO_CREDENTIAL_ID,
                VIGO_CREDENTIAL_PWD,
                VIGO_SERVER_URL,
                VIGO_APP_ID);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    // 把一个activity压入栈中
    public static void pushOneActivity(Activity actvity) {
        if (App.activityStack == null) {
            App.activityStack = new Stack<Activity>();
        }
        App.activityStack.add(actvity);
    }

    // 移除一个activity
    public static void popOneActivity(Activity activity) {
        if (App.activityStack != null && App.activityStack.size() > 0) {
            if (activity != null) {
                activity.finish();
                App.activityStack.remove(activity);
                activity = null;
            }
        }
    }

    /**
     * 查看内存溢出的
     * @param application
     */
    public void initLeakcanary(Application application){
        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(application);
    }

    // 获取栈顶的activity，先进后出原则
    public static Activity getLastActivity() {
        return App.activityStack.lastElement();
    }

    // 退出所有activity
    public static void finishAllActivity() {
        if (App.activityStack != null) {
            while (App.activityStack.size() > 0) {
                Activity activity = getLastActivity();
                if (activity == null)
                    break;
                popOneActivity(activity);
            }
        }
    }


    public void logout() {
        // 关闭所有Activity
        if (activityStack != null) {
            for (Activity activity : activityStack) {
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
            activityStack.clear();
        }
    }

}
