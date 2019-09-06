package com.idea.jgw.bean;

/**
 * Created by idea on 2018/6/8.
 */

public class MiningCoinData {
    private String coin;
    private double num;
    private int type; // 1领取记录 2转出记录 3转出失败返还记录
    private long time;

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
