package com.idea.jgw.ui.mining;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.OkhttpApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.dialog.LoadingDialog;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.user.UserInfoActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.DialogUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.utils.glide.GlideApp;
import com.joker.annotation.PermissionsCustomRationale;
import com.joker.annotation.PermissionsGranted;
import com.joker.annotation.PermissionsNonRationale;
import com.joker.api.Permissions4M;
import com.socks.okhttp.plus.listener.UploadListener;
import com.socks.okhttp.plus.model.Progress;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static com.idea.jgw.api.OkhttpApi.BASE_HOST;

/**
 * 实名认证，第二步，上传照片
 */
@Route(path = RouterPath.IDENTITY_AUTHENTICATION_ACTIVITY2)
public class IdentityAuthentication2Activity extends BaseActivity {
    //调用系统相机请求码，正面
    public static final int DO_CAMERA_REQUEST = 100;
    //调用系统相册请求码，正面
    public static final int OPEN_SYS_ALBUMS_REQUEST = 101;
    //调用系统相机请求码，反面
    public static final int DO_CAMERA_REQUEST_BACK = 103;
    //调用系统相册请求码，反面
    public static final int OPEN_SYS_ALBUMS_REQUEST_BACK = 104;
    //调用系统相机权限请求码，正面
    public static final int DO_CAMERA_PERMISSION_REQUEST = 105;
    //调用系统相册权限请求码，正面
    public static final int OPEN_SYS_ALBUMS_PERMISSION_REQUEST = 106;
    //调用系统相机权限请求码，反面
    public static final int DO_CAMERA_PERMISSION_REQUEST_BACK = 107;
    //调用系统相册权限请求码，反面
    public static final int OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK = 108;
    //调用系统相机权SD卡限请求码，反面
    public static final int DO_CAMERA_SD_PERMISSION_REQUEST_BACK = 109;
    //调用系统相机权SD卡限请求码，正面
    public static final int DO_CAMERA_SD_PERMISSION_REQUEST = 110;

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.iv_front_id_card)
    ImageView ivFrontIdCard;
    @BindView(R.id.iv_back_id_card)
    ImageView ivBackIdCard;
    @BindView(R.id.btn_of_submit)
    Button btnOfSubmit;
    private String frontPhotoPath;
    private String backPhotoPath;
    private String idNumber;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_identity_authentication2;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.authentication);

        if(getIntent().hasExtra("name")) {
            name = getIntent().getStringExtra("name");
        }
        if(getIntent().hasExtra("idNumber")) {
            idNumber = getIntent().getStringExtra("idNumber");
        }
    }

    @OnClick({R.id.btn_of_back, R.id.iv_front_id_card, R.id.iv_back_id_card, R.id.tv_load_from_album_front, R.id.tv_load_from_album_back, R.id.btn_of_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iv_front_id_card:
                requestPermission(DO_CAMERA_PERMISSION_REQUEST, Manifest.permission.CAMERA);
                break;
            case R.id.iv_back_id_card:
                requestPermission(DO_CAMERA_PERMISSION_REQUEST_BACK, Manifest.permission.CAMERA);
                break;
            case R.id.tv_load_from_album_front:
                requestPermission(OPEN_SYS_ALBUMS_PERMISSION_REQUEST, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.tv_load_from_album_back:
                requestPermission(OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.btn_of_submit:
                if(TextUtils.isEmpty(frontPhotoPath)) {
                    MToast.showToast(R.string.id_photo_is_null);
                } else if(TextUtils.isEmpty(backPhotoPath)) {
                    MToast.showToast(R.string.id_back_photo_is_null);
                } else {
                    certification();
                }
                break;
        }
    }

//    @PermissionsCustomRationale({DO_CAMERA_PERMISSION_REQUEST, DO_CAMERA_PERMISSION_REQUEST_BACK, OPEN_SYS_ALBUMS_PERMISSION_REQUEST, OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK, DO_CAMERA_SD_PERMISSION_REQUEST, DO_CAMERA_SD_PERMISSION_REQUEST_BACK})
    @Override
    public void customRationale(final int code) {
        switch (code) {
            case DO_CAMERA_SD_PERMISSION_REQUEST:
            case DO_CAMERA_SD_PERMISSION_REQUEST_BACK:
            case OPEN_SYS_ALBUMS_PERMISSION_REQUEST:
            case OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK:
                showPremissionRequest(code, Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.why_need_storage));
//                DialogUtils.showAlertDialog(this, "SD卡权限申请：\n我们需要您开启SD权限，一边访问上传头像", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        requestPermission(code, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                    }
//                });
                break;
            case DO_CAMERA_PERMISSION_REQUEST:
            case DO_CAMERA_PERMISSION_REQUEST_BACK:
                showPremissionRequest(code, Manifest.permission.CAMERA, getString(R.string.why_need_camera));
//                DialogUtils.showAlertDialog(this, "相机权限申请：\n我们需要您开启相机信息权限", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        requestPermission(code, Manifest.permission.CAMERA);
//                    }
//                });
                break;
        }
    }

//    @PermissionsNonRationale({DO_CAMERA_PERMISSION_REQUEST, DO_CAMERA_PERMISSION_REQUEST_BACK, OPEN_SYS_ALBUMS_PERMISSION_REQUEST, OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK, DO_CAMERA_SD_PERMISSION_REQUEST, DO_CAMERA_SD_PERMISSION_REQUEST_BACK})
    @Override
    public void non(int requestCode, final Intent intent) {
        switch (requestCode) {
            case DO_CAMERA_PERMISSION_REQUEST:
            case DO_CAMERA_PERMISSION_REQUEST_BACK:
                showExplain(intent, getString(R.string.why_need_storage));
//                DialogUtils.showAlertDialog(this, "sd卡权限申请：\n我们需要您开启读SD卡权限，以便上传照片", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                    }
//                });
                break;
            case DO_CAMERA_SD_PERMISSION_REQUEST:
            case DO_CAMERA_SD_PERMISSION_REQUEST_BACK:
            case OPEN_SYS_ALBUMS_PERMISSION_REQUEST:
            case OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK:
                showExplain(intent, getString(R.string.why_need_storage));
//                DialogUtils.showAlertDialog(this, "sd卡权限申请：\n我们需要您开启读SD卡权限，以便上传照片", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                    }
//                });
                break;
        }
    }

//    @PermissionsGranted({DO_CAMERA_PERMISSION_REQUEST, DO_CAMERA_PERMISSION_REQUEST_BACK, OPEN_SYS_ALBUMS_PERMISSION_REQUEST, OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK, DO_CAMERA_SD_PERMISSION_REQUEST, DO_CAMERA_SD_PERMISSION_REQUEST_BACK})
    @Override
    public void granted(int requestCode) {
        switch (requestCode) {
            case DO_CAMERA_PERMISSION_REQUEST:
                requestPermission(DO_CAMERA_SD_PERMISSION_REQUEST, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case DO_CAMERA_PERMISSION_REQUEST_BACK:
                requestPermission(DO_CAMERA_SD_PERMISSION_REQUEST_BACK, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case DO_CAMERA_SD_PERMISSION_REQUEST:
                frontPhotoPath = CommonUtils.doCamra(this, "front.jpg", DO_CAMERA_REQUEST);
                break;
            case DO_CAMERA_SD_PERMISSION_REQUEST_BACK:
                backPhotoPath = CommonUtils.doCamra(this, "back.jpg", DO_CAMERA_REQUEST_BACK);
                break;
            case OPEN_SYS_ALBUMS_PERMISSION_REQUEST:
                CommonUtils.openSysPick(this,  OPEN_SYS_ALBUMS_REQUEST);
                break;
            case OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK:
                CommonUtils.openSysPick(this,  OPEN_SYS_ALBUMS_REQUEST_BACK);
                break;
        }
    }

    @Override
    public void denied(int requestCode) {
        switch (requestCode) {
            case DO_CAMERA_PERMISSION_REQUEST:
            case DO_CAMERA_PERMISSION_REQUEST_BACK:
                MToast.showToast(R.string.camera_permission_fail);
                break;
            case DO_CAMERA_SD_PERMISSION_REQUEST:
            case DO_CAMERA_SD_PERMISSION_REQUEST_BACK:
            case OPEN_SYS_ALBUMS_PERMISSION_REQUEST:
            case OPEN_SYS_ALBUMS_PERMISSION_REQUEST_BACK:
                MToast.showToast(R.string.storage_permission_fail);
                break;
        }
    }

    private void certification() {
        final File file1 = new File(frontPhotoPath);
        final File file2 = new File(backPhotoPath);
        LoadingDialog.showDialogForLoading(IdentityAuthentication2Activity.this);
        String token = SharedPreferenceManager.getInstance().getSession();
        OkhttpApi.certification(token, name, idNumber, file1, file2, new UploadListener() {
            @Override
            public void onSuccess(String data) {
                LoadingDialog.cancelDialogForLoading();
                BaseResponse baseResponse = JSON.parseObject(data, BaseResponse.class);
                MToast.showToast(baseResponse.getData().toString());
                if(baseResponse.getCode() == BaseResponse.RESULT_OK) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(file1.exists()) {
                                file1.delete();
                            }
                            if(file2.exists()) {
                                file2.delete();
                            }
                        }
                    }).start();
                    setResult(RESULT_OK);
                    finish();
                } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                    reLogin();
                    MToast.showToast(baseResponse.getData().toString());
                }
            }

            @Override
            public void onFailure(Exception e) {
                LoadingDialog.cancelDialogForLoading();
                e.printStackTrace();
            }

            @Override
            public void onUIProgress(Progress progress) {
            }

            @Override
            public void onUIStart() {
            }

            @Override
            public void onUIFinish() {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPEN_SYS_ALBUMS_REQUEST:
                    if (data != null) {
                        frontPhotoPath = CommonUtils.getRealPathFromUri(this, data.getData());
                    } else {
                        MToast.showToast(R.string.file_not_exists);
                    }
                case DO_CAMERA_REQUEST:
                    if (!IdentityAuthentication2Activity.this.isDestroyed())
                    GlideApp.with(this).load(frontPhotoPath).centerInside().into(ivFrontIdCard);
                    break;
                case OPEN_SYS_ALBUMS_REQUEST_BACK:
                    if (data != null) {
                        backPhotoPath = CommonUtils.getRealPathFromUri(this, data.getData());
                    } else {
                        MToast.showToast(R.string.file_not_exists);
                    }
                case DO_CAMERA_REQUEST_BACK:
                    GlideApp.with(this).load(backPhotoPath).centerInside().into(ivBackIdCard);
                    break;
            }

        }
    }
}
