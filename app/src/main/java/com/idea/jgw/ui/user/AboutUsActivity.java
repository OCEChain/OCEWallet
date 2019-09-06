package com.idea.jgw.ui.user;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.CheckVersion;
import com.idea.jgw.utils.UpdateManager;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.DialogUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * 关于我们
 */
@Route(path = RouterPath.ABOUT_ACTIVITY)
public class AboutUsActivity extends BaseActivity {

    private static final int PHONE_STATE = 1;
    private static final int INSTALL_PACKAGE = 2;
    private static final int EXTERNAL_STORAGE = 3;
    public static final int REQUEST_STORAGE_CODE = 223; //存储权限
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.ll_version_log)
    LinearLayout llVersionLog;
    @BindView(R.id.ll_update)
    LinearLayout llUpdate;
    @BindView(R.id.tv_of_version)
    TextView tvOfVersion;
    private String apkPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.about_us);
        tvOfVersion.setText("V"+ CommonUtils.getAppVersionName2(this));
        llVersionLog.setVisibility(View.GONE);
    }

    @OnClick({R.id.btn_of_back, R.id.ll_version_log, R.id.ll_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.ll_version_log:
                ARouter.getInstance().build(RouterPath.VERSION_ACTIVITY).navigation();
                break;
            case R.id.ll_update:
                requestPermission(REQUEST_READ_STORAGE_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                break;
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
                                                UpdateManager updateManager = new UpdateManager(AboutUsActivity.this, new UpdateManager.DownloadListener() {
                                                    @Override
                                                    public void onFinish() {
                                                        checkPermissionAndInstall();
                                                    }

                                                    @Override
                                                    public void onFail(Exception e) {
                                                        MToast.showToast(R.string.download_failed);
                                                    }

                                                    @Override
                                                    public int onProgress(int progress) {
                                                        return progress;
                                                    }
                                                });
                                                updateManager.downloadApk(info.mPackageUrl, apkPath, true);
                                            }
                                        });
                                    }
                                } else {
                                    MToast.showToast(R.string.current_is_new);
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
                showExplain(intent, getString(R.string.why_need_phone));
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
                showExplain(intent, getString(R.string.why_need_install));
//                DialogUtils.showAlertDialog(this, "安装权限申请：\n我们需要您开启读安装权限，用以更新app", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                MToast.showToast(R.string.install_permission_fail);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INSTALL_PACKAGE) {
                install(apkPath);
            }
        }
    }
}
