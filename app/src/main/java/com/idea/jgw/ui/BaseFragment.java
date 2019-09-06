package com.idea.jgw.ui;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.DialogUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.joker.api.Permissions4M;
import com.joker.api.wrapper.ListenerWrapper;

import io.reactivex.disposables.CompositeDisposable;
import rx.Subscription;

/**
 * Created by idea on 2018/6/21.
 */

public class BaseFragment extends Fragment implements ListenerWrapper.PermissionCustomRationaleListener, ListenerWrapper.PermissionRequestListener, ListenerWrapper.PermissionPageListener {
    public static final int REQUEST_STORAGE_CODE = 223; //存储权限
    public static final int REQUEST_PHONE_CODE = 227; //手机信息

    protected final CompositeDisposable disposables = new CompositeDisposable();

    public void reLogin() {
        SharedPreferenceManager.getInstance().setLogin(false);
        App.login = false;
        SPreferencesHelper.getInstance(App.getInstance()).saveData("relogin_time", System.currentTimeMillis());
        ARouter.getInstance().build(RouterPath.LOGIN_ACTIVITY).navigation();
        App.finishAllActivity();
        getActivity().finish();
    }
    public void checkPhoneStatePermission() {
        requestPermission(REQUEST_PHONE_CODE, Manifest.permission.READ_PHONE_STATE);
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
            case REQUEST_STORAGE_CODE:
                showPremissionRequest(code, Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.why_need_storage));
                break;
        }
    }

    public void showPremissionRequest(final int code, final String permission, String explain) {
        DialogUtils.showAlertDialog(getActivity(), explain, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissionOnRationale(code, permission);
            }
        });
    }

    public void non(int requestCode, final Intent intent) {
        switch (requestCode) {
            case REQUEST_STORAGE_CODE:
                showExplain(intent, getString(R.string.why_need_storage));
                break;
            case REQUEST_PHONE_CODE:
                showPhoneStateExplain(intent);
                break;
        }
    }

    public void showPhoneStateExplain(Intent intent){
        showExplain(intent, getString(R.string.why_need_phone_state));
    }

    public void showExplain(final Intent intent, String rational) {
        DialogUtils.showAlertDialog(getActivity(), rational, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(intent);
            }
        });
    }

    public void denied(int requestCode) {
        switch (requestCode) {
            case REQUEST_STORAGE_CODE:
                MToast.showToast(R.string.storage_permission_fail);
                break;
            case REQUEST_PHONE_CODE:
                MToast.showToast(R.string.phone_state_permission_fail);
                break;
        }
    }

    public void granted(int code) {
        switch (code) {
            case REQUEST_STORAGE_CODE:
                storageGranted();
                break;
            case REQUEST_PHONE_CODE:
                phoneStateGranted();
                break;
        }
    }

    public void storageGranted() {
    }

    public void phoneStateGranted() {
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

    public void unSubscribe(Subscription subscription) {
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
