package com.idea.jgw.ui.createWallet;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.main.MainActivity;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.joker.annotation.PermissionsDenied;
import com.joker.annotation.PermissionsGranted;
import com.joker.annotation.PermissionsRationale;
import com.joker.annotation.PermissionsRequestSync;

import butterknife.BindView;
import butterknife.OnClick;

import static com.idea.jgw.ui.login.LoginActivity.EXTRA_USER;
import static com.idea.jgw.ui.main.MainActivity.ACCESS_NETWORK_STATE_CODE;
import static com.idea.jgw.ui.main.MainActivity.CAMERA_CODE;
import static com.idea.jgw.ui.main.MainActivity.CHANGE_NETWORK_STATE_CODE;
import static com.idea.jgw.ui.main.MainActivity.CHANGE_WIFI_STATE_CODE;
import static com.idea.jgw.ui.main.MainActivity.READ_EXTERNAL_STORAGE_CODE;
import static com.idea.jgw.ui.main.MainActivity.WRITE_EXTERNAL_STORAGE_CODE;

@Route(path = RouterPath.LOAD_OR_CREATE_WALLET_ACTIVITY)
@PermissionsRequestSync(
        permission = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        },
        value = {READ_EXTERNAL_STORAGE_CODE,
                WRITE_EXTERNAL_STORAGE_CODE
        })
public class LoadOrCreateWalletActivity extends BaseActivity {

    private static final int CREATE_WALLET_REQUEST = 11;
    private static final int LOAD_WALLET_REQUEST = 12;
    @BindView(R.id.btn_of_create_wallet)
    Button btnOfCreateWallet;
    @BindView(R.id.btn_of_load_wallet)
    Button btnOfLoadWallet;

    String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPhone = getIntent().getStringExtra(EXTRA_USER);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_load_or_create_wallet;
    }

    @Override
    public void initView() {

    }

    @OnClick({R.id.btn_of_create_wallet, R.id.btn_of_load_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_create_wallet:
                ARouter.getInstance().build(RouterPath.SET_TRANSACTION_PIN_ACTIVITY)
                        .withString(EXTRA_USER, userPhone)
                        .navigation(this, CREATE_WALLET_REQUEST);
                break;
            case R.id.btn_of_load_wallet:
                ARouter.getInstance().build(RouterPath.INPUT_KEY_WORDS_ACTIVITY).navigation(this, LOAD_WALLET_REQUEST);
                break;
        }
    }


    int hasPermissions = 0;

    @PermissionsGranted({READ_EXTERNAL_STORAGE_CODE, WRITE_EXTERNAL_STORAGE_CODE
    })
    public void granted(int code) {
        switch (code) {
            case READ_EXTERNAL_STORAGE_CODE:
                hasPermissions++;
                break;
            case WRITE_EXTERNAL_STORAGE_CODE:
                hasPermissions++;
                break;
            default:
                break;
        }
        //6 是请求的权限总数
        if (hasPermissions != 2) {

        }
    }

    @PermissionsDenied({READ_EXTERNAL_STORAGE_CODE, WRITE_EXTERNAL_STORAGE_CODE
    })
    public void denied(int code) {
        switch (code) {
            case READ_EXTERNAL_STORAGE_CODE:
                MToast.showLongToast(R.string.rquest_permission_read_storage);
                break;
            case WRITE_EXTERNAL_STORAGE_CODE:
                MToast.showLongToast(R.string.rquest_permission_write_storage);
                break;
            default:
                break;
        }
    }

    @PermissionsRationale({READ_EXTERNAL_STORAGE_CODE, WRITE_EXTERNAL_STORAGE_CODE
            , ACCESS_NETWORK_STATE_CODE, CHANGE_NETWORK_STATE_CODE, CHANGE_WIFI_STATE_CODE, CAMERA_CODE
    })
    public void rationale(int code) {
        switch (code) {
            case READ_EXTERNAL_STORAGE_CODE:
                MToast.showLongToast(R.string.rquest_permission_read_storage);
                break;
            case WRITE_EXTERNAL_STORAGE_CODE:
                MToast.showLongToast(R.string.rquest_permission_write_storage);
                break;
            case ACCESS_NETWORK_STATE_CODE:
                MToast.showLongToast(R.string.rquest_permission_access_network_state);
                break;
            case CHANGE_NETWORK_STATE_CODE:
                MToast.showLongToast(R.string.rquest_permission_change_network_state);
                break;
            case CHANGE_WIFI_STATE_CODE:
                MToast.showLongToast(R.string.rquest_permission_change_wifi_state);
                break;
            case CAMERA_CODE:
                MToast.showLongToast(R.string.rquest_permission_camera);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOAD_WALLET_REQUEST:
                case CREATE_WALLET_REQUEST:
                    finish();
                    break;
            }
        }
    }


}
