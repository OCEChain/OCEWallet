package com.idea.jgw.ui.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.request.RequestOptions;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.OkhttpApi;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.UserInfo;
import com.idea.jgw.dialog.ChoosePhotoDialog;
import com.idea.jgw.dialog.LoadingDialog;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.mining.ShareActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.DialogUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.utils.glide.GlideApp;
import com.joker.annotation.PermissionsCustomRationale;
import com.joker.annotation.PermissionsDenied;
import com.joker.annotation.PermissionsGranted;
import com.joker.annotation.PermissionsNonRationale;
import com.joker.api.Permissions4M;
import com.socks.okhttp.plus.listener.UploadListener;
import com.socks.okhttp.plus.model.Progress;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.idea.jgw.api.OkhttpApi.BASE_HOST;

/**
 * 个人信息
 */
@Route(path = RouterPath.USER_INFO_ACTIVITY)
public class UserInfoActivity extends BaseActivity implements ChoosePhotoDialog.OnChooseListener {
    //调用系统相机请求码
    public static final int DO_CAMERA_REQUEST = 100;
    //调用系统相册请求码
    public static final int OPEN_SYS_ALBUMS_REQUEST = 101;
    //调用系统截图请求码
    public static final int SYS_CROP_REQUEST = 102;
    private static final int CAMERA_CODE_REQUEST = 11;
    private static final int ABLUM_STORAGE_CODE = 12; //相册访问sd权限
    private static final int TAKEPHOTO_STORAGE_CODE = 13; //拍照访问sd权限
    private static final int EXTERNAL_STORAGE_CODE = 14; //拍照访问sd权限
    private static final int UPDATE_NICKNAME = 15;

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.ll_nickname)
    LinearLayout llNickname;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;

    ChoosePhotoDialog choosePhotoDialog;
    private String userPhotoPath;
    UserInfo userInfo;
    private String nickname;
    private String face;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_info;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.personal_info);
        if (getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if (userInfo != null) {
            nickname = userInfo.getNickname();
            face = userInfo.getFace();
        } else {
            face = SharedPreferenceManager.getInstance().getFace();
            nickname = SharedPreferenceManager.getInstance().getNickname();
            if (TextUtils.isEmpty(face) && TextUtils.isEmpty(nickname)) {
                getInfo();
            }
        }
        tvNickname.setText(nickname);
        if (!TextUtils.isEmpty(face)) {
            GlideApp.with(this).load(BASE_HOST + face).apply(RequestOptions.circleCropTransform()).placeholder(R.mipmap.icon_default_photo).into(ivPhoto);
        }
        tvPhone.setText(SharedPreferenceManager.getInstance().getPhone());
    }

    private void getInfo() {
        String token = SharedPreferenceManager.getInstance().getSession();
        disposables.add(ServiceApi.getInstance().getApiService()
                .getinfo(token)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(UserInfoActivity.this, getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                       UserInfo userInfo = JSON.parseObject(baseResponse.getData().toString(), UserInfo.class);
                                       String phone = SharedPreferenceManager.getInstance().getPhone();
                                       SharedPreferenceManager.getInstance().setInvite_code(userInfo.getInvite_num());
                                       SharedPreferenceManager.getInstance().setInvite_num(userInfo.getInvite_man_num());
                                       SharedPreferenceManager.getInstance().setInvite_url(userInfo.getInvite_url());
                                       SharedPreferenceManager.getInstance().setFace(userInfo.getFace());
                                       SharedPreferenceManager.getInstance().setNickname(userInfo.getNickname());
                                       tvNickname.setText(userInfo.getNickname());
                                       if (!TextUtils.isEmpty(userInfo.getFace())) {
                                           GlideApp.with(UserInfoActivity.this).load(BASE_HOST + userInfo.getFace()).apply(RequestOptions.circleCropTransform()).placeholder(R.mipmap.icon_default_photo).into(ivPhoto);
                                       }
                                   } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                                       reLogin();
                                   } else {
                                       MToast.showToast(baseResponse.getData().toString());
                                   }
                               }

                               @Override
                               protected void _onError(String message) {
                                   MToast.showToast(message);
                               }
                           }
                ));
    }

    @OnClick({R.id.btn_of_back, R.id.iv_photo, R.id.ll_nickname, R.id.ll_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iv_photo:
                showPhotoChooseDialog();
                break;
            case R.id.ll_nickname:
                ARouter.getInstance().build(RouterPath.NIKENAME_ACTIVITY2).withString("nickname", nickname).navigation(this, UPDATE_NICKNAME);
                break;
            case R.id.ll_phone:
//                ARouter.getInstance().build(RouterPath.SECURITY_MANAGER_ACTIVITY).navigation();
                break;
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("nickname", nickname);
        intent.putExtra("face", face);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    public void showPhotoChooseDialog() {
        if (choosePhotoDialog == null) {
            choosePhotoDialog = new ChoosePhotoDialog(this, this);
        }
        choosePhotoDialog.show();
    }

//    @PermissionsCustomRationale({TAKEPHOTO_STORAGE_CODE, ABLUM_STORAGE_CODE, CAMERA_CODE_REQUEST})
//    public void customRationale(final int code) {
//        switch (code) {
//            case TAKEPHOTO_STORAGE_CODE:
//            case ABLUM_STORAGE_CODE:
//                DialogUtils.showAlertDialog(this, "SD卡权限申请：\n我们需要您开启SD权限，一边访问上传头像", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        requestPermission(code, Manifest.permission.READ_EXTERNAL_STORAGE);
//                    }
//                });
//                break;
//            case CAMERA_CODE_REQUEST:
//                DialogUtils.showAlertDialog(this, "相机权限申请：\n我们需要您开启相机信息权限", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        requestPermission(CAMERA_CODE_REQUEST, Manifest.permission.CAMERA);
//                    }
//                });
//                break;
//        }
//    }

//    @PermissionsNonRationale({TAKEPHOTO_STORAGE_CODE, ABLUM_STORAGE_CODE, CAMERA_CODE_REQUEST})
//    public void non(int code, final Intent intent) {
//        switch (code) {
//            case TAKEPHOTO_STORAGE_CODE:
//            case ABLUM_STORAGE_CODE:
//                DialogUtils.showAlertDialog(this, "sd卡权限申请：\n我们需要您开启读SD卡权限，以便上传照片", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                    }
//                });
//                break;
//            case CAMERA_CODE_REQUEST:
//                DialogUtils.showAlertDialog(this, "读取相机权限申请：\n我们需要您开启读取相机权限", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//
//                    }
//                });
//                break;
//        }
//    }

    @Override
    public void choose(int which) {
        switch (which) {
            case ChoosePhotoDialog.ALBUM:
                checkStoragePermission();
                break;
            case ChoosePhotoDialog.CANCEL:
                choosePhotoDialog.dismiss();
                break;
            case ChoosePhotoDialog.TAKE_PHOTO:
//                requestCameraPermission();
                checkCameraPermission();
                break;
        }
    }

    @Override
    public void storageGranted() {
        pickPhoto();
    }

    @Override
    public void cameraGranted() {
        takePhoto();
    }

    public void takePhoto() {
        userPhotoPath = CommonUtils.doCamra(this, "temp.jpg", DO_CAMERA_REQUEST);
        choosePhotoDialog.dismiss();
    }

    public void pickPhoto() {
        CommonUtils.openSysPick(this, OPEN_SYS_ALBUMS_REQUEST);
//        userPhotoPath = CommonUtils.cropImageUri(this, "userPhotoPath.jpg", OPEN_SYS_ALBUMS_REQUEST);
        choosePhotoDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String resultKey = "";
            switch (requestCode) {
                case DO_CAMERA_REQUEST:
                    userPhotoPath = CommonUtils.cropImageUri(this, userPhotoPath, SYS_CROP_REQUEST);
                    break;
                case SYS_CROP_REQUEST:
                    updateUserPhoto(userPhotoPath);
                    break;
                case OPEN_SYS_ALBUMS_REQUEST:
                    if (data != null) {
                        userPhotoPath = CommonUtils.getRealPathFromUri(this, data.getData());
                        userPhotoPath = CommonUtils.cropImageUri(this, userPhotoPath, SYS_CROP_REQUEST);
//                        updateUserPhoto(userPhotoPath);
                    } else {
                        MToast.showToast("图片损坏，请重新选择");
                    }
                    break;
                case UPDATE_NICKNAME:
                    if (data.hasExtra("nickname")) {
                        nickname = data.getStringExtra("nickname");
                        tvNickname.setText(nickname);
                        SharedPreferenceManager.getInstance().setNickname(nickname);
                    }
                    break;
            }
        }
    }

    public void updateUserPhoto2(final String fileName) {
        final File file = new File(fileName);
//        Map<String, Object> map = new HashMap<>();
//        map.put("token", "6fd95490e77cdf77c9c8162641d2cb6c");
        RequestBody body1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("avatarByte", file.getName(), body1);
        String token = SharedPreferenceManager.getInstance().getSession();
        RequestBody tokenBody = RequestBody.create(MediaType.parse("text/plain"), token);
        disposables.add(ServiceApi.getInstance().getApiService().updatePhoto(tokenBody, part)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), true) {
                                   @Override
                                   protected void _onNext(BaseResponse baseResponse) {
                                       MToast.showToast(baseResponse.getData().toString());
                                   }

                                   @Override
                                   protected void _onError(String message) {
                                       MToast.showToast(message);
                                   }
                               }
                ));
    }

    /**
     * <p>上传头像都附件服务器</p>
     *
     * @param fileName 要上传的文件名
     */
    public void updateUserPhoto(final String fileName) {
//        updateUserPhoto2(fileName);
        final File file = new File(fileName);
//        File file = new File(Environment.getExternalStorageDirectory(), "HldImage/userPhotoPath.jpg");

        String token = SharedPreferenceManager.getInstance().getSession();
        OkhttpApi.updatePhoto(token, file, new UploadListener() {
            @Override
            public void onSuccess(String data) {
                BaseResponse baseResponse = JSON.parseObject(data, BaseResponse.class);
                if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                    face = baseResponse.getData().toString();
                    SharedPreferenceManager.getInstance().setFace(face);
                    if (!UserInfoActivity.this.isDestroyed())
                        GlideApp.with(UserInfoActivity.this).load(BASE_HOST + face).apply(RequestOptions.circleCropTransform()).into(ivPhoto);
                    if (file.exists()) {
                        file.delete();
                    }
                } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                    MToast.showToast(baseResponse.getData().toString());
                    reLogin();
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onUIProgress(Progress progress) {
            }

            @Override
            public void onUIStart() {
                LoadingDialog.showDialogForLoading(UserInfoActivity.this);
            }

            @Override
            public void onUIFinish() {
                LoadingDialog.cancelDialogForLoading();
            }
        });
    }

    /**
     * 更新修改后的控件背景
     *
     * @param view 要修改的空间
     * @param url  图片路径
     */
    private void updateImageView(final View view, final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlideApp.with(UserInfoActivity.this).load(userPhotoPath).into((ImageView) view);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
