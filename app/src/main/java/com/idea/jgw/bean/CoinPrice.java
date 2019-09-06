package com.idea.jgw.bean;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.data.DatabaseUtils;
import com.idea.jgw.data.MyOpenHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MyLog;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 币种价格
 */
public class CoinPrice {

    private String coinName;
    private String moneyType;
    private String coin;
    private String last;
    private String averagesDay;
    private long timestamp;


    static final long delay_time = 60 * 60 * 2; //2个小时的间隔
    long last_req_time = 0;

    /**
     * 获取价格
     * @param coinType 币种 BTC
     * @param moneyType 法币简写 USD CNY
     * @param callback 回调
     */
    public void getCoinPrice(String coinType, String moneyType, Handler callback) {
        coinType = coinType.toUpperCase();
        moneyType = moneyType.toUpperCase();


        if(last_req_time == 0){
            requestCoinPrice(coinType,moneyType,callback);
            return;
        }

        String sp_save_time = coinType;
        SharedPreferences sp = App.getInstance().getSharedPreferences(sp_save_time, Application.MODE_PRIVATE);
        long saveTime = sp.getLong(sp_save_time, 0);
        long nowTime = System.currentTimeMillis();

        if(nowTime - saveTime > delay_time){
            requestCoinPrice(coinType,moneyType,callback);

        }else{
            CoinPrice cp = getCoinPrice(coinType, moneyType);
            if (null == cp) {
                requestCoinPrice(coinType,moneyType,callback);
            } else {
                Message msg = callback.obtainMessage();
                msg.obj = cp;
                msg.what = 1;
                callback.sendMessage(msg);
            }
        }

        last_req_time = nowTime;
        sp.edit().putLong(sp_save_time,nowTime).apply();
    }


    public void requestCoinPrice(final String coinType, final String money, final Handler callback) {
        ServiceApi.getInstance().getApiService()
                .getCoinPrice(coinType)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxSubscriber<BaseResponse>(App.getInstance(), App.getInstance().getResources().getString(R.string.loading), false) {

                    @Override
                    protected void _onNext(BaseResponse baseResponse) {
                        MyLog.e(baseResponse.toString());
                        if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                            String str = baseResponse.getData().toString();
                            try {
                                final JSONObject head = new JSONObject(str);
                                CoinPrice cp;
                                List<CoinPrice> list = new ArrayList<>();
                                CoinPrice result = new CoinPrice();
                                String moneyType;
                                String last;
                                long timestamp;
                                JSONObject averages;
                                JSONObject exchangeRate;
                                String currencyCode;
                                String avagesDay;
                                for (final Iterator<String> i = head.keys(); i.hasNext(); ) {
                                    currencyCode = i.next();
                                    exchangeRate = head.getJSONObject(currencyCode);
                                    averages = exchangeRate.getJSONObject("averages");
                                    avagesDay = averages.optString("day");
                                    timestamp = exchangeRate.optLong("timestamp");
                                    last = exchangeRate.optString("last");
                                    moneyType = currencyCode.substring(coinType.length());
                                    cp = new CoinPrice();
                                    cp.setAveragesDay(avagesDay);
                                    cp.setLast(last);
                                    cp.setTimestamp(timestamp);
                                    cp.setCoin(currencyCode);
                                    cp.setCoinName(coinType);
                                    cp.setMoneyType(moneyType);
                                    list.add(cp);

                                    if (moneyType.equals(money)){
                                        result = cp;
                                    }
                                }

                                Message msg = callback.obtainMessage();
                                msg.obj = result;
                                msg.what = 1;
                                callback.sendMessage(msg);

                                clearData(coinType);
                                insertAll(coinType, list);
                            } catch (JSONException e) {
//                                e.printStackTrace();
                                callback.sendEmptyMessage(-1);
                            }
                        } else {
                            callback.sendEmptyMessage(-1);
                        }
                    }

                    @Override
                    protected void _onError(String message) {
                        callback.sendEmptyMessage(-1);
                    }
                });
    }



    public void insertAll(String tableName, List<CoinPrice> list) {
        try{
            getHelper().clear(tableName);
        }catch (Exception e){

        }
        getHelper().saveAll(tableName, list);
    }

    public void clearData(String tableName) {
        getHelper().clear(tableName);
    }

    public CoinPrice getCoinPrice(String coinType, String moneyType) {
        List<CoinPrice> list = getHelper().queryAll(coinType, CoinPrice.class);
        String coin = null;
        for (CoinPrice cp : list) {
            coin = cp.getCoin();
            if (coin.endsWith(moneyType) && coin.startsWith(coinType)) {
                return cp;
            }
        }
        return null;
    }

    private MyOpenHelper getHelper() {
        MyOpenHelper helper = DatabaseUtils.getHelper();
        if (null == helper) {
            DatabaseUtils.initHelper(App.getInstance(), "coinPrice.db");
            helper = DatabaseUtils.getHelper();
        }
        return helper;
    }


    public void getSesion() {
    }


    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setAveragesDay(String averagesDay) {
        this.averagesDay = averagesDay;
    }

    public String getAveragesDay() {
        return averagesDay;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public String getMoneyType() {
        return moneyType;
    }
}
