package com.idea.jgw.logic.btc.model;

import org.json.JSONArray;

import java.util.List;

public class CreatedTransactionObject {
    public String txHex;
    public String txHash;
    public Integer txSize;
    public JSONArray inputHexScripts;

    public List<String> realToAddresses;
    public JSONArray txInputsAccountHDIdxes;
}