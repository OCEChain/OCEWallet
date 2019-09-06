package com.idea.jgw.ui.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.OceApi;
import com.idea.jgw.api.retrofit.OceServiceApi;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.CoinData;
import com.idea.jgw.bean.CoinPrice;
import com.idea.jgw.common.Common;
import com.idea.jgw.dialog.LoadingDialog;
import com.idea.jgw.logic.btc.interfaces.TLCallback;
import com.idea.jgw.logic.btc.model.TLCoin;
import com.idea.jgw.logic.eth.EthWalltUtils;
import com.idea.jgw.logic.ic.EncryptUtils;
import com.idea.jgw.logic.jgw.JgwUtils;
import com.idea.jgw.service.MessageEvent;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.ui.BaseFragment;
import com.idea.jgw.ui.main.MainActivity;
import com.idea.jgw.ui.BaseRecyclerAdapter;
import com.idea.jgw.ui.main.adapter.DigitalCurrencysAdapter;
import com.idea.jgw.ui.wallet.BalanceActivity;
import com.idea.jgw.ui.wallet.EthBalanceActivity;
import com.idea.jgw.ui.wallet.JgwBalanceActivity;
import com.idea.jgw.ui.wallet.OceBalanceActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <p>钱包tab</p>
 * Created by idea on 2018/5/16.
 */

public class WalletFragment extends BaseFragment implements BaseAdapter.OnItemClickListener<CoinData> {
    DigitalCurrencysAdapter digitalCurrencysAdapter;

    @BindView(R.id.tv_of_total_asset)
    TextView tvOfTotalAsset;
    @BindView(R.id.rv_of_detail_mining)
    XRecyclerView rvOfDetailAsset;
    @BindView(R.id.tx_sum_money)
    TextView tvSumMoney;


    CoinPrice ethCoinPrice; //姨太的单价
    BigDecimal ethSumMoney = new BigDecimal("0"); //eth币的总价值
    //    BigDecimal sumMoney = new BigDecimal("0"); //所有币的重甲
    public static final String MONEY_TYPE = "CNY"; //法币符号

    HashMap<String, CoinData> map = new HashMap<>();

    MainActivity mContext;
    String ethAddress;

    @Override
    public void onAttach(Context context) {
        mContext = (MainActivity) context;
        super.onAttach(context);
    }

    static final long DEALY_GET_COIN = 60 * 60 * 5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);


        getEthOfCnyPrice();

        digitalCurrencysAdapter = new DigitalCurrencysAdapter();
        digitalCurrencysAdapter.addDatas(getInitData());
        digitalCurrencysAdapter.setOnItemClickListener(this);

        cretaeOceWallet();

        //根据姨太的地址获取记录
        ethAddress = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_ADDRESS_KEY, "").toString();
//        if (!TextUtils.isEmpty(ethAddress) && EthWalltUtils.isValidAddress(ethAddress)) {

        //后期再优化--->>>需要持久化


//            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
//            {
//                CoinData cd = new CoinData();
//                cd.setAddress(ethAddress);
//                cd.setCoinTypeEnum(Common.CoinTypeEnum.JGW);
//                cd.setCount(df.format(MainActivity.jgwCount.doubleValue()));
//                addData(cd);
//            }
//
//            {
//                CoinData cd = new CoinData();
//                cd.setAddress(ethAddress);
//                cd.setCoinTypeEnum(Common.CoinTypeEnum.ETH);
//                cd.setCount(df.format(MainActivity.ethCount.doubleValue()));
//                addData(cd);
//            }


//            getEthBalance(ethAddress);
//            getJgwBalance(ethAddress);


//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    static String oceAddress = "";

    @Override
    public void onResume() {
        super.onResume();
        //根据姨太的地址获取记录
        String eth_address = SPreferencesHelper.getInstance(App.getInstance()).getData(Common.Eth.PREFERENCES_ADDRESS_KEY, "").toString();
        if (!(TextUtils.isEmpty(ethAddress) && ethAddress.equals(eth_address))) {
            List<CoinData> list = digitalCurrencysAdapter.getmDatas();
            for (CoinData coinData : list) {
                if (coinData.getCoinTypeEnum() == Common.CoinTypeEnum.ETH) {
                    coinData.setAddress(eth_address);
                    break;
                }
            }
        }


        // 这里只是demo，所以没考虑什么性能之类的。
        String address = (String) SPreferencesHelper.getInstance(getActivity()).getData(OCE_ADDRESS, "");
        if (!(TextUtils.isEmpty(oceAddress) && oceAddress.equals(address))) {
            oceAddress = address;
            getOceBalance(address);
        }
    }

    /**
     * 初始化数据
     *
     * @return
     */
    private List<CoinData> getInitData() {
        List<CoinData> list = new ArrayList<>();
        CoinData cd = new CoinData();
        cd.setCoinTypeEnum(Common.CoinTypeEnum.ETH);
        cd.setCount("0.0");
        CoinData cd2 = new CoinData();
        cd2.setCoinTypeEnum(Common.CoinTypeEnum.JGW);
        cd2.setCount("0.0");
        CoinData cd3 = new CoinData();
        cd3.setCoinTypeEnum(Common.CoinTypeEnum.OCE);
        cd3.setCount("0.0");
        list.add(cd);
//        list.add(cd2);
        list.add(cd3);
        map.put(cd.getCoinTypeEnum().getName(), cd);
//        map.put(cd2.getCoinTypeEnum().getName(), cd2);
        map.put(cd3.getCoinTypeEnum().getName(), cd3);
        return list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_wallet, null);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvOfDetailAsset.setLayoutManager(layoutManager);
        rvOfDetailAsset.setAdapter(digitalCurrencysAdapter);
