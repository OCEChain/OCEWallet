package com.idea.jgw.api.retrofit;


import com.idea.jgw.api.Api;
import com.idea.jgw.logic.eth.APIKey;
import com.idea.jgw.logic.eth.utils.Key;

/**
 * Created by Ganlin.Wu on 2016/9/21.
 */
public class ServiceApi extends Api<SongApiService> {

    private static ServiceApi sInstance;
    String token;

    private ServiceApi() {
        super(SongApiService.BASE_URL);
        token = new Key(APIKey.API_KEY).toString();
    }

    public static ServiceApi getInstance() {
        if (sInstance == null) {
            synchronized (ServiceApi.class) {
                if (sInstance == null) {
                    sInstance = new ServiceApi();
                }
            }
        }
        return sInstance;
    }

    public String getToken() {
        return token;
    }
}
