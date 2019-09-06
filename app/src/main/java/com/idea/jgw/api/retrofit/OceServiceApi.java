package com.idea.jgw.api.retrofit;


import com.idea.jgw.App;
import com.idea.jgw.api.Api;

import java.net.URL;
import java.util.WeakHashMap;

/**
 * Created by Ganlin.Wu on 2016/9/21.
 */
public class OceServiceApi extends Api<OceApi> {

    private static OceServiceApi sInstance;

    private OceServiceApi() {
        super(OceApi.URL);
    }

    private OceServiceApi(String url) {
        super(url);
    }

    static WeakHashMap<String, OceServiceApi> map = new WeakHashMap<>();

    public static OceServiceApi getInstance(String baseUrl) {

        if (map.containsKey(baseUrl)) {
            return map.get(baseUrl);
        } else {
            OceServiceApi api = new OceServiceApi(baseUrl);
            map.put(baseUrl, api);
            return api;
        }
    }


}
