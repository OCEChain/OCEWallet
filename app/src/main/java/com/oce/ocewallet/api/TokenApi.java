package com.oce.ocewallet.api;

import com.oce.ocewallet.base.Constants;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TokenApi {
    public static TokenApi instance;

    private TokenApiService service;

    public TokenApi(OkHttpClient okHttpClient) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Constants.PATH_DATA)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        service = retrofit.create(TokenApiService.class);

    }

    public static TokenApi getInstance(OkHttpClient okHttpClient) {
        if (instance == null)
            instance = new TokenApi(okHttpClient);
        return instance;
    }

}
