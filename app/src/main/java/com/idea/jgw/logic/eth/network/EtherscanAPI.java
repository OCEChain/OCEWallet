package com.idea.jgw.logic.eth.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.idea.jgw.common.Common;
import com.idea.jgw.logic.eth.APIKey;
import com.idea.jgw.logic.eth.interfaces.LastIconLoaded;
import com.idea.jgw.logic.eth.interfaces.StorableWallet;
import com.idea.jgw.logic.eth.utils.Key;
import com.idea.jgw.logic.eth.utils.RequestCache;
import com.idea.jgw.logic.eth.utils.TokenIconCache;
import com.idea.jgw.utils.common.MyLog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EtherscanAPI {

    public static final String HOST = "http://api.etherscan.io/"; //正式链
//    public static final String HOST = "https://api-ropsten.etherscan.io/"; //测试链

    private String token;

    private static EtherscanAPI instance;

    public static EtherscanAPI getInstance() {
        if (instance == null)
            instance = new EtherscanAPI();
        return instance;
    }

    public void getPriceChart(long starttime, int period, boolean usd, Callback b) throws IOException {
        get("http://poloniex.com/public?command=returnChartData&currencyPair=" + (usd ? "USDT_ETH" : "BTC_ETH") + "&start=" + starttime + "&end=9999999999&period=" + period, b);
    }


    /**
     * Retrieve all internal transactions from address like contract calls, for normal transactions @see rehanced.com.simpleetherwallet.network.EtherscanAPI#getNormalTransactions() )
     *
     * @param address Ether address
     * @param b       Network callback to @see rehanced.com.simpleetherwallet.fragments.FragmentTransactions#update() or @see rehanced.com.simpleetherwallet.fragments.FragmentTransactionsAll#update()
     * @param force   Whether to force (true) a network call or use cache (false). Only true if user uses swiperefreshlayout
     * @throws IOException Network exceptions
     */
    public void getInternalTransactions(String address, Callback b) {
//        if (!force && RequestCache.getInstance().contains(RequestCache.TYPE_TXS_INTERNAL, address)) {
//            b.onResponse(null, new Response.Builder().code(200).message("").request(new Request.Builder()
//                    .url(HOST + "api?module=account&action=txlistinternal&address=" + address + "&startblock=0&endblock=99999999&sort=desc&apikey=" + token)
//                    .build()).protocol(Protocol.HTTP_1_0).body(ResponseBody.create(MediaType.parse("JSON"), RequestCache.getInstance().get(RequestCache.TYPE_TXS_INTERNAL, address))).build());
//            return;
//        }
        get(HOST + "api?module=account&action=txlistinternal&address=" + address + "&startblock=0&endblock=99999999&sort=desc&apikey=" + token, b);
    }


    /**
     * Retrieve all normal ether transactions from address (excluding contract calls etc, @see rehanced.com.simpleetherwallet.network.EtherscanAPI#getInternalTransactions() )
     *
     * @param address Ether address
     * @param b       Network callback to @see rehanced.com.simpleetherwallet.fragments.FragmentTransactions#update() or @see rehanced.com.simpleetherwallet.fragments.FragmentTransactionsAll#update()
     * @param force   Whether to force (true) a network call or use cache (false). Only true if user uses swiperefreshlayout
     * @throws IOException Network exceptions
     */
    public void getNormalTransactions(String address, int page, int offset, String sort, Callback b) {
//        if (!force && RequestCache.getInstance().contains(RequestCache.TYPE_TXS_NORMAL, address)) {
////            MyLog.e("getNormalTransactions-url--->"+HOST + "api?module=account&action=txlist&address=" + address + "&startblock=0&endblock=99999999&sort=desc&apikey=" + token);
//            b.onResponse(null, new Response.Builder().code(200).message("").request(new Request.Builder()
//                    .cacheControl(CacheControl.FORCE_NETWORK)
//                    .url(HOST + "api?module=account&action=txlist&address=" + address + "&startblock=0&endblock=99999999&sort=desc&apikey=" + token)
//                    .build()).protocol(Protocol.HTTP_1_0).body(ResponseBody.create(MediaType.parse("JSON"), RequestCache.getInstance().get(RequestCache.TYPE_TXS_NORMAL, address))).build());
//            return;
//        }
        get(HOST + "api?module=account&action=txlist&startblock=0&endblock=99999999&address=" + address + "&page=" + page + "&offset=" + offset + "&sort=" + sort + "&apikey=" + token, b);
    }


    public void getEtherPrice(Callback b) throws IOException {
        get(HOST + "api?module=stats&action=ethprice&apikey=" + token, b);
    }

    //获取汽油价格
    public void getGasPrice(Callback b) throws IOException {
        get(HOST + "api?module=proxy&action=eth_gasPrice&apikey=" + token, b);
    }


    /**
     * Get token balances via ethplorer.io
     *
     * @param address Ether address
     * @param b       Network callback to @see rehanced.com.simpleetherwallet.fragments.FragmentDetailOverview#update()
     * @param force   Whether to force (true) a network call or use cache (false). Only true if user uses swiperefreshlayout
     * @throws IOException Network exceptions
     */
    public void getTokenBalances(String address, Callback b, boolean force) throws IOException {
        if (!force && RequestCache.getInstance().contains(RequestCache.TYPE_TOKEN, address)) {
            b.onResponse(null, new Response.Builder().code(200).message("").request(new Request.Builder()
                    .url("https://api.ethplorer.io/getAddressInfo/" + address + "?apiKey=freekey")
                    .build()).protocol(Protocol.HTTP_1_0).body(ResponseBody.create(MediaType.parse("JSON"), RequestCache.getInstance().get(RequestCache.TYPE_TOKEN, address))).build());
            return;
        }
        get("http://api.ethplorer.io/getAddressInfo/" + address + "?apiKey=freekey", b);
    }


    /**
     * Download and save token icon in permanent image cache (TokenIconCache)
     *
     * @param c         Application context, used to load TokenIconCache if reinstanced
     * @param tokenName Name of token
     * @param lastToken Boolean defining whether this is the last icon to download or not. If so callback is called to refresh recyclerview (notifyDataSetChanged)
     * @param callback  Callback to @see rehanced.com.simpleetherwallet.fragments.FragmentDetailOverview#onLastIconDownloaded()
     * @throws IOException Network exceptions
     */
    public void loadTokenIcon(final Context c, String tokenName, final boolean lastToken, final LastIconLoaded callback) throws IOException {
        if (tokenName.indexOf(" ") > 0)
            tokenName = tokenName.substring(0, tokenName.indexOf(" "));
        if (TokenIconCache.getInstance(c).contains(tokenName)) return;

        if(tokenName.equalsIgnoreCase("OMGToken"))
            tokenName = "omise";
        else if(tokenName.equalsIgnoreCase("0x"))
            tokenName = "0xtoken_28";

        final String tokenNamef = tokenName;
        get("http://etherscan.io//token/images/" + tokenNamef + ".PNG", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (c == null) return;
                ResponseBody in = response.body();
                InputStream inputStream = in.byteStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                final Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                TokenIconCache.getInstance(c).put(c, tokenNamef, new BitmapDrawable(c.getResources(), bitmap).getBitmap());
                // if(lastToken) // TODO: resolve race condition
                callback.onLastIconDownloaded();
            }
        });
    }


    public void getGasLimitEstimate(String to, Callback b) throws IOException {
        get(HOST + "api?module=proxy&action=eth_estimateGas&to=" + to + "&value=0xff22&gasPrice=0x051da038cc&gas=0xffffff&apikey=" + token, b);
    }


    public void getBalance(String address, Callback b) throws IOException {
        get(HOST + "api?module=account&action=balance&address=" + address + "&apikey=" + token, b);
//        get(Common.Eth.URL,b);
    }


    public void getNonceForAddress(String address, Callback b) throws IOException {
        get(HOST + "api?module=proxy&action=eth_getTransactionCount&address=" + address + "&tag=latest&apikey=" + token, b);
    }


    public void getPriceConversionRates(String currencyConversion, Callback b) throws IOException {
        get("https://api.fixer.io/latest?base=USD&symbols=" + currencyConversion, b);
    }


    public void getBalances(ArrayList<StorableWallet> addresses, Callback b) throws IOException {
        String url = HOST + "api?module=account&action=balancemulti&address=";
        for (StorableWallet address : addresses)
            url += address.getPubKey() + ",";
        url = url.substring(0, url.length() - 1) + "&tag=latest&apikey=" + token; // remove last , AND add token
        get(url, b);
    }


    public void forwardTransaction(String raw, Callback b) throws IOException {
        get(HOST + "api?module=proxy&action=eth_sendRawTransaction&hex=" + raw + "&apikey=" + token, b);
    }


    public void get(String url, Callback b) {

        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(b);
    }


    /**
     * 获取交易记录（代币的）
     * @param tokenAddres 智能合约地址
     * @param ethAddress 姨太的地址
     * @param b
     * @throws IOException
     */
    public void getTransaction(String tokenAddres,String ethAddress,Callback b) throws IOException{
        String url ="https://api.etherscan.io/api?module=logs&action=getLogs&fromBlock=earliest&toBlock=latest&address="+ tokenAddres;
        url = url +"&topic2="+getTopic2(ethAddress);
        url = url + "&apikey=" + token;
        get(url,b);
    }

    private String getTopic2(String ethAddress){
        ethAddress = ethAddress.replace("0x", "");
        if (ethAddress.length() == 64) {
//            ethAddress = "0x000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
//                    + ethAddress;
            ethAddress = "0x"+ethAddress;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("0x");
            for (int i = 0; i < 64 - ethAddress.length(); i++) {
                sb.append("0");
            }
            sb.append(ethAddress);
            ethAddress = sb.toString();
        }
        return ethAddress;
    }

    private EtherscanAPI() {
        token = new Key(APIKey.API_KEY).toString();
    }

}