//        View header = inflater.inflate(R.layout.header_of_wallet, null, false);
//        tvOfTotalAsset = header.findViewById(R.id.tv_of_total_asset);
//        tvSumMoney = header.findViewById(R.id.tx_sum_money);
//        rvOfDetailAsset.addHeaderView(header);
        rvOfDetailAsset.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (!TextUtils.isEmpty(ethAddress) && EthWalltUtils.isValidAddress(ethAddress)) {
                    getEthBalance(false);
                } else {
                    MToast.showToast(R.string.address_wrong);
                }
            }

            @Override
            public void onLoadMore() {

            }
        });
        getEthBalance(true);
        if(EncryptUtils.isJGWBrand()) {
            tvOfTotalAsset.setText(R.string.asset_protected);
            tvOfTotalAsset.setTextColor(getResources().getColor(R.color.ic_protect));
            tvOfTotalAsset.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_encryption, 0, 0, 0);
            return view;
        }
        tvOfTotalAsset.setText(R.string.asset_unprotected);
        tvOfTotalAsset.setTextColor(getResources().getColor(R.color.white));
        tvOfTotalAsset.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_of_unencrypted, 0, 0, 0);
        return view;
    }

    @Override
    public void onItemClick(int position, CoinData data) {
        String path = "";
        switch (data.getCoinTypeEnum()) {
            case BTC:
                path = RouterPath.BALANCE_BTC_ACTIITY;
                break;
            case ETH:
                path = RouterPath.BALANCE_ETH_ACTIITY;
                break;
            case JGW:
                path = RouterPath.BALANCE_JGW_ACTIITY;
                break;
            case OCE:
                path = RouterPath.BALANCE_OCE_ACTIITY;
                break;
            default:
                break;
        }
        if (TextUtils.isEmpty(path)) {
            MToast.showToast(R.string.type_not_find);
        } else {
            ARouter.getInstance().build(path)
                    .withSerializable(BalanceActivity.EXTRA_AMOUNT, data.getCount())
                    .withString(BalanceActivity.EXTRA_USABLE, data.getUsable())
                    .withString(BalanceActivity.EXTRA_ADDRESS, data.getAddress())
                    .navigation();
        }
    }


    private void getJgwBalance(final String address) {
        String tempAddress;
        if (!address.startsWith("0x"))
            tempAddress = "0x" + address;
        else
            tempAddress = address;

        JgwUtils ju = new JgwUtils();
        ju.queryBalance(address, new TLCallback() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.e("getJgwBalance___" + (obj == null));
                if (null != obj) {
                    String str = obj.toString();
                    BigInteger bi = new BigInteger(str);
                    BigDecimal bd = new BigDecimal(10).pow(18);
                    DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
                    BigDecimal amount = new BigDecimal(bi.toString(10)).divide(bd);
                    String balance = df.format(amount.doubleValue());
                    CoinData cd = new CoinData();
                    cd.setAddress(address);
                    cd.setCoinTypeEnum(Common.CoinTypeEnum.JGW);
                    cd.setCount(balance);
                    addData(cd);

                    MainActivity.jgwCount = amount;
                }
            }

            @Override
            public void onFail(Integer status, String error) {
                MyLog.e(status + "____" + error);
            }

            @Override
            public void onSetHex(String hex) {

            }

            @Override
            public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

            }
        });
    }

    private void getEthBalance(boolean showDialog) {
        if (showDialog)
            LoadingDialog.showDialogForLoading(getActivity());
        String tempAddress;
        if (!ethAddress.startsWith("0x"))
            tempAddress = "0x" + ethAddress;
        else
            tempAddress = ethAddress;

        EthWalltUtils.getCurAvailable(mContext, tempAddress, new TLCallback() {
            @Override
            public void onSuccess(Object obj) {
                LoadingDialog.cancelDialogForLoading();
                rvOfDetailAsset.refreshComplete();
                if (null == obj) return;
                String str = obj.toString();

                CoinData cd = new CoinData();
                cd.setAddress(ethAddress);
                cd.setCoinTypeEnum(Common.CoinTypeEnum.ETH);

                if (!TextUtils.isEmpty(str)) {
                    BigInteger bi = new BigInteger(str);
                    BigDecimal bd = new BigDecimal(10).pow(18);
                    DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
                    BigDecimal amount = new BigDecimal(bi.toString(10)).divide(bd);
                    String balance = String.valueOf(amount.doubleValue());
                    cd.setCount(balance);
                    MainActivity.ethCount = amount;
                } else {
                    MainActivity.ethCount = new BigDecimal(0);
                }
                addData(cd);

            }

            @Override
            public void onFail(Integer status, String error) {
                LoadingDialog.cancelDialogForLoading();
                rvOfDetailAsset.refreshComplete();
            }

            @Override
            public void onSetHex(String hex) {

            }

            @Override
            public void onAmountMoveFromAccount(TLCoin amountMovedFromAccount) {

            }
        });

    }

    private void addData(CoinData cd) {

        //计算大概值多少人民币
        if (ethCoinPrice != null) {
            if (cd.getCoinTypeEnum().equals(Common.CoinTypeEnum.ETH)) {
                cd.setPrice(ethCoinPrice);
                BigDecimal sumEth = getEthSumPrice(cd, ethCoinPrice);
                tvSumMoney.setText(sumEth.doubleValue() + MONEY_TYPE);
            }
        }

        //该方法只适用于api24的版本，兼容低版本用下面的代码
//        CoinData coinData = map.get(cd.getCoinTypeEnum().getName());
        map.put(cd.getCoinTypeEnum().getName(), cd);
        List<CoinData> list = new ArrayList<>();
        for (CoinData data : map.values()) {
            list.add(data);
        }
        digitalCurrencysAdapter.replaceDatas(list);
    }


    private void getEthOfCnyPrice() {
        CoinPrice cp = new CoinPrice();
        cp.getCoinPrice("ETH", MONEY_TYPE, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what > 0) {
                    ethCoinPrice = (CoinPrice) msg.obj;
                    CoinData cd = map.get(Common.CoinTypeEnum.ETH.getName());
                    cd.setPrice(ethCoinPrice);
                    addData(cd);

                    BigDecimal bd = getEthSumPrice(cd, ethCoinPrice);
//                    sumMoney = sumMoney.add(bd);
                    tvSumMoney.setText(bd.doubleValue() + MONEY_TYPE);

                }
            }
        });

    }

    private BigDecimal getEthSumPrice(CoinData cd, CoinPrice cp) {
        String amount = cd.getCount();
        BigDecimal bd = new BigDecimal(amount);
        return bd.multiply(new BigDecimal(cp.getLast())).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


    public static final String OCE_PRIVATE_KEY = "OCE_PRIVATE_KEY";
    public static final String OCE_ADDRESS = "OCE_ADDRESS";
    public static final String OCE_PUBLIC_KEY = "OCE_PUBLIC_KEY";

    /**
     * 创建钱包
     */
    private void cretaeOceWallet() {
        String address = (String) SPreferencesHelper.getInstance(getActivity()).getData(OCE_ADDRESS, "");
        if (TextUtils.isEmpty(address)) {
            disposables.add(OceServiceApi.getInstance(OceApi.URL).getApiService().cretaeWallet("xxx")
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new RxSubscriber<BaseResponse>(getActivity()) {
                        @Override
                        protected void _onNext(BaseResponse response) {
                            com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) response.getInfo();
                            String privateKey = obj.getString("private key");
                            String address = obj.getString("address");
                            String publicKey = obj.getString("public key");

                            getOceBalance(address);

                            SPreferencesHelper.getInstance(getActivity()).saveData(OCE_PRIVATE_KEY, privateKey);
                            SPreferencesHelper.getInstance(getActivity()).saveData(OCE_ADDRESS, address);
                            SPreferencesHelper.getInstance(getActivity()).saveData(OCE_PUBLIC_KEY, publicKey);
                        }

                        @Override
                        protected void _onError(String message) {

                        }
                    }));
        } else {
            getOceBalance(address);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendCoinState(MessageEvent messageEvent) {
        if (messageEvent.getCoinType() == Common.CoinTypeEnum.OCE && messageEvent.getState() == MessageEvent.STAE_SUCCES) {
            String address = (String) SPreferencesHelper.getInstance(getActivity()).getData(OCE_ADDRESS, "");
            getOceBalance(address);
        }
    }


    private void getOceBalance(final String address) {
        disposables.add(OceServiceApi.getInstance(OceApi.URL).getApiService().getinfo(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(getActivity()) {
                    @Override
                    protected void _onNext(BaseResponse response) {
                        if (response.getCode() == 1) {
                            com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) response.getInfo();
                            Integer num = obj.getInteger("Number");
                            Integer usable = obj.getInteger("Usable");
                            addOce(address, String.valueOf(num), String.valueOf(usable));
                        }
                    }

                    @Override
                    protected void _onError(String message) {
                        MyLog.e("xxx", message);
                    }
                }));
    }

    private void addOce(String address, String amount, String usable) {
        CoinData cd = new CoinData();
        cd.setAddress(address);
        cd.setCount(amount);
        cd.setCoinTypeEnum(Common.CoinTypeEnum.OCE);
        cd.setUsable(usable);
        addData(cd);
    }
}
