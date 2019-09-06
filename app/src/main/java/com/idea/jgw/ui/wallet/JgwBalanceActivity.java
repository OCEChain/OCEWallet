package com.idea.jgw.ui.wallet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.protobuf.MessageLiteOrBuilder;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.common.Common;
import com.idea.jgw.dialog.LoadingDialog;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.btc.model.TLCoin;
import com.idea.jgw.logic.eth.data.TransactionDisplay;
import com.idea.jgw.logic.jgw.JgwUtils;
import com.idea.jgw.service.GetSendStatusService;
import com.idea.jgw.service.MessageEvent;
import com.idea.jgw.ui.main.adapter.JgwTransferRecordListAdapter;
import com.idea.jgw.ui.main.adapter.TransferRecordListAdapter;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vam on 2018\6\4 0004.
 */

@Route(path = RouterPath.BALANCE_JGW_ACTIITY)
public class JgwBalanceActivity extends BalanceActivity {

    String address;
    String balance;

    JgwTransferRecordListAdapter transferRecordListAdapter;
    protected List<TransactionDisplay> wallets = new ArrayList<>();

    final JgwUtils ju = new JgwUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transferRecordListAdapter = new JgwTransferRecordListAdapter();
        transferRecordListAdapter.addDatas(wallets);
        transferRecordListAdapter.setOnItemClickListener(this);
        rvOfTransferRecord.setAdapter(transferRecordListAdapter);
    }

    @Override
    protected void refreshData() {
        getTX(false);
    }

    @Override
    protected void loadMoreData() {

    }

    @Override
    public void sendCoinState(MessageEvent messageEvent) {
        if (isDestroyed() || isFinishing()) return;
        if (messageEvent.getCoinType() == Common.CoinTypeEnum.JGW && messageEvent.getState() == MessageEvent.STAE_SUCCES) {
//            String tranId = messageEvent.getTranId();
//            forTag:
//            for (TransactionDisplay transactionDisplay : wallets) {
//                if (tranId.equals(transactionDisplay.getTxHash())) {
//                    transactionDisplay.setConfirmationStatus(16);
//                    break forTag;
//                }
//            }
//            transferRecordListAdapter.replaceData(wallets);

            getTX(false);
        }
    }

    @Override
    public void initView() {
        super.initView();

        tvOfTitle.setText(R.string.jgw);
        ivOfLogo.setImageResource(R.mipmap.icon_oce);


        balance = getIntent().getStringExtra(JgwBalanceActivity.EXTRA_AMOUNT);
        if (TextUtils.isEmpty(balance)) {
            getBalance();
        } else {
            tvOfUsableBalanceValue.setText(balance);
        }

        address = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_ADDRESS_KEY, "").toString();
        if (TextUtils.isEmpty(address)) {
            MToast.showLongToast(R.string.jgw_get_address_err);
            finish();
            return;
        }
        getTX(true);
    }


    private void getTX(boolean showDialog) {
        if(showDialog) {
            LoadingDialog.showDialogForLoading(this);
        }
        ju.queryTX(address, new TLCallback() {
            @Override
            public void onSuccess(Object obj) {
                if (null == obj)
                    return;
                wallets = (List<TransactionDisplay>) obj;
                transferRecordListAdapter.replaceData(wallets);
                LoadingDialog.cancelDialogForLoading();
                rvOfTransferRecord.refreshComplete();
            }

            @Override
            public void onFail(Integer status, String error) {
                MToast.showToast(error);
                LoadingDialog.cancelDialogForLoading();
                rvOfTransferRecord.refreshComplete();
            }

            @Override
            public void onSetHex(String hex) {

            }

            @Override
            public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

            }
        });
    }

    private void getBalance() {

        ju.queryBalance(address, new TLCallback() {
            @Override
            public void onSuccess(Object obj) {
                try {
                    String str = obj.toString();
                    BigInteger bi = new BigInteger(str);
                    BigDecimal bd = new BigDecimal(10).pow(18);
                    DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
                    BigDecimal amount = new BigDecimal(bi.toString(10)).divide(bd);
                    balance = df.format(amount.doubleValue());
                    tvOfUsableBalanceValue.setText(balance);
                } catch (Exception e) {
                    tvOfUsableBalanceValue.setText("0");
                }

            }

            @Override
            public void onFail(Integer status, String error) {
                MToast.showLongToast(R.string.jgw_query_balance_coin_eror);
            }

            @Override
            public void onSetHex(String hex) {

            }

            @Override
            public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

            }
        });

    }

    @Override
    public void onSendCoin() {
        ARouter.getInstance()
                .build(RouterPath.SEND_JGW_ACTIVITY)
                .withString(JgwSendActivity.EXTRA_BALANCE_KEY, balance)
                .withString(JgwReceivedActivity.EXTRA_ADDRESS, address)
                .navigation(JgwBalanceActivity.this, EthSendActivity.REQ_CODE);
    }

    @Override
    public void onReceivedCoin() {
        ARouter.getInstance().build(RouterPath.RECEIVED_JGW_ACTIVITY)
                .navigation(JgwBalanceActivity.this, EthReceivedActivity.REQ_CODE);
    }


    @Override
    public void onItemClick(int position, Object data) {
        TransactionDisplay td = (TransactionDisplay) data;
        ARouter.getInstance().build(RouterPath.TRANSACTION_DETAIL_ACTIVITY)
//                .withObject(EXTRA_DETAIL_OBJECT,data)
                .withSerializable(TransactionDetailActivity.EXTRA_DETAIL_OBJECT, td)
                .withInt(TransactionDetailActivity.EXTRA_COIN_TYPE, Common.CoinTypeEnum.JGW.getIndex())
                .navigation();
    }

}
