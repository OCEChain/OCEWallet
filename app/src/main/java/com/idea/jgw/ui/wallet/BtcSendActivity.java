package com.idea.jgw.ui.wallet;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.RouterPath;

/**
 * Created by vam on 2018\6\4 0004.
 */
@Route(path = RouterPath.SEND_BTC_ACTIVITY)
public class BtcSendActivity extends SendActivity {

    String selfAddress;


    @Override
    protected String getSelfAddress() {
        return selfAddress;
    }

    @Override
    public void onPasswordPassed() {

    }
}
