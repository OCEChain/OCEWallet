package com.idea.jgw.bean;

import java.util.HashMap;

/**
 * Created by idea on 2018/6/4.
 */

public class LoginRequest {
    private String account;
    private String passwd;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public HashMap<String, String> getQueryMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("passwd", passwd);
        return map;
    }
}
