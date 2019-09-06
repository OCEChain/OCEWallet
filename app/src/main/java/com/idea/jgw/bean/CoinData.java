package com.idea.jgw.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.idea.jgw.common.Common;

/**
 * Created by idea on 2018/6/8.
 */

public class CoinData {
    /**
     * id : 1
     * name : 比特币
     * char : btc
     * face :
     * unit : c
     * unit_face :
     */

    private int id;
    private String name;
    @SerializedName("char")
    @JSONField(name = "char")
    private String charX;
    private String face;
    private String unit;
    private String unit_face;
    private String address;
    private String count;
    private Common.CoinTypeEnum coinTypeEnum;
    private CoinPrice price; //单价
    private String frozen;//冻结的金额
    private String usable;//可用


    public String getFrozen() {
        return frozen;
    }

    public void setFrozen(String frozen) {
        this.frozen = frozen;
    }

    public String getUsable() {
        return usable;
    }

    public void setUsable(String usable) {
        this.usable = usable;
    }

    public void setCoinTypeEnum(Common.CoinTypeEnum coinTypeEnum) {
        this.coinTypeEnum = coinTypeEnum;
    }

    public Common.CoinTypeEnum getCoinTypeEnum() {
        return coinTypeEnum;
    }


    public CoinPrice getPrice() {
        return price;
    }

    public void setPrice(CoinPrice price) {
        this.price = price;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharX() {
        return charX;
    }

    public void setCharX(String charX) {
        this.charX = charX;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit_face() {
        return unit_face;
    }

    public void setUnit_face(String unit_face) {
        this.unit_face = unit_face;
    }
}
