package com.idea.jgw.bean;

import java.util.List;

/**
 * Created by idea on 2018/6/8.
 */

public class AllMiningData {

    /**
     * calculation : 666
     * list : [{"balance":0,"coin_info":{"id":1,"name":"比特币","char":"btc","face":"","unit":"c","unit_face":""},"profit":5986.27,"receive_profit":5986.27},{"balance":0,"coin_info":{"id":2,"name":"以太币","char":"eth","face":"","unit":"gwei","unit_face":""},"profit":5986.27,"receive_profit":5986.27}]
     */

    private int calculation;
    private List<CoinMining> list;

    public int getCalculation() {
        return calculation;
    }

    public void setCalculation(int calculation) {
        this.calculation = calculation;
    }

    public List<CoinMining> getList() {
        return list;
    }

    public void setList(List<CoinMining> list) {
        this.list = list;
    }
}
