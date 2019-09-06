package com.idea.jgw.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.service.GetSendStatusService;
import com.idea.jgw.ui.service.ScreenListenerService;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.TestUtils;
import com.idea.jgw.utils.common.DialogUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.joker.api.Permissions4M;
import com.joker.api.wrapper.ListenerWrapper;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import rx.Subscription;

/**
 * Created by idea on 2018/5/16.
 */

public abstract class BaseActivity extends AppCompatActivity implements ListenerWrapper.PermissionCustomRationaleListener, ListenerWrapper.PermissionRequestListener, ListenerWrapper.PermissionPageListener {
    //动态权限申请码
    private static int PREMISSION_REQUEST_CODE = 222; //相册访问sd权限
    public static final int REQUEST_STORAGE_CODE = 223; //存储权限
    public static final int REQUEST_READ_STORAGE_CODE = 323; //存储权限
    public static final int REQUEST_ALBUMS_CODE = 224; //相册
    public static final int REQUEST_CAMERA_CODE = 225; //拍照权限
    public static final int CAMERA_STORAGE_CODE = 226; //拍照访问sd权限
    public static final int REQUEST_PHONE_CODE = 227; //手机信息

    protected final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFullScreenFlags();
        hideStatusBar();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initView();
        App.pushOneActivity(this);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    public void setFullScreenFlags() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //只透明状态栏
    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    public abstract int getLayoutId();

    public abstract void initView();

    @Override
    protected void onResume() {
        super.onResume();
//        startScreenListener();
//        sendBroadcast(new Intent(ScreenListenerService.ACTION_ONRESUME));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        App.popOneActivity(this);
        disposables.clear();
    }

//    public void unSubscribe(Subscription subscription) {
//        if (subscription != null && !subscription.isUnsubscribed()) {
//            subscription.unsubscribe();
//        }
//    }

