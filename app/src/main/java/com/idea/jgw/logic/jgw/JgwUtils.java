package com.idea.jgw.logic.jgw;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.idea.jgw.App;
import com.idea.jgw.bean.Coin;
import com.idea.jgw.common.Common;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.eth.data.TransactionDisplay;
import com.idea.jgw.logic.eth.network.EtherscanAPI;
import com.idea.jgw.service.GetSendStatusService;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MyLog;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.functions.Action1;

public class JgwUtils {


    /**
     * 查询余额
     *
     * @param address
     * @param callback
     */
    public void queryBalance(String address, final TLCallback callback) {


        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 0) {
                    callback.onSuccess(msg.obj);
                } else {
                    callback.onFail(-1, null);
                }
            }
        };


        if (!address.startsWith("0x")) {
            address = "0x" + address;
        }

        final String tempAddress = address;
        final String tempAddres2 = address;

        new Thread(new Runnable() {
            @Override
            public void run() {
                String tokenAddress = Common.Jgw.SMART_CONTRACT;//智能合约地址

                Web3j web3j = Web3jFactory.build(new HttpService(Common.Jgw.URL));
                String methodHex = "0x70a08231"; //查询余额
                String addressHex = tempAddres2.replace("0x", "");
                String dataHex = methodHex + StringUtils.leftPad(addressHex, 64, '0');
                org.web3j.protocol.core.methods.request.Transaction etherTransaction = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(tempAddress, tokenAddress, dataHex);
                try {
                    Future<EthCall> ethCallFuture = web3j.ethCall(etherTransaction, DefaultBlockParameterName.LATEST).sendAsync();
                    EthCall r = ethCallFuture.get();
                    // org.web3j.protocol.core.methods.response.EthCall r = ethCallFuture;
                    String value = r.getResult().substring(2);
                    if (TextUtils.isEmpty(value)) {
                        handler.sendEmptyMessage(-1);
                        return;
                    }

                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = new BigInteger(value, 16).toString(10).toString();
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    handler.sendEmptyMessage(-1);
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void sendCoin(final String fromAddress, final String toAddress, final String amont, final TLCallback callback) {
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    callback.onSuccess(msg.obj);
                } else if (msg.what == -2) {
                    callback.onFail(-2, null);
                } else {
                    callback.onFail(-1, null);
                }
            }
        };

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String tokenAddress = Common.Jgw.SMART_CONTRACT;//智能合约地址
//                Web3j web3j = Web3jFactory.build(new HttpService(Common.Jgw.URL));
//                String methodHex = "0xa9059cbb"; //转账
//                String addressHex = toAddress.replace("0x", "");
//                //method + toAddress+amont
//                String dataHex = methodHex + StringUtils.leftPad(addressHex, 64, '0') + StringUtils.leftPad(new BigInteger(amont, 10).toString(16), 64, '0');
//
//                web3j.ethGetTransactionCount(fromAddress)
//                org.web3j.protocol.core.methods.request.Transaction etherTransaction = org.web3j.protocol.core.methods.request.Transaction
//                        .createEthCallTransaction(toAddress, tokenAddress, dataHex);
//                try {
//                    EthSendTransaction r = web3j.ethSendTransaction(etherTransaction).send();
//                    if (TextUtils.isEmpty(r.getTransactionHash())) {
//                        handler.sendEmptyMessage(-1);
//                        return;
//                    }
//
//                    Message msg = handler.obtainMessage();
//                    msg.what = 0;
//                    msg.obj = r.getResult();
//                    handler.sendMessage(msg);
//                } catch (Exception e) {
//                    handler.sendEmptyMessage(-1);
//                    e.printStackTrace();
//                }
//            }
//        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                String tokenAddress = Common.Jgw.SMART_CONTRACT;//智能合约地址
                Web3j web3j = Web3jFactory.build(new HttpService(Common.Jgw.URL));
                try {
                    String password = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_PWD_KEY, "").toString();
                    String filePath = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.FILE_DIR, "").toString();
                    String fileName = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.FILE_NAME, "").toString();
                    File file = new File(filePath, fileName);
                    Credentials credentials = WalletUtils.loadCredentials(password, file.getPath());
                    LTEToken load = LTEToken.load(tokenAddress, web3j, credentials,
                            web3j.ethGasPrice().send().getGasPrice(), //price
                            new BigInteger("35000") //limiet
                    );
                    String toAdd = toAddress;
                    if (!toAdd.contains("0x"))
                        toAdd = "0x" + toAdd;
                    Future<TransactionReceipt> transactionReceiptFuture = load.transfer(toAdd, new BigInteger(amont)).sendAsync();
                    String hash = transactionReceiptFuture.get().getBlockHash();

                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = hash;
                    handler.sendMessage(msg);


                    GetSendStatusService.startNewService(Common.CoinTypeEnum.JGW, hash);
                } catch (Exception e) {
                    Message m = handler.obtainMessage();
                    String msg = e.getLocalizedMessage();
                    if (msg.contains("not have enough funds")) {
                        m.what = -2;
                        handler.sendMessage(m);
                    } else {
                        handler.sendEmptyMessage(-1);
                    }
                    e.printStackTrace();
                }
            }
        }).start();


    }


    /**
     * 查询交易
     *
     * @param callback
     */
    public void queryTX(final String address, final TLCallback callback) {

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    callback.onSuccess(msg.obj);
                } else {
                    callback.onFail(-1, null);
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EtherscanAPI.getInstance().getTransaction(Common.Jgw.SMART_CONTRACT, address, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            handler.sendEmptyMessage(-1);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final List<TransactionDisplay> list = new ArrayList<>();
                            String body = response.body().string();
                            try {
//                                {"status":"1","message":"OK","result":[{"address":"0x9f6e483ca730907583de27ad30596448a562b362","topics":["0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef","0x00000000000000000000000020fa23b630ec9a4b32e9a3b62dcfb5fc8809bbe1","0x00000000000000000000000085f0e9fc3ef61688865b11b0fc46d9ac432b0f3c"],"data":"0x000000000000000000000000000000000000000000000e1df89a1c64df680000","blockNumber":"0x59853e","timeStamp":"0x5b3452c4","gasPrice":"0x3b9aca00","gasUsed":"0xcfec","logIndex":"0x38","transactionHash":"0xe347f92c285eaadb258f1f721ccb3b30a18771a85d504e5b077f23c276890740","transactionIndex":"0x4a"}]}
                                TransactionDisplay td;
                                JSONObject jsonObject = new JSONObject(body);
                                if (jsonObject.optInt("status") == 1) {
                                    JSONArray result = jsonObject.getJSONArray("result");
                                    JSONObject jsonObjectResult;
                                    for (int i = 0; i < result.length(); i++) {
                                        jsonObjectResult = result.getJSONObject(i);
                                        JSONArray topics = jsonObjectResult.getJSONArray("topics");
                                        String fromAddress = topics.get(1).toString().replace("0x000000000000000000000000", "0x");
                                        String toAddress = topics.get(2).toString().replace("0x000000000000000000000000", "0x");
                                        String data = jsonObjectResult.optString("data").replace("0x", "");
                                        td = new TransactionDisplay(toAddress, fromAddress, new BigInteger(new BigInteger(data, 16).toString(10)));
                                        String hash = jsonObjectResult.optString("transactionHash");
//                                        String gasPrice = new BigInteger(jsonObjectResult.optString("gasPrice").replace("0x",""),16).toString(10);
                                        long gasPrice = Long.parseLong(jsonObjectResult.optString("gasPrice").replace("0x", ""), 16);
                                        int gasUsed = Integer.parseInt(jsonObjectResult.optString("gasUsed").replace("0x", ""), 16);
                                        long blockHeight = Long.parseLong(jsonObjectResult.optString("blockNumber").replace("0x", ""), 16);
                                        long time = Long.parseLong(jsonObjectResult.optString("timeStamp").replace("0x", ""), 16) * 1000;
                                        td.setDate(time);
                                        td.setBlock(blockHeight);
                                        td.setGasprice(gasPrice);
                                        td.setGasUsed(gasUsed);
                                        td.setTxHash(hash);
                                        td.setCoinType(Common.CoinTypeEnum.JGW);
                                        list.add(td);
                                    }

                                    Message msg = handler.obtainMessage();
                                    msg.obj = list;
                                    msg.what = 0;
                                    handler.sendMessage(msg);


                                } else {
                                    handler.sendEmptyMessage(-1);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(-1);
                            }

//


                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-1);
                }


//
//
//
//
//
//                String tokenAddress = Common.Jgw.SMART_CONTRACT;//智能合约地址
//                final List<TransactionDisplay> list = new ArrayList<>();
//                Web3j build = Web3jFactory.build(new HttpService(Common.Jgw.URL));
//                String password = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_PWD_KEY, "").toString();
//                String filePath = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.FILE_DIR, "").toString();
//                String fileName = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.FILE_NAME, "").toString();
//                File file = new File(filePath, fileName);
//                Credentials credentials = null;
//                try {
//
//                    credentials = WalletUtils.loadCredentials(password, file.getPath());
//                    LTEToken load = LTEToken.load(tokenAddress, build, credentials, new BigInteger("18000000000"),
//                            new BigInteger("1000000"));
//
//                    //有交易的时候，获取交易记录
//                    load.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
//                            .forEach(new Action1<LTEToken.TransferEventResponse>() {
//                                @Override
//                                public void call(LTEToken.TransferEventResponse transferEventResponse) {
//                                    TransactionDisplay td = new TransactionDisplay(transferEventResponse._from, transferEventResponse._to, transferEventResponse._value);
//                                    td.setCoinType(Common.CoinTypeEnum.JGW);
//                                    list.add(td);
//                                    Message msg = handler.obtainMessage();
//                                    msg.obj = list;
//                                    handler.sendMessage(msg);
//
//                                    MyLog.e(transferEventResponse._value + "");
//                                    MyLog.e(transferEventResponse._from + "");
//                                    MyLog.e(transferEventResponse._to + "");
//                                }
//                            });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }).start();

    }

}
