package com.idea.jgw.ui.wallet;

import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSONObject;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.OceApi;
import com.idea.jgw.api.retrofit.OceServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.common.Common;
import com.idea.jgw.dialog.LoadingDialog;
import com.idea.jgw.logic.eth.data.TransactionDisplay;
import com.idea.jgw.service.MessageEvent;
import com.idea.jgw.ui.main.adapter.JgwTransferRecordListAdapter;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.idea.jgw.ui.main.fragment.WalletFragment.OCE_ADDRESS;
import static com.idea.jgw.ui.main.fragment.WalletFragment.OCE_PRIVATE_KEY;

/**
 * Created by vam on 2018\6\4 0004.
 * <p>
 * com.idea.jgw.ui.wallet.OceBalanceActivity
 */

@Route(path = RouterPath.BALANCE_OCE_ACTIITY)
public class OceBalanceActivity extends BalanceActivity {

    String address;
    String balance;
    String usable;

    JgwTransferRecordListAdapter transferRecordListAdapter;
    protected List<TransactionDisplay> wallets = new ArrayList<>();


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
        getBalance(false);
    }

    @Override
    protected void loadMoreData() {

    }

    @Override
    public void sendCoinState(MessageEvent messageEvent) {
        if (isDestroyed() || isFinishing()) return;
        if (messageEvent.getCoinType() == Common.CoinTypeEnum.OCE && messageEvent.getState() == MessageEvent.STAE_SUCCES) {
            if (!isFinishing()) {
                getBalance(false);
            }
        }
    }

    @Override
    public void initView() {
        super.initView();

        tvOfTitle.setText(R.string.jgw);
        ivOfLogo.setImageResource(R.mipmap.oce);
        balance = getIntent().getStringExtra(BalanceActivity.EXTRA_AMOUNT);
        usable = getIntent().getStringExtra(BalanceActivity.EXTRA_USABLE);
        address = (String) SPreferencesHelper.getInstance(this).getData(OCE_ADDRESS, "");
        String privateKey = (String) SPreferencesHelper.getInstance(this).getData(OCE_PRIVATE_KEY, "");

        if (TextUtils.isEmpty(balance)) {
//            getBalance(address);
        } else {
            tvOfUsableBalanceValue.setText(balance);

            try {
                int b = Integer.valueOf(balance);
                int u = Integer.valueOf(usable);
                if (b > u && b > 0) {
                    tvOfFrozenBalanceValue.setText(String.valueOf(b - u));
                }
            } catch (Exception e) {

            }
        }


        if (TextUtils.isEmpty(address)) {
            MToast.showLongToast(R.string.get_oce_address_failed);
            finish();
            return;
        }

        getBalance(true);
    }


    private void getTX(final String address) {

        disposables.add(OceServiceApi.getInstance(OceApi.URL).getApiService().gettranslist(address).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(OceBalanceActivity.this) {
                    @Override
                    protected void _onNext(BaseResponse response) {
                        wallets.clear();
                        com.alibaba.fastjson.JSONArray arr = (com.alibaba.fastjson.JSONArray) response.getInfo();
                        TransactionDisplay td;
                        int len = arr.size();
                        for (int i = 0; i < len; i++) {
                            td = new TransactionDisplay();
                            JSONObject obj = arr.getJSONObject(i);
                            String a = String.valueOf(obj.getIntValue("number"));
                            td.setToAddress(obj.getString("to_address"));
                            td.setFromAddress(obj.getString("from_address"));
                            td.setAmount(new BigInteger(String.valueOf(obj.getIntValue("number"))));
                            td.setDate(obj.getLong("trans_time") * 1000);
                            td.setTxHash(obj.getString("tx_id"));
                            td.setConfirmationStatus(13);
                            td.setAddress(address);
                            td.setBlock(obj.getLong("block"));
                            td.setCoinType(Common.CoinTypeEnum.OCE);
                            td.setBrokerage(obj.getString("brokerage"));
                            wallets.add(td);
                        }

                        transferRecordListAdapter.replaceData(wallets);
                        rvOfTransferRecord.refreshComplete();
                        LoadingDialog.cancelDialogForLoading();
                    }

                    @Override
                    protected void _onError(String message) {
                        MyLog.e("xxx", message);
                        MToast.showToast(message);
                        rvOfTransferRecord.refreshComplete();
                        LoadingDialog.cancelDialogForLoading();
                    }
                }));
    }


    private void getBalance(boolean showDialog) {
        if(showDialog) {
            LoadingDialog.showDialogForLoading(this);
        }
        OceServiceApi.getInstance(OceApi.URL).getApiService().getinfo(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxSubscriber<BaseResponse>(OceBalanceActivity.this) {
                    @Override
                    protected void _onNext(BaseResponse response) {
                        com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) response.getInfo();
                        Integer num = obj.getInteger("Number");
                        Integer usa = obj.getInteger("Usable");
                        balance = String.valueOf(num);
                        usable = String.valueOf(usa);

                        tvOfUsableBalanceValue.setText(balance);
                        if (num > usa && usa > 0) {
                            tvOfFrozenBalanceValue.setText(String.valueOf(num - usa));
                        }
                        getTX(address);
                    }

                    @Override
                    protected void _onError(String message) {
                        MyLog.e("xxx", message);
                        getTX(address);
                    }
                });
    }

    @Override
    public void onSendCoin() {
        ARouter.getInstance()
                .build(RouterPath.SEND_OCE_ACTIVITY)
                .withString(JgwSendActivity.EXTRA_BALANCE_KEY, balance)
                .withString(JgwReceivedActivity.EXTRA_ADDRESS, address)
                .navigation(OceBalanceActivity.this, EthSendActivity.REQ_CODE);
    }

    @Override
    public void onReceivedCoin() {
        ARouter.getInstance().build(RouterPath.RECEIVED_OCE_ACTIVITY)
                .withString(WalletAddressActivity.EXTRA_ADDRESS, address)
                .navigation(OceBalanceActivity.this, EthReceivedActivity.REQ_CODE);
    }


    @Override
    public void onItemClick(int position, Object data) {
        TransactionDisplay td = (TransactionDisplay) data;
        ARouter.getInstance().build(RouterPath.TRANSACTION_DETAIL_ACTIVITY)
//                .withObject(EXTRA_DETAIL_OBJECT,data)
                .withSerializable(TransactionDetailActivity.EXTRA_DETAIL_OBJECT, td)
                .withInt(TransactionDetailActivity.EXTRA_COIN_TYPE, Common.CoinTypeEnum.OCE.getIndex())
                .navigation();
    }

}
