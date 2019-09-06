package com.idea.jgw.bean;

/**
 * Created by idea on 2018/6/4.
 */

public class BaseResponse {
    public static final int RESULT_OK = 200;
    public static final int INVALID_SESSION = 1;
    private int code;
    private Object data;
    private Object info;
    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
