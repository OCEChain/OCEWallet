package com.idea.jgw.bean;

public class OceBaseResponse   {

    public static final int RESULT_OK = 1;
    private int code;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    private Object info;
    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }
}
