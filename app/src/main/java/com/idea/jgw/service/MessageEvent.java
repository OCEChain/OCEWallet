package com.idea.jgw.service;

import com.idea.jgw.common.Common;

public class MessageEvent {

        public static final int STAE_SUCCES = 1;

        private Common.CoinTypeEnum coinType;
        private String tranId;
        private int state;

        public MessageEvent(Common.CoinTypeEnum coinType, int state, String tranId) {
            this.coinType = coinType;
            this.tranId = tranId;
            this.state = state;
        }

        public Common.CoinTypeEnum getCoinType() {
            return coinType;
        }

        public void setCoinType(Common.CoinTypeEnum coinType) {
            this.coinType = coinType;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public void setTranId(String tranId) {
            this.tranId = tranId;
        }

        public String getTranId() {
            return tranId;
        }
    }