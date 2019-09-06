package com.idea.jgw.bean;

import java.util.List;

public class BaseEthResponse {
    /**
     * status : 0
     * message : No transactions found
     * result : ""
     */

    private int status;
    private String message;
    private String result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
