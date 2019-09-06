package com.idea.jgw.api.retrofit;

import com.idea.jgw.bean.BaseResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OceApi {


    String URL = "http://120.132.120.246:8888/wallet/";

    /**
     * 创建钱包
     */
    @FormUrlEncoded
    @POST("create")
    Observable<BaseResponse> cretaeWallet(@Field("xxx") String xx);

    /**
     * 由区块序号获取转账记录
     */
    @FormUrlEncoded
    @GET("getblocktranslist")
    Observable<BaseResponse> getBlockTranslist(@Field("num") int num);

    /**
     * 获取钱包信息
     */
    @GET("getinfo")
    Observable<BaseResponse> getinfo(@Query("address") String address);

    /**
     * 获取最新区块信息
     */
    @GET("getlatestblock")
    Observable<BaseResponse> getlatestblock();

    /**
     * 由TxId获取转账记录
     */
    @GET("gettraninfo")
    Observable<BaseResponse> gettraninfo(@Query("tx_id") String txId);

    /**
     * 由Address获取转账记录
     */
    @GET("gettranslist")
    Observable<BaseResponse> gettranslist(@Query("address") String address);


    /**
     * 钱包转账
     */
    @FormUrlEncoded
    @POST("transign")
    Observable<BaseResponse> transign(@Field("data") String data, @Field("sign") String sign);

//    tran

    /**
     * 钱包转账
     */
    @FormUrlEncoded
    @POST("tran")
    Observable<BaseResponse> tran(@Field("amount") int amount, @Field("from_address") String fromAddres, @Field("goods") String goods, @Field("to_address") String to_address);


}
