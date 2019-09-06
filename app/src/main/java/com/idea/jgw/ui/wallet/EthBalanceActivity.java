package com.idea.jgw.ui.wallet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.BaseEthResponse;
import com.idea.jgw.common.Common;
import com.idea.jgw.dialog.LoadingDialog;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.btc.model.TLCoin;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.eth.data.TransactionDisplay;
import com.idea.jgw.logic.eth.network.EtherscanAPI;
import com.idea.jgw.logic.eth.utils.RequestCache;
import com.idea.jgw.logic.eth.utils.ResponseParser;
import com.idea.jgw.service.MessageEvent;
import com.idea.jgw.ui.main.adapter.TransferRecordListAdapter;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by vam on 2018\6\4 0004.
 */
@Route(path = RouterPath.BALANCE_ETH_ACTIITY)
public class EthBalanceActivity extends BalanceActivity {

    public BigDecimal mCurAvailable;
    public String mCurAddress;

    protected List<TransactionDisplay> wallets = new ArrayList<>();
    protected int requestCount = 0;  // used to count to two (since internal and normal transactions are each one request). Gets icnreased once one request is finished. If it is two, notifyDataChange is called (display transactions)
    private long unconfirmed_addedTime;
    protected TransactionDisplay unconfirmed;

    String ethAddress;
    String sort = "desc";
    int page = 1;//一般交易查询页数，从1开始
    int internalPage = 1;//internal查询页数，从1开始
    int pageSize = 10;//一页区块大小
    boolean moreTracscation;
    boolean moreInternalTracscation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transferRecordListAdapter = new TransferRecordListAdapter();
        transferRecordListAdapter.addDatas(wallets);
        transferRecordListAdapter.setOnItemClickListener(this);
        rvOfTransferRecord.setAdapter(transferRecordListAdapter);

        ivOfLogo.setImageResource(R.mipmap.icon_eth);

        if(getIntent().hasExtra((EXTRA_ADDRESS))) {
            ethAddress = "0x" + getIntent().getStringExtra(EXTRA_ADDRESS);
        }
        if(getIntent().hasExtra((EXTRA_AMOUNT))) {
            String balance = getIntent().getStringExtra(EXTRA_AMOUNT);
            tvOfUsableBalanceValue.setText(getBigDecimalText(new BigDecimal(balance)));
        } else {
            tvOfUsableBalanceValue.setText("0");
        }

//        ethAddress = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_ADDRESS_KEY, "").toString();
        //钱包为空
        if (TextUtils.isEmpty(ethAddress)) {

        } else {
            getData(true);


        }
    }

    private void getData(boolean showDialog) {

        //
        //这里需要优化下
        //当 String balance = getIntent().getStringExtra(EXTRA_AMOUNT);不为空时，不需要再去请求数据
        //获取姨太的金额
        getEthBanlance(showDialog);
    }

    @Override
    protected void refreshData() {
        page = 1;
        internalPage = 1;
        getData(false);
    }

    @Override
    protected void loadMoreData() {
        getData(false);
    }

    @Override
    public void sendCoinState(MessageEvent messageEvent) {
        if (isDestroyed() || isFinishing()) return;
        if (messageEvent.getCoinType() == Common.CoinTypeEnum.ETH && messageEvent.getState() == MessageEvent.STAE_SUCCES) {
            String tranId = messageEvent.getTranId();
            forTag:
            for (TransactionDisplay transactionDisplay : wallets) {
                if (tranId.equals(transactionDisplay.getTxHash())) {
                    transactionDisplay.setConfirmationStatus(16);
                    break forTag;
                }
            }
            transferRecordListAdapter.replaceData(wallets);

        }
    }

    @Override
    public void initView() {
        super.initView();
        tvOfTitle.setText(R.string.eth);
        ivOfLogo.setImageResource(R.mipmap.icon_eth);
        rvOfTransferRecord.setLoadingMoreEnabled(true);
    }

    private void getEthBanlance(boolean showDialog) {
        if (showDialog)
            LoadingDialog.showDialogForLoading(this);
        mCurAddress = ethAddress;
        EthWalltUtils.getCurAvailable(EthBalanceActivity.this, mCurAddress,
                new TLCallback() {
                    @Override
                    public void onSuccess(Object obj) {
                        if (null == obj) return;
                        String str = obj.toString();
//                    BigInteger bi = new BigInteger(new BigInteger(str,16).toString(10));
                        BigInteger bi = new BigInteger(str);
                        BigDecimal bd = new BigDecimal(10).pow(18);
                        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
//                    df.setMaximumFractionDigits(18);
                        BigDecimal amount = new BigDecimal(bi.toString(10)).divide(bd);
//                        String balance = df.format(amount.doubleValue());

                        mCurAvailable = amount;

                        tvOfUsableBalanceValue.setText(getBigDecimalText(amount));
                        //获取交易记录
                        getTransactionRecord(false);
                    }

                    @Override
                    public void onFail(Integer status, String error) {
                        MToast.showToast(error);
                        //获取交易记录
                        getTransactionRecord(false);
                    }

                    @Override
                    public void onSetHex(String hex) {

                    }

                    @Override
                    public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

                    }
                });


