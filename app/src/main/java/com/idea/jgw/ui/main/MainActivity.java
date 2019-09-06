package com.idea.jgw.ui.main;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.OceApi;
import com.idea.jgw.api.retrofit.OceServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.OceBaseResponse;
import com.idea.jgw.logic.btc.model.TLAppDelegate;
import com.idea.jgw.logic.btc.model.TLHDWalletWrapper;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.interfaces.StorableWallet;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.createWallet.SetTransactionPinActivity;
import com.idea.jgw.ui.createWallet.WalletCreateSuccessActivity;
import com.idea.jgw.ui.login.StartActivity;
import com.idea.jgw.ui.main.fragment.DiscoverFragment;
import com.idea.jgw.ui.main.fragment.MineFragment;
import com.idea.jgw.ui.main.fragment.WalletFragment;
import com.idea.jgw.ui.user.AboutUsActivity;
import com.idea.jgw.utils.CheckVersion;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.UpdateManager;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.ui.service.ScreenListenerService;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.DialogUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.common.SharedPreferenceManager;

import org.bitcoinj.core.Base58;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * 主页面
 */
@Route(path = RouterPath.MAIN_ACTIVITY)
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {


    private static final int PHONE_STATE = 1;
    private static final int INSTALL_PACKAGE = 2;
    private static final int EXTERNAL_STORAGE = 3;
    public static final int READ_EXTERNAL_STORAGE_CODE = 123;//硬盘读
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 234;//硬盘写
    public static final int ACCESS_NETWORK_STATE_CODE = 345;//
    public static final int CHANGE_NETWORK_STATE_CODE = 456;
    public static final int CAMERA_CODE = 678;
    public static final int CHANGE_WIFI_STATE_CODE = 789;
    public static final int READ_LOGS_CDOE = 890;
    static final String MINE_FRAGMENT_TAG = "mine";
    static final String WALLET_FRAGMENT_TAG = "wallet";
    static final String DISCOVER_FRAGMENT_TAG = "discover";


    @BindView(R.id.home_container)
    FrameLayout homeContainer;
    @BindView(R.id.btn_of_wallet)
    RadioButton rbOfWallet;
    @BindView(R.id.btn_of_discovery)
    RadioButton rbOfDiscovery;
    @BindView(R.id.rb_of_mine)
    RadioButton rbOfMine;
    @BindView(R.id.radio_group_button)
    RadioGroup radioGroupButton;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    WalletFragment walletFragment;
    DiscoverFragment discoverFragment;
    MineFragment mineFragment;
    Fragment currentFragment;
    private String apkPath = "";


    public static BigDecimal ethCount = new BigDecimal("0"); //
    public static BigDecimal jgwCount = new BigDecimal("0");//
    public static long lastGetCoinTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, ScreenListenerService.class));
