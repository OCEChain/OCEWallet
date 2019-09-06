package com.idea.jgw.logic.eth.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.idea.jgw.R;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.eth.network.EtherscanAPI;
import com.idea.jgw.logic.eth.utils.ExchangeCalculator;
import com.idea.jgw.logic.eth.utils.WalletStorage;
import com.idea.jgw.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TransactionService extends IntentService {

    private NotificationCompat.Builder builder;
    final int mNotificationId = 153;

    public TransactionService() {
        super("Transaction Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification();
        try {
            String fromAddress = intent.getStringExtra("FROM_ADDRESS");
            final String toAddress = intent.getStringExtra("TO_ADDRESS");
            final String amount = intent.getStringExtra("AMOUNT");
            final String gas_price = intent.getStringExtra("GAS_PRICE");
            final String gas_limit = intent.getStringExtra("GAS_LIMIT");
            final String data = intent.getStringExtra("DATA");
            String password = intent.getStringExtra("PASSWORD");

            final Credentials keys = WalletStorage.getInstance(getApplicationContext()).getFullWallet(getApplicationContext(), password, fromAddress);

            EtherscanAPI.getInstance().getNonceForAddress(fromAddress, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    error("Can't connect to network, retry it later");sendBroadReceiver(TransactionService.this,false);
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    try {
                        JSONObject o = new JSONObject(response.body().string());
                        BigInteger nonce = new BigInteger(o.getString("result").substring(2), 16);
//                        if(nonce.compareTo(new BigInteger("0")) ==0){
//                            nonce =new BigInteger("1");
//                        }

                        RawTransaction tx = RawTransaction.createTransaction(
                                nonce,
                                new BigInteger(gas_price),
                                new BigInteger(gas_limit),
                                toAddress,
                                new BigDecimal(amount).multiply(ExchangeCalculator.ONE_ETHER).toBigInteger(),
                                data
                        );

                        Log.d("txx",
                                "Nonce: " + tx.getNonce() + "\n" +
                                        "gasPrice: " + tx.getGasPrice() + "\n" +
                                        "gasLimit: " + tx.getGasLimit() + "\n" +
                                        "To: " + tx.getTo() + "\n" +
                                        "Amount: " + tx.getValue() + "\n" +
                                        "Data: " + tx.getData()
                        );

                        byte[] signed = TransactionEncoder.signMessage(tx, (byte) 1, keys);

                        forwardTX(signed);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error("Can't connect to network, retry it later");
                    }
                }
            });

        } catch (Exception e) {
            error("Invalid Wallet Password!");
            e.printStackTrace();
        }
    }

    private void forwardTX(byte[] signed) throws IOException {
        EtherscanAPI.getInstance().forwardTransaction("0x" + Hex.toHexString(signed), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                error("Can't connect to network, retry it later");  sendBroadReceiver(TransactionService.this,false);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String received = response.body().string();
                try {
                    suc(new JSONObject(received).getString("result"));
                    sendBroadReceiver(TransactionService.this,true);
                } catch (Exception e) {
                    // Advanced error handling. If etherscan returns error message show the shortened version in notification. Else abbort with unknown error
                    try {
                        String errormsg = new JSONObject(received).getJSONObject("error").getString("message");
                        sendBroadReceiver(TransactionService.this,false);
                        if (errormsg.indexOf(".") > 0)
                            errormsg = errormsg.substring(0, errormsg.indexOf("."));
                        error(errormsg); // f.E Insufficient funds
                    } catch (JSONException e1) {
                        error("Unknown error occured");
                    }
                }
            }
        });
    }

    /**
     * 发送广播
     * @param context
     * @param result
     */
    private void sendBroadReceiver(Context context, boolean result) {
        Intent intent = new Intent(Common.Broadcast.SEND_ETH_RESULT);
        intent.putExtra(Common.Broadcast.SEND_ETH_RESULT_DATA, result);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void suc(String hash) {
//        builder
//                .setContentTitle(getString(R.string.notification_transfersuc))
//                .setProgress(100, 100, false)
//                .setOngoing(false)
//                .setAutoCancel(true)
//                .setContentText("");
//
//        Intent main = new Intent(this, MainActivity.class);
//        main.putExtra("STARTAT", 2);
//        main.putExtra("TXHASH", hash);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                main, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//
//        final NotificationManager mNotifyMgr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        mNotifyMgr.notify(mNotificationId, builder.build());
    }

    private void error(String err) {
//        builder
//                .setContentTitle(getString(R.string.notification_transferfail))
//                .setProgress(100, 100, false)
//                .setOngoing(false)
//                .setAutoCancel(true)
//                .setContentText(err);
//
//        Intent main = new Intent(this, MainActivity.class);
//        main.putExtra("STARTAT", 2);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                main, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//
//        final NotificationManager mNotifyMgr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        mNotifyMgr.notify(mNotificationId, builder.build());
    }

    private void sendNotification() {
//        builder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                .setColor(0x2d435c)
//                .setTicker(getString(R.string.notification_transferingticker))
//                .setContentTitle(getString(R.string.notification_transfering_title))
//                .setContentText(getString(R.string.notification_might_take_a_minute))
//                .setOngoing(true)
//                .setProgress(0, 0, true);
//        final NotificationManager mNotifyMgr =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        mNotifyMgr.notify(mNotificationId, builder.build());
    }


}