//                new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Toast.makeText(EthBalanceActivity.this, "获取不到钱包信息", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            mCurAvailable = new BigDecimal(ResponseParser.parseBalance(response.body().string(), 18));
//                            tvOfUsableBalanceValue.setText(mCurAvailable.toString());
//                        } catch (Exception e) {
//                            Toast.makeText(EthBalanceActivity.this, "获取不到钱包信息", Toast.LENGTH_LONG).show();
////                                        ac.snackError("Cant fetch your account balance");
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }
//});
    }


    @Override
    public void onSendCoin() {
        ARouter.getInstance()
                .build(RouterPath.SEND_ETH_ACTIVITY)
                .withString(EthSendActivity.EXTRA_ADDRESS, (TextUtils.isEmpty(mCurAddress) ? "" : mCurAddress))
                .withString(EthSendActivity.EXTRA_CUR_AMOUNT, (null == mCurAvailable ? "" : mCurAvailable.toString()))
                .navigation(EthBalanceActivity.this, EthSendActivity.REQ_CODE);
    }

    @Override
    public void onReceivedCoin() {
        ARouter.getInstance().build(RouterPath.RECEIVED_ETH_ACTIVITY)
                .withString(EthReceivedActivity.EXTRA_ADDRESS, (TextUtils.isEmpty(mCurAddress) ? "" : mCurAddress))
                .withString(EthReceivedActivity.EXTRA_CUR_AMOUNT, (null == mCurAvailable ? "" : mCurAvailable.toString()))
                .navigation(EthBalanceActivity.this, EthReceivedActivity.REQ_CODE);
    }


    LocalRceiver mLocalReceiver;
    IntentFilter mIntentFileter;
    private LocalBroadcastManager mLocalBroadcastManager;


    @Override
    protected void onResume() {
        register();
        super.onResume();

    }

    private void register() {
        if (null == mLocalBroadcastManager)
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        if (null == mIntentFileter) {
            mIntentFileter = new IntentFilter();
            mIntentFileter.addAction(Common.Broadcast.SEND_ETH_RESULT); //发送以太的广播
        }

        if (null == mLocalReceiver)
            mLocalReceiver = new LocalRceiver();
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, mIntentFileter);
    }

    public class LocalRceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Common.Broadcast.SEND_ETH_RESULT.equals(intent.getAction())) {
                boolean result = intent.getBooleanExtra(Common.Broadcast.SEND_ETH_RESULT_DATA, false);
                MToast.showLongToast(getResources().getString(result ? R.string.send_eth_result_success : R.string.send_eth_result_fail));
            }
        }

    }

    @Override
    protected void onPause() {
        if (null != mLocalBroadcastManager && null != mLocalReceiver) {
            mLocalBroadcastManager.unregisterReceiver(mLocalReceiver);
        }
        super.onPause();
    }

    void getTransactionRecord() {
        disposables.add(ServiceApi.getInstance().getApiService()
                .getInternalTransactions(ethAddress, page, pageSize, sort, ServiceApi.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseEthResponse>() {
                    @Override
                    public void accept(BaseEthResponse baseEthResponse) throws Exception {
                        if(baseEthResponse.getStatus() == 0) {
                            String result = baseEthResponse.getResult();
                            if(!TextUtils.isEmpty(result)) {

                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }


    /**
     * 获取交易信息
     *
     * @param force true：刷新（有加载过数据的时候），false 默认加载
     */
    private void getTransactionRecord(final boolean force) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getNormalTX();
            }
        }).start();
    }

    private void getNormalTX() {
        EtherscanAPI.getInstance().getNormalTransactions(ethAddress, page, pageSize, sort, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getInternalTx();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String restring = response.body().string();
                MyLog.e("response--->>" + restring);
                System.out.println("response--->>" + restring);
                if (restring != null && restring.length() > 2)
                    RequestCache.getInstance().put(RequestCache.TYPE_TXS_NORMAL, ethAddress, restring);
                final ArrayList<TransactionDisplay> w = new ArrayList<TransactionDisplay>(ResponseParser.parseTransactions(restring, "Unnamed Address", ethAddress, TransactionDisplay.NORMAL));
                int size = w.size();
                if(size < pageSize) {
                    moreTracscation = false;
                } else {
                    moreTracscation = true;
                }
                if(internalPage == 1 && page == 1) {
                    getWallets().clear();
                }
                if(size > 0) {
                    page ++;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onComplete(w);
                        getInternalTx();
                    }
                });
            }
        });
    }

    void getInternalTx() {
            EtherscanAPI.getInstance().getInternalTransactions(ethAddress, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshComplete();
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String restring = response.body().string();
                    if (restring != null && restring.length() > 2)
                        RequestCache.getInstance().put(RequestCache.TYPE_TXS_INTERNAL, ethAddress, restring);
                    final ArrayList<TransactionDisplay> w = new ArrayList<TransactionDisplay>(ResponseParser.parseTransactions(restring, "Unnamed Address", ethAddress, TransactionDisplay.CONTRACT));
                    int size = w.size();
                    if(size < pageSize) {
                        moreInternalTracscation = false;
                    } else {
                        moreInternalTracscation = true;
                    }
                    if(internalPage == 1 && page == 1) {
                        getWallets().clear();
                    }
                    if(size > 0) {
                        internalPage ++;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onComplete(w);
                            refreshComplete();
                        }
                    });

                }
            });
    }

    private void refreshComplete() {
        LoadingDialog.cancelDialogForLoading();
        if(moreInternalTracscation || moreTracscation) {
            rvOfTransferRecord.setLoadingMoreEnabled(true);
        } else {
            rvOfTransferRecord.setLoadingMoreEnabled(false);
        }
        rvOfTransferRecord.refreshComplete();
        rvOfTransferRecord.loadMoreComplete();
    }

    private void onComplete(ArrayList<TransactionDisplay> w) {
        addToWallets(w);
        addRequestCount();
        onItemsLoadComplete();

        // If transaction was send via App and has no confirmations yet (Still show it when users refreshes for 10 minutes)
        if (unconfirmed_addedTime + 10 * 60 * 1000 < System.currentTimeMillis()) // After 10 minutes remove unconfirmed (should now have at least 1 confirmation anyway)
            unconfirmed = null;
        if (unconfirmed != null && wallets.size() > 0) {
            if (wallets.get(0).getAmount() == unconfirmed.getAmount()) {
                unconfirmed = null;
            } else {
                wallets.add(0, unconfirmed);
            }
        }


        if (null != transferRecordListAdapter) {
            transferRecordListAdapter.replaceData(wallets);
        }
    }

    public synchronized List<TransactionDisplay> getWallets() {
        return wallets;
    }

    public synchronized void addToWallets(List<TransactionDisplay> w) {
        wallets.addAll(w);
        Collections.sort(getWallets(), new Comparator<TransactionDisplay>() {
            @Override
            public int compare(TransactionDisplay o1, TransactionDisplay o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public synchronized void addRequestCount() {
        requestCount++;
    }

    public synchronized int getRequestCount() {
        return requestCount;
    }


    void onItemsLoadComplete() {
//        if (swipeLayout == null) return;
//        swipeLayout.setRefreshing(false);
    }


    /**
     * 增加交易记录
     *
     * @param from
     * @param to
     * @param amount
     */
    public void addUnconfirmedTransaction(String from, String to, BigInteger amount) {
        unconfirmed = new TransactionDisplay(from, to, amount, 0, System.currentTimeMillis(), "", TransactionDisplay.NORMAL, "", "0", 0, 1, 1, false);
        unconfirmed.setCoinType(Common.CoinTypeEnum.ETH);
        unconfirmed_addedTime = System.currentTimeMillis();
        wallets.add(0, unconfirmed);
        transferRecordListAdapter.notifyDataSetChanged();
//        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, Object data) {

        TransactionDisplay td = (TransactionDisplay) data;
        ARouter.getInstance().build(RouterPath.TRANSACTION_DETAIL_ACTIVITY)
//                .withObject(EXTRA_DETAIL_OBJECT,data)
                .withSerializable(TransactionDetailActivity.EXTRA_DETAIL_OBJECT, td)
                .withInt(TransactionDetailActivity.EXTRA_COIN_TYPE, Common.CoinTypeEnum.ETH.getIndex())
                .navigation();
    }
}