//        cretaeEthWallet();

        requestPermission(REQUEST_READ_STORAGE_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        walletFragment = new WalletFragment();
        discoverFragment = new DiscoverFragment();
        mineFragment = new MineFragment();
//        currentFragment = discoverFragment;

        radioGroupButton.setOnCheckedChangeListener(this);
        rbOfDiscovery.setChecked(true);
//        radioGroupButton.check(R.id.btn_of_discovery);
//        showFragment(DISCOVER_FRAGMENT_TAG);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.btn_of_wallet:
                showFragment(WALLET_FRAGMENT_TAG);
//                currentFragment = walletFragment;
//                rbOfWallet.setSelected(true);
//                getFragmentManager().beginTransaction().replace(R.id.home_container, currentFragment).commit();
                break;
            case R.id.btn_of_discovery:
                showFragment(DISCOVER_FRAGMENT_TAG);
//                currentFragment = discoverFragment;
//                rbOfDiscovery.setSelected(true);
//                getFragmentManager().beginTransaction().replace(R.id.home_container, currentFragment).commit();
                break;
            case R.id.rb_of_mine:
                showFragment(MINE_FRAGMENT_TAG);
//                currentFragment = mineFragment;
//                rbOfMine.setSelected(true);
//                getFragmentManager().beginTransaction().replace(R.id.home_container, currentFragment).commit();
                break;
        }
    }

    private void showFragment(String tag) {
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        if (fragment == null) {
            if (tag.equals(WALLET_FRAGMENT_TAG)) {
                fragment = walletFragment;
//                rbOfWallet.setChecked(true);
            } else if (tag.equals(DISCOVER_FRAGMENT_TAG)) {
                fragment = discoverFragment;
//                rbOfDiscovery.setChecked(true);
            } else if (tag.equals(MINE_FRAGMENT_TAG)) {
                fragment = mineFragment;
//                rbOfMine.setChecked(true);
            }
            transaction.add(R.id.home_container, fragment, tag).commit();
        } else {
//            if (tag.equals(CLASSIFY_FRAGMENT_TAG)) {
//                btnOfClassify.setSelected(true);
//            } else if (tag.equals(RANKING_FRAGMENT_TAG)) {
//                btnOfRanking.setSelected(true);
//            } else if (tag.equals(ALERT_FRAGMENT_TAG)) {
//                btnOfAlert.setSelected(true);
//            } else if (tag.equals(MINE_FRAGMENT_TAG)) {
//                rbOfUser.setSelected(true);
//            } else {
//                btnOfHome.setSelected(true);
//            }
            transaction.show(fragment).commit();
        }
        currentFragment = fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, ScreenListenerService.class));
        MyLog.e("main onDestroy");
    }

    long lastBackTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBackTime < 2 * 1000) {
                finish();
            } else {
                MToast.showToast(R.string.quit_notice);
                lastBackTime = currentTime;
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void checkAndUpdate(final String version) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (CommonUtils.pingNet("114.215.211.154")) {
                    CheckVersion getVersion = new CheckVersion();
                    getVersion.setCheckNotice(true);
                    final CheckVersion.VersionInfo info = getVersion.checkUpdate(version);
                    if (info == null) {
                        MyLog.e(" CheckVersion.VersionInfo is null");
                    } else {
                        MyLog.e("checkNotice --  versionInfo :" + JSON.toJSONString(info));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (info.isParaseOk && !TextUtils.isEmpty(info.mPackageUrl) && info.mPackageSize > 0) {
                                    if (compare(info.mNum, version)) {
                                        showAlertDialog(getResources().getString(R.string.app_update), info.mLog, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/11.apk";
                                                UpdateManager updateManager = new UpdateManager(MainActivity.this, new UpdateManager.DownloadListener() {
                                                    @Override
                                                    public void onFinish() {
                                                        checkPermissionAndInstall();
                                                    }

                                                    @Override
                                                    public void onFail(Exception e) {
//                                                        MToast.showToast(R.string.download_failed);
                                                        e.printStackTrace();
                                                    }

                                                    @Override
                                                    public int onProgress(int progress) {
                                                        return progress;
                                                    }
                                                });
                                                updateManager.downloadApk(info.mPackageUrl, apkPath);
                                            }
                                        });
                                    }
//                                } else {
//                                    MToast.showToast(R.string.current_is_new);
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }

    boolean compare(String version1, String version2) {
//        String flag1 = version1.substring(0, 1);
//        String flag2 = version2.substring(0, 1);
//        if(!flag1.equals("v") && flag2.equals("v")) {
//            return false;
//        } else if(flag1.equals("v") && !flag2.equals("v")) {
//            return true;
//        }

        String[] str1 = getVersionNumber(version1);
        String[] str2 = getVersionNumber(version2);
        if (Integer.parseInt(str1[0]) > Integer.parseInt(str2[0])) {
            return true;
        } else if (Integer.parseInt(str1[0]) == Integer.parseInt(str2[0]) && Integer.parseInt
                (str1[1]) > Integer.parseInt(str2[1])) {
            return true;
        } else if (Integer.parseInt(str1[0]) == Integer.parseInt(str2[0]) && Integer.parseInt
                (str1[1]) == Integer.parseInt(str2[1]) && Integer.parseInt(str1[2]) > Integer
                .parseInt(str2[2])) {
            return true;
        }
        return false;
    }

    private String[] getVersionNumber(String version1) {
        String[] str;
        if (version1.startsWith("v")) {
            str = version1.replace("v", "").split("\\.");
        } else {
            str = version1.replace("d", "").split("\\.");
        }
        return str;
    }

    public void showAlertDialog(String title, String str, DialogInterface.OnClickListener negativeListener, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setNegativeButton(R.string.string_of_cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        AlertDialog alertDialog = builder.create();
        if (!TextUtils.isEmpty(title)) {
            alertDialog.setTitle(title);
        } else {
            alertDialog.setTitle(null);
        }
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setMessage(str);
//        if(negativeListener == null) {
//            negativeListener = null;
//        }
        alertDialog.setButton(BUTTON_NEGATIVE, getResources().getString(R.string.string_of_cancel), negativeListener);
        alertDialog.setButton(BUTTON_POSITIVE, getResources().getString(R.string.ok), positiveListener);
        alertDialog.show();
    }

    //    @PermissionsGranted({PHONE_STATE, INSTALL_PACKAGE, EXTERNAL_STORAGE})
    @Override
    public void granted(int requestCode) {
        super.granted(requestCode);
        switch (requestCode) {
            case PHONE_STATE:
                checkAndUpdate(CommonUtils.getAppName(App.getInstance()));
                break;
            case EXTERNAL_STORAGE:
                requestPermission(PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
                break;
            case REQUEST_READ_STORAGE_CODE:
                requestPermission(EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case INSTALL_PACKAGE:
                //安装apk
                install(apkPath);
                break;
        }
    }

    private void checkPermissionAndInstall() {
        if (Build.VERSION.SDK_INT >= 26) {
            //来判断应用是否有权限安装apk
            boolean installAllowed = getPackageManager().canRequestPackageInstalls();
            //有权限
            if (installAllowed) {
                //安装apk
                install(apkPath);
            } else {
                //无权限 申请权限
                startInstallPermissionSettingActivity();
            }
        } else {
            install(apkPath);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, INSTALL_PACKAGE);
    }

    private void install(String apkPath) {
        File apkfile = new File(apkPath);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //7.0以上通过FileProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(this, "com.idea.jgw.fileprovider", apkfile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkPath), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    //    @PermissionsCustomRationale({PHONE_STATE, EXTERNAL_STORAGE, INSTALL_PACKAGE})
    @Override
    public void customRationale(int requestCode) {
        super.customRationale(requestCode);
        switch (requestCode) {
            case PHONE_STATE:
                DialogUtils.showAlertDialog(this, getString(R.string.why_need_phone_state), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionOnRationale(PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
                    }
                });
                break;
            case EXTERNAL_STORAGE:
                DialogUtils.showAlertDialog(this, getString(R.string.why_need_storage), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionOnRationale(EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                });
                break;
            case INSTALL_PACKAGE:
                DialogUtils.showAlertDialog(this, getString(R.string.why_need_install), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionOnRationale(INSTALL_PACKAGE, Manifest.permission.INSTALL_PACKAGES);
                    }
                });
                break;
        }
    }

    //    @PermissionsNonRationale({PHONE_STATE, EXTERNAL_STORAGE, INSTALL_PACKAGE})
    @Override
    public void non(int requestCode, final Intent intent) {
        super.non(requestCode, intent);
        switch (requestCode) {
            case PHONE_STATE:
                showExplain(intent, "PHONE_STATE权限申请：\n我们需要您开启读PHONE_STATE权限");
//                DialogUtils.showAlertDialog(this, "PHONE_STATE权限申请：\n我们需要您开启读PHONE_STATE权限", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                    }
//                });
                break;
            case EXTERNAL_STORAGE:
                showExplain(intent, getString(R.string.why_need_storage));
//                DialogUtils.showAlertDialog(this, "SD权限申请：\n我们需要您开启读SD权限", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                    }
//                });
                break;
            case INSTALL_PACKAGE:
                showExplain(intent, "安装权限申请：\n我们需要您开启安装应用权限，用以更新app");
//                DialogUtils.showAlertDialog(this, "安装权限申请：\n我们需要您开启读安装权限，用以更新app", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                    }
//                });
                break;
        }
    }

    @Override
    public void denied(int requestCode) {
        super.denied(requestCode);
        switch (requestCode) {
            case PHONE_STATE:
                MToast.showToast(R.string.phone_state_permission_fail);
                break;
            case EXTERNAL_STORAGE:
                MToast.showToast(R.string.storage_permission_fail);
                break;
            case INSTALL_PACKAGE:
                MToast.showToast("INSTALL_PACKAGE_permission_fail");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == INSTALL_PACKAGE) {
                install(apkPath);
            }
        }
    }


}

