package com.idea.jgw.api;

import android.util.Pair;
import android.view.SurfaceHolder;

import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.listener.UploadListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by idea on 2018/6/14.
 */

public class OkhttpApi {

    //正式服
    public static final String BASE_HOST = "http://120.132.120.251:10004/";
    //测试服
//    public static final String BASE_HOST = "http://121.201.80.40:10004/";

    public static final String UPDATE_PHOTO = BASE_HOST + "edit_face";
    public static final String UPDATE_CERTIFICATION = BASE_HOST + "certification";

    public static final void updatePhoto(String token, File file, UploadListener listener) {

        Map<String, String> param = new HashMap<>();
        param.put("token", token);
        Pair<String, File> pair = new Pair<String, File>("file", file);

        OkHttpProxy
                .upload()
                .url(UPDATE_PHOTO)
                .file(pair)
                .setParams(param)
                .setWriteTimeOut(20)
                .start(listener);
    }

    public static final void certification(String token, String id_name, String id_num, File id_cart, File id_cart_back, UploadListener listener) {

        Map<String, String> param = new HashMap<>();
        param.put("token", token);
        param.put("id_name", id_name);
        param.put("id_num", id_num);
        Pair<String, File> pair = new Pair<String, File>("id_cart", id_cart);
        Pair<String, File> pair2 = new Pair<String, File>("id_cart_back", id_cart_back);

        OkHttpProxy
                .upload()
                .url(UPDATE_CERTIFICATION)
                .file(pair)
                .file(pair2)
                .setParams(param)
                .setWriteTimeOut(20)
                .start(listener);
    }
}
