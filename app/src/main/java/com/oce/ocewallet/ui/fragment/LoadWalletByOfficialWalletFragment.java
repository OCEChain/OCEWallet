package com.oce.ocewallet.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.oce.ocewallet.R;
import com.oce.ocewallet.base.BaseFragment;
import com.oce.ocewallet.domain.OCEWallet;
import com.oce.ocewallet.ui.contract.LoadWalletContract;
import com.oce.ocewallet.ui.presenter.LoadWalletPresenter;
import com.oce.ocewallet.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;



public class LoadWalletByOfficialWalletFragment extends BaseFragment implements LoadWalletContract.View {

    @BindView(R.id.et_keystore)
    EditText etKeystore;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.btn_load_wallet)
    TextView btnLoadWallet;

    private LoadWalletContract.Presenter mPresenter;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_official_wallet;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        mPresenter = new LoadWalletPresenter(this);
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.btn_load_wallet, R.id.lly_wallet_agreement})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_wallet:
                String walletPwd = etWalletPwd.getText().toString().trim();
                String keystore = etKeystore.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(keystore, walletPwd);
                if (verifyWalletInfo) {
                    showDialog(getString(R.string.loading_wallet_tip));
                    mPresenter.loadWalletByKeystore(keystore, walletPwd);
                }
                break;
            case R.id.lly_wallet_agreement:
                if (cbAgreement.isChecked()) {
                    cbAgreement.setChecked(false);
                    btnLoadWallet.setEnabled(false);
                } else {
                    cbAgreement.setChecked(true);
                    btnLoadWallet.setEnabled(true);
                }
                break;
        }
    }

    private boolean verifyInfo(String keystore, String walletPwd) {
        if (TextUtils.isEmpty(keystore)) {
            ToastUtils.showToast(R.string.load_wallet_by_official_wallet_keystore_input_tip);
            return false;
        } else if (TextUtils.isEmpty(walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
            // 同时判断强弱
            return false;
        }
        return true;
    }

    @Override
    public void loadSuccess(OCEWallet wallet) {
        ToastUtils.showToast("导入钱包成功");
        dismissDialog();
        EventBus.getDefault().post(wallet);
    }

    @Override
    public void showError(String errorInfo) {

    }

    @Override
    public void complete() {

    }
}
