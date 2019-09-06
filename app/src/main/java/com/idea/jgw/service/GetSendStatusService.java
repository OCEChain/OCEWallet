package com.idea.jgw.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.idea.jgw.App;
import com.idea.jgw.common.Common;
import com.idea.jgw.utils.WorkQueue;

import org.greenrobot.eventbus.EventBus;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.Future;

import static com.idea.jgw.service.MessageEvent.STAE_SUCCES;

/**
 * 获取发送状态(暂时只支持eth跟eth代币)
 */
public class GetSendStatusService extends Service {

    public static final String EXTRA_TRANSACTION_ID = "EXTRA_TRANSACTION_ID";
    public static final String EXTRA_COIN_TYPE = "COIN_TYPE";

    /**
     * 启动service
     *
     * @param coinType
     * @param tranId
     */
    public static final void startNewService(Common.CoinTypeEnum coinType, String tranId) {
        if (TextUtils.isEmpty(tranId)) return;
        if (coinType == null) return;

        Context context = App.getInstance();
        Intent intent = new Intent(context, GetSendStatusService.class);
        intent.putExtra(EXTRA_TRANSACTION_ID, tranId);
        intent.putExtra(EXTRA_COIN_TYPE, coinType);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    WorkQueue workQueue;

    @Override
    public void onCreate() {
        workQueue = new WorkQueue(1);
        super.onCreate();
    }


    class Task implements Runnable {

        String tranId;
        Common.CoinTypeEnum coinType;

        public Task(Common.CoinTypeEnum coinType, String tranId) {
            this.coinType = coinType;
            this.tranId = tranId;
        }

        @Override
        public void run() {
            Web3j web3j = Web3jFactory.build(new HttpService(Common.Jgw.URL));
            try {
                Future<EthTransaction> ethTransactionFuture = web3j.ethGetTransactionByHash(tranId).sendAsync();
                BigInteger blockNumber = ethTransactionFuture.get().getTransaction().getBlockNumber();
                if (0 != blockNumber.intValue()) {
                    EventBus.getDefault().post(new MessageEvent(coinType, STAE_SUCCES, tranId));
                } else {
                    exec(this);
                }
            } catch (Exception e) {
                exec(this);
            }
        }
    }

    HashMap<String, Integer> map = new HashMap<>();
    static final int MAX_REQ = 3;
    static final long SLEEP_TIME = 1000;

    private void exec(Task run) {

        //1个任务只能有3次请求
        if (map.containsKey(run.tranId)) {
            int count = map.get(run.tranId);
            if (count == MAX_REQ) {
                return;
            }
            count++;
            map.put(run.tranId, count);
        } else {
            map.put(run.tranId, 1);
        }

        //请求数据
        try {
            Thread.sleep(SLEEP_TIME);
            workQueue.execute(run);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(EXTRA_TRANSACTION_ID)) {
            if (null == workQueue) {
                workQueue = new WorkQueue(1);
            }
            String tranId = intent.getStringExtra(EXTRA_TRANSACTION_ID);
            Common.CoinTypeEnum coinType = (Common.CoinTypeEnum) intent.getSerializableExtra(EXTRA_COIN_TYPE);
            workQueue.execute(new Task(coinType, tranId));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
