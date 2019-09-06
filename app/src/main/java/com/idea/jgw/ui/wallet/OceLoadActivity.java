package com.idea.jgw.ui.wallet;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MToast;

import static com.idea.jgw.ui.main.fragment.WalletFragment.OCE_ADDRESS;
import static com.idea.jgw.ui.main.fragment.WalletFragment.OCE_PRIVATE_KEY;
import static com.idea.jgw.ui.main.fragment.WalletFragment.OCE_PUBLIC_KEY;

@Route(path = RouterPath.LOAD_OCE)
public class OceLoadActivity extends BaseActivity {

    EditText etAddrss;
    EditText etPrivateKey;
    EditText etPublicKey;
    Button btnLoad;
    Button btn_role;
    String maijia = "phenix3LvG3Ka7JQHQzXJhvcEdkCjnY5gurcNX5P";
    String maijia2 = "phenix3Q3J9eN44DKv6eeBH3Z9wf9n74G5hwTVS3";  //买家地址

    @Override
    public int getLayoutId() {
        return R.layout.activity_load_oce;
    }

    @Override
    public void initView() {
        etAddrss = findViewById(R.id.et_address);
        etPrivateKey = findViewById(R.id.et_privateKey);
        etPublicKey = findViewById(R.id.et_publicKey);
        btnLoad = findViewById(R.id.btn_load);
        btn_role = findViewById(R.id.btn_role);
        etAddrss.setText(maijia);

        btn_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_role.getText().toString().trim().equals(getString(R.string.merchants))) {
                    btn_role.setText(R.string.customer);
                    etAddrss.setText(maijia2);
                } else {
                    btn_role.setText(R.string.merchants);
                    etAddrss.setText(maijia);
                }
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String privateKey = etPrivateKey.getText().toString();
                String publicKey = etPublicKey.getText().toString();
                String address = etAddrss.getText().toString();

                if(TextUtils.isEmpty(address)){
                    MToast.showToast(R.string.address_empty);
                    return;
                }

//                if(TextUtils.isEmpty(publicKey)){
//                    MToast.showLongToast("公钥不能为空");
//                    return;
//                }

//                if(TextUtils.isEmpty(privateKey)){
//                    MToast.showLongToast("私钥不能为空");
//                    return;
//                }


                if(!TextUtils.isEmpty(privateKey))
                SPreferencesHelper.getInstance(OceLoadActivity.this).saveData(OCE_PRIVATE_KEY, privateKey);
                SPreferencesHelper.getInstance(OceLoadActivity.this).saveData(OCE_ADDRESS, address);
                if(!TextUtils.isEmpty(publicKey))
                SPreferencesHelper.getInstance(OceLoadActivity.this).saveData(OCE_PUBLIC_KEY, publicKey);

                MToast.showToast(R.string.load_success);
            }
        });
    }
}
