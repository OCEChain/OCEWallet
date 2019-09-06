package com.idea.jgw.bean;

/**
 * Created by idea on 2018/6/8.
 */



public class CoinMining {
    /**
     * balance : 0
     * coin_info : {"id":1,"name":"比特币","char":"btc","face":"","unit":"c","unit_face":""}
     * profit : 5986.27
     * receive_profit : 5986.27
     */

    private double balance;
    private CoinData coin_info;
    private double profit;
    private double receive_profit;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public CoinData getCoin_info() {
        return coin_info;
    }

    public void setCoin_info(CoinData coin_info) {
        this.coin_info = coin_info;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getReceive_profit() {
        return receive_profit;
    }

    public void setReceive_profit(double receive_profit) {
        this.receive_profit = receive_profit;
    }

}
