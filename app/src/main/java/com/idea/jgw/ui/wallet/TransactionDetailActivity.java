package com.idea.jgw.ui.wallet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.eth.data.TransactionDisplay;
import com.idea.jgw.logic.eth.utils.AddressNameConverter;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 交易详情页面
 */
@Route(path = RouterPath.TRANSACTION_DETAIL_ACTIVITY)
public class TransactionDetailActivity extends BaseActivity {

    public static final String EXTRA_DETAIL_OBJECT = "TransactionDetailActivity.EXTRA_DETAIL_OBJECT";
    public static final String EXTRA_COIN_TYPE = "TransactionDetailActivity.EXTRA_COIN_TYPE";

    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_send_time)
    TextView tvSendTime;
    @BindView(R.id.tv_transaction_number)
    TextView tvTransactionNumber;
    @BindView(R.id.tv_transaction_status)
    TextView tvTransactionStatus;
    @BindView(R.id.tv_send_address)
    TextView tvSendAddress;
    @BindView(R.id.tv_copy_send_address)
    TextView tvCopySendAddress;
    @BindView(R.id.tv_received_address)
    TextView tvReceivedAddress;
    @BindView(R.id.tv_copy_received_address)
    TextView tvCopyReceivedAddress;
    @BindView(R.id.tv_transaction_id)
    TextView tvTransactionId;
    @BindView(R.id.tv_commission)
    TextView tvCommission;
    @BindView(R.id.tv_chain_number)
    TextView tvChainNumber;
    @BindView(R.id.tv_send_label)
    TextView tvSendLabel;
    @BindView(R.id.iv_coin_logo)
    ImageView ivCoinLogo;
    ClipboardManager mClipboardManager;


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired(name = EXTRA_COIN_TYPE)
    int coinType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transcation_detail;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.transaction_detail);

        TransactionDisplay td = (TransactionDisplay) getIntent().getSerializableExtra(EXTRA_DETAIL_OBJECT);
        //根据货币类型做处理
        coinType = getIntent().getIntExtra(EXTRA_COIN_TYPE, -1);
        if (coinType == Common.CoinTypeEnum.BTC.getIndex()) {
            ivCoinLogo.setImageResource(R.mipmap.icon_btc);
        } else if (coinType == Common.CoinTypeEnum.ETH.getIndex()) {
            ivCoinLogo.setImageResource(R.mipmap.icon_eth);
            setDataToValue(td);
        } else if (coinType == Common.CoinTypeEnum.JGW.getIndex()) {
            ivCoinLogo.setImageResource(R.mipmap.icon_oce);
            setDataToValue(td);

        } else if (coinType == Common.CoinTypeEnum.OCE.getIndex()) {
            ivCoinLogo.setImageResource(R.mipmap.icon_oce);
            setOceData(td);
        }
    }

    private void setOceData(TransactionDisplay td) {
        BigDecimal bd = new BigDecimal(10).pow(18);
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(18);

        tvTransactionId.setText(getResources().getString(R.string.transaction_id) + ":" + td.getTxHash());
        tvChainNumber.setText(getResources().getString(R.string.chain_number) + ":" + td.getBlock());
        tvSendTime.setText(sdf.format(new Date(td.getDate())));
        BigDecimal gas = new BigDecimal(td.getGasUsed() * td.getGasprice()).divide(bd);
        tvCommission.setText(getResources().getString(R.string.commission) + ":"+td.getBrokerage());
        BigDecimal amount = new BigDecimal(td.getAmountNative()).divide(bd);

        // amount > 0 表示接收，< 0表示发送
        if (td.getAddress().toUpperCase().equals(td.getFromAddress().toUpperCase())) {
//        if(amount.doubleValue() < 0){
            tvTransactionNumber.setText("-" + df.format(td.getAmount2()));
            tvSendLabel.setText(R.string.send);
            tvReceivedAddress.setText(td.getToAddress());
            tvSendAddress.setText(td.getFromAddress());
        } else {
            tvSendLabel.setText(R.string.received);
            tvSendAddress.setText(td.getToAddress());
            tvReceivedAddress.setText(td.getFromAddress());
            tvTransactionNumber.setText("+" + df.format(td.getAmount2()));
        }
    }

    private void setDataToValue(TransactionDisplay td) {
        BigDecimal bd = new BigDecimal(10).pow(18);
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        df.setMaximumFractionDigits(18);

        tvTransactionId.setText(getResources().getString(R.string.transaction_id) + ":" + td.getTxHash());
        tvChainNumber.setText(getResources().getString(R.string.chain_number) + ":" + td.getBlock());
        tvSendTime.setText(sdf.format(new Date(td.getDate())));
        BigDecimal gas = new BigDecimal(td.getGasUsed() * td.getGasprice()).divide(bd);
        tvCommission.setText(getResources().getString(R.string.commission) + ":" + df.format(gas.doubleValue()));
        BigDecimal amount = new BigDecimal(td.getAmountNative()).divide(bd);
        tvTransactionNumber.setText(df.format(amount.doubleValue()));
        if(td.isError()) {
            tvTransactionStatus.setText(R.string.fail);
        } else {
            tvTransactionStatus.setText(R.string.success);
        }
        // amount > 0 表示接收，< 0表示发送
        if (td.getAmount() > 0) {
            tvSendLabel.setText(R.string.received);
            tvSendAddress.setText(td.getToAddress());
            tvReceivedAddress.setText(td.getFromAddress());
        } else {
            tvSendLabel.setText(R.string.send);
            tvReceivedAddress.setText(td.getToAddress());
            tvSendAddress.setText(td.getFromAddress());
        }
    }

    @OnClick({R.id.btn_of_back, R.id.tv_copy_send_address, R.id.tv_copy_received_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.tv_copy_send_address:
                copyText(tvSendAddress);
                MToast.showLongToast(R.string.copy_success);
                break;
            case R.id.tv_copy_received_address:
                copyText(tvReceivedAddress);
                MToast.showLongToast(R.string.copy_success);
                break;
        }
    }

    private void copyText(TextView textView) {
        if (mClipboardManager == null) {
            mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("address", textView.getText().toString().trim());
        mClipboardManager.setPrimaryClip(clipData);



    }
}
