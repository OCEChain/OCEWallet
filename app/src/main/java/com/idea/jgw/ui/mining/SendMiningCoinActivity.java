package com.idea.jgw.ui.mining;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.dialog.InputTransactionPwdDialog;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.IBAN;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.ui.wallet.EthSendActivity;
import com.idea.jgw.ui.wallet.QrSanActivity;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.utils.glide.GlideApp;
import com.idea.jgw.view.PayPsdInputView;

import org.web3j.utils.Numeric;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.idea.jgw.api.OkhttpApi.BASE_HOST;

/**
 * 发送挖矿所得页面
 */
@Route(path = RouterPath.SEND_MINING_COIN_ACTIVITY)
public class SendMiningCoinActivity extends BaseActivity {

    private static final int INPUT_PASSWORD = 22;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.iv_digital_logo)
    ImageView ivDigitalLogo;
    @BindView(R.id.tv_balance_label)
    TextView tvBalanceLabel;
    @BindView(R.id.et_received_address)
    EditText etReceivedAddress;
    @BindView(R.id.iv_of_delete)
    ImageView ivOfDelete;
    @BindView(R.id.iv_of_scan_code)
    ImageView ivOfScanCode;
    @BindView(R.id.et_send_amount)
    EditText etSendAmount;
    @BindView(R.id.btn_of_send)
    Button btnOfSend;
    @BindView(R.id.tv_of_balance)
    TextView tvOfBalance;

    private String coinType;
    private String coinLogo;
    private double balance;
    int feetype = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_send_mining_coin;
    }

    @Override
    public void initView() {

        if (getIntent().hasExtra("coinType")) {
            coinType = getIntent().getStringExtra("coinType");
        }
        if (getIntent().hasExtra("coinLogo")) {
            coinLogo = getIntent().getStringExtra("coinLogo");
        }
        if (getIntent().hasExtra("balance")) {
            balance = getIntent().getDoubleExtra("balance", 0);
        }

        tvOfBalance.setText(R.string.balance);

        tvOfTitle.setText(R.string.send);
        GlideApp.with(this).load(BASE_HOST + coinLogo).into(ivDigitalLogo);
    }

    @OnClick({R.id.btn_of_back, R.id.iv_of_delete, R.id.iv_of_scan_code, R.id.btn_of_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.iv_of_delete:
                etReceivedAddress.setText("");
                break;
            case R.id.iv_of_scan_code:
                ARouter.getInstance()
                        .build(RouterPath.QR_SCAN_ACTIVITY)
                        .navigation(SendMiningCoinActivity.this, QrSanActivity.REQ_CODE);
                break;
            case R.id.btn_of_send:
                if(TextUtils.isEmpty(etSendAmount.getText().toString().trim())) {
                    MToast.showToast(R.string.send_amount_empty);
                } else if(!validAddress(etReceivedAddress.getText().toString().trim())) {
                    MToast.showToast(R.string.address_empty);
                } else {
                    showPwdInputDialog();
                }
                break;
        }
    }

    InputTransactionPwdDialog inputTransactionPwdDialog;

    private void showPwdInputDialog() {
        inputTransactionPwdDialog = new InputTransactionPwdDialog(this, new PayPsdInputView.OnPasswordListener() {
            @Override
            public void inputFinished(String inputPsd) {
                inputTransactionPwdDialog.dismiss();
                String paymentPwd = SharedPreferenceManager.getInstance().getPaymentPwd();
                if (inputPsd.equals(paymentPwd)) {
                    transferMining(Double.parseDouble(etSendAmount.getText().toString()), etReceivedAddress.getText().toString());
                } else {
                    inputTransactionPwdDialog.setErrorMsg(getResources().getString(R.string.transaction_pin_error));
                }
            }
        });
        inputTransactionPwdDialog.show();
    }

    private void transferMining(double num, String addr) {
        String token = SharedPreferenceManager.getInstance().getSession();
        disposables.add(ServiceApi.getInstance().getApiService()
                .transferMiningData(coinType, token, feetype, num, addr)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(this, getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
//                                       MToast.showToast(baseResponse.getData().toString());
                                   } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                                       reLogin();
                                   }
                                   MToast.showToast(baseResponse.getData().toString());
                               }

                               @Override
                               protected void _onError(String message) {
                                   MToast.showToast(message);
                               }
                           }
                ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case QrSanActivity.REQ_CODE:
                    String address = data.getExtras().getString(QrSanActivity.EXTRA_RESULT_QR);
                    if (address.contains("?") && address.contains("&") && address.contains("token")) {
                        String iban = address.substring("iban:".length(), address.indexOf("?"));
                        String amount = address.substring(address.indexOf("=") + 1, address.indexOf("&"));
                        String tokenType = address.substring(address.indexOf("token") + 6);

                        address = IBAN.IBAN2Address(iban);

                    } else if (validAddress(address)) {
                        if (address.startsWith("iban:")) {
                            address = address.replace("iban:", "");
                        }
                        if (IBAN.validateIBAN(address)) {
                            address = IBAN.IBAN2Address(address);
                        }
                        String addressNoPrefix = Numeric.cleanHexPrefix(address);
                        if (addressNoPrefix.contains("0x")) {
                            int index = addressNoPrefix.indexOf("0x") + 2;
                            addressNoPrefix = addressNoPrefix.substring(index);
                        }
                    }
                    if (address.contains("iban:"))
                        address.replace("iban:", "");
                    etReceivedAddress.setText(address);
                    break;
            }
        }
    }

    private boolean validAddress(String address) {
        if (!EthWalltUtils.isValidAddress(address)) {
            MToast.showLongToast(getResources().getString(R.string.address_wrong));
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