    long lastTouchTime;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTouchTime > 1000) {
            lastTouchTime = currentTime;
            sendBroadcast(new Intent(ScreenListenerService.ACTION_TOUCH_SCREEN));
//            startService(new Intent(this, ScreenListenerService.class));
            MyLog.e("dispatchTouchEvent ======");
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTouchTime > 1000) {
            lastTouchTime = currentTime;
            sendBroadcast(new Intent(ScreenListenerService.ACTION_TOUCH_SCREEN));
            MyLog.e("dispatchKeyEvent ======");
        }
        return super.dispatchKeyEvent(event);
    }

    private void startScreenListener() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTouchTime > 1000) {
            lastTouchTime = currentTime;
            startService(new Intent(this, ScreenListenerService.class));
            MyLog.e("startScreenListener ======");
        }
    }

    public void resetScreenListener() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTouchTime > 1000) {
            lastTouchTime = currentTime;
            sendBroadcast(new Intent(ScreenListenerService.ACTION_RESET));
//            startService(new Intent(this, ScreenListenerService.class));
            MyLog.e("resetScreenListener ======");
        }
    }

    public void share(Context context) {
        /** * 分享图片 */
        String filePath = SnapShort(this);
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("image/*");  //设置分享内容的类型
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, "com.idea.jgw.fileprovider", new File(filePath));
            share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        share_intent.putExtra(Intent.EXTRA_STREAM, uri);
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, context.getString(R.string.share_to));
        context.startActivity(share_intent);
    }

    public String SnapShort(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        int widths = display.getWidth();
        int heights = display.getHeight();

        view.setDrawingCacheEnabled(true);

        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeights, widths,
                heights - statusBarHeights);

        view.destroyDrawingCache();

        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = picDir.getPath() + File.separator + "IMAGE_" + timeStamp + ".jpg";
        File file = new File(filename);

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);

                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        bmp.recycle();

        return filename;
    }

    public void reLogin() {
        SharedPreferenceManager.getInstance().setLogin(false);
        App.login = false;
        ARouter.getInstance().build(RouterPath.LOGIN_ACTIVITY).navigation();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        TestUtils.writeWithName(timeStamp + ".txt", "relogin at " + System.currentTimeMillis());
        App.finishAllActivity();
        finish();
    }

    public void requestPermission(int requestCode, String permission) {
        Permissions4M.get(this)
                // 是否强制弹出权限申请对话框，建议设置为 true，默认为 true
                .requestForce(true)
                // 是否支持 5.0 权限申请，默认为 false
                .requestUnderM(true)
                // 权限，单权限申请仅只能填入一个
//                .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestPermissions(permission)
                // 权限码
                .requestCodes(requestCode)
                // 如果需要使用 @PermissionNonRationale 注解的话，建议添加如下一行
                // 返回的 intent 是跳转至**系统设置页面**
                .requestPageType(Permissions4M.PageType.MANAGER_PAGE)
                // 返回的 intent 是跳转至**手机管家页面**
                // .requestPageType(Permissions4M.PageType.ANDROID_SETTING_PAGE)
                .requestListener(this)
//                .requestCustomRationaleListener(this)
                .requestPage(this)
                .request();
    }

    public void requestPermissionOnRationale(int requestCode, String permission) {
        Permissions4M.get(this)
                //二次申请权限时避免循环弹出解释对话框
                .requestOnRationale()
                // 是否强制弹出权限申请对话框，建议设置为 true，默认为 true
                .requestForce(true)
                // 是否支持 5.0 权限申请，默认为 false
                .requestUnderM(true)
                // 权限，单权限申请仅只能填入一个
//                .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestPermissions(permission)
                // 权限码
                .requestCodes(requestCode)
                // 如果需要使用 @PermissionNonRationale 注解的话，建议添加如下一行
                // 返回的 intent 是跳转至**系统设置页面**
                .requestPageType(Permissions4M.PageType.MANAGER_PAGE)
                // 返回的 intent 是跳转至**手机管家页面**
                // .requestPageType(Permissions4M.PageType.ANDROID_SETTING_PAGE)
                .requestListener(this)
                .requestCustomRationaleListener(this)
                .requestPage(this)
                .request();
    }

    public void customRationale(final int code) {
        switch (code) {
            case REQUEST_PHONE_CODE:
                showPremissionRequest(code, Manifest.permission.CAMERA, getString(R.string.why_need_phone_state));
                break;
            case CAMERA_STORAGE_CODE:
            case REQUEST_STORAGE_CODE:
            case REQUEST_READ_STORAGE_CODE:
            case REQUEST_ALBUMS_CODE:
                showPremissionRequest(code, Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.why_need_storage));
                break;
            case REQUEST_CAMERA_CODE:
                showPremissionRequest(code, Manifest.permission.CAMERA, getString(R.string.why_need_camera));
                break;
        }
    }

    public void showPremissionRequest(final int code, final String permission, String explain) {
        DialogUtils.showAlertDialog(this, explain, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissionOnRationale(code, permission);
            }
        });
    }

    public void non(int requestCode, final Intent intent) {
        switch (requestCode) {
            case REQUEST_STORAGE_CODE:
            case REQUEST_READ_STORAGE_CODE:
            case CAMERA_STORAGE_CODE:
                showExplain(intent, getString(R.string.why_need_storage));
                break;
            case REQUEST_CAMERA_CODE:
                showExplain(intent, getString(R.string.why_need_camera));
                break;
            case REQUEST_PHONE_CODE:
                showExplain(intent, getString(R.string.why_need_phone_state));
                break;
            case REQUEST_ALBUMS_CODE:
                break;
        }
    }

    public void showExplain(final Intent intent, String rational) {
        DialogUtils.showAlertDialog(this, rational, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(intent);
            }
        });
    }

    public void denied(int requestCode) {
        switch (requestCode) {
            case REQUEST_STORAGE_CODE:
            case REQUEST_READ_STORAGE_CODE:
            case CAMERA_STORAGE_CODE:
                MToast.showToast(R.string.storage_permission_fail);
                break;
            case REQUEST_CAMERA_CODE:
                MToast.showToast(R.string.camera_permission_fail);
                break;
            case REQUEST_PHONE_CODE:
                phoneStateDenied();
                break;
            case REQUEST_ALBUMS_CODE:
                MToast.showToast(R.string.storage_permission_fail);
                break;
        }
    }

    public void granted(int code) {
        switch (code) {
            case REQUEST_STORAGE_CODE:
                storageGranted();
                break;
            case REQUEST_CAMERA_CODE:
                requestPermission(CAMERA_STORAGE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case CAMERA_STORAGE_CODE:
                cameraGranted();
                break;
            case REQUEST_PHONE_CODE:
                phoneStateGranted();
                break;
        }
    }

    public void checkStoragePermission() {
        requestPermission(REQUEST_STORAGE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void checkCameraPermission() {
        requestPermission(REQUEST_CAMERA_CODE, Manifest.permission.CAMERA);
    }

    public void checkPhoneStatePermission() {
        requestPermission(REQUEST_PHONE_CODE, Manifest.permission.READ_PHONE_STATE);
    }

    public void storageGranted() {
    }

    public void cameraGranted() {
    }

    public void phoneStateGranted() {
    }

    public void phoneStateDenied() {
        MToast.showToast(R.string.phone_state_permission_fail);
    }

    @Override
    public void permissionCustomRationale(int i) {
//        自定义权限申请解释对话框
        customRationale(i);
    }

    @Override
    public void pageIntent(int i, Intent intent) {
        non(i, intent);
    }

    @Override
    public void permissionGranted(int i) {
        granted(i);
    }

    @Override
    public void permissionDenied(int i) {
        denied(i);
    }

    @Override
    public void permissionRationale(int i) {
//        异步触发系统权限申请提示对话框
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        Permissions4M.onRequestPermissionsResult(this, requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @NonNull
    public String getBigDecimalText(BigDecimal amount) {
        return String.valueOf(amount.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
}
