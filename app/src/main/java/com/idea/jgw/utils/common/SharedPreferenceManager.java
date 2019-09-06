package com.idea.jgw.utils.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.idea.jgw.App;

/**
 * Created by wgl on 2016/9/21.
 */

public class SharedPreferenceManager {
    private static SharedPreferenceManager instance;
    private SharedPreferences sp;
    private String session; //session
    private String gesturePwd; //session
    private boolean createWallet; //session
    private boolean takeOnGesturePwd; //session
    private boolean login; //session
    private String passphrase ;//助记词
    private String paymentPwd;//支付密码
    private String phone;
    private String invite_url;
    private String invite_code;
    private String face;
    private String nickname;
    private int invite_num;
    private int hashrate;

    public static SharedPreferenceManager getInstance() {
        if (instance == null) {
            instance = new SharedPreferenceManager();
        }
        return instance;
    }

    public SharedPreferenceManager() {
        sp = App.getInstance().getSharedPreferences("uav_setting", Context.MODE_PRIVATE);
        session = sp.getString(Key.KEY_OF_SESSION, "");
        gesturePwd = sp.getString(Key.KEY_OF_GESTURE_PWD, "");
        createWallet = sp.getBoolean(Key.KEY_OF_CREATE_WALLET, true);
        takeOnGesturePwd = sp.getBoolean(Key.KEY_OF_TAKE_ON_GESTURE_PWD, false);
        login = sp.getBoolean(Key.KEY_OF_LOGIN, false);
        passphrase = sp.getString(Key.KEY_PASSPHRASE,"");
        paymentPwd = sp.getString(Key.KEY_PAYMENT_PWD,"");
        phone = sp.getString(Key.KEY_OF_PHONE, "");
        invite_url = sp.getString(Key.KEY_OF_INVITE_URL, "");
        invite_code = sp.getString(Key.KEY_OF_INVITE_CODE, "");
        invite_num = sp.getInt(Key.KEY_OF_INVITE_NUM, 0);
        hashrate = sp.getInt(Key.KEY_OF_INVITE_NUM, 0);
        face = sp.getString(Key.KEY_OF_FACE, "");
        nickname = sp.getString(Key.KEY_OF_NICK_NAME, "");
    }


    public String getPaymentPwd() {
        return paymentPwd;
    }

    public void setPaymentPwd(String paymentPwd) {
        this.paymentPwd = paymentPwd;
        sp.edit().putString(Key.KEY_PAYMENT_PWD, paymentPwd).apply();
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
        sp.edit().putString(Key.KEY_OF_SESSION, session).apply();
    }

    public boolean isCreateWallet() {
        return createWallet;
    }

    public void setCreateWallet(boolean createWallet) {
        this.createWallet = createWallet;
        sp.edit().putBoolean(Key.KEY_OF_CREATE_WALLET, createWallet).apply();
    }




    public boolean isTakeOnGesturePwd() {
        return takeOnGesturePwd;
    }

    public void setTakeOnGesturePwd(boolean takeOnGesturePwd) {
        this.takeOnGesturePwd = takeOnGesturePwd;
        sp.edit().putBoolean(Key.KEY_OF_TAKE_ON_GESTURE_PWD, takeOnGesturePwd).apply();
    }

    public String getGesturePwd() {
        return gesturePwd;
    }

    public void setGesturePwd(String gesturePwd) {
        this.gesturePwd = gesturePwd;
        sp.edit().putString(Key.KEY_OF_GESTURE_PWD, gesturePwd).apply();
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
//        sp.edit().putBoolean(Key.KEY_OF_LOGIN, login).apply();
    }

    public void setPassphrase(String passphrase){
        this.passphrase = passphrase;
        sp.edit().putString(Key.KEY_PASSPHRASE, passphrase).apply();
    }
    public String getPassphrase(){
        return passphrase;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        sp.edit().putString(Key.KEY_OF_PHONE, phone).apply();
    }

    public String getInvite_url() {
        return invite_url;
    }

    public void setInvite_url(String invite_url) {
        this.invite_url = invite_url;
        sp.edit().putString(Key.KEY_OF_INVITE_URL, invite_url).apply();
    }

    public String getInvite_code() {
        return invite_code;
    }

    public void setInvite_code(String invite_code) {
        this.invite_code = invite_code;
        sp.edit().putString(Key.KEY_OF_INVITE_CODE, invite_code).apply();
    }

    public int getInvite_num() {
        return invite_num;
    }

    public void setInvite_num(int invite_num) {
        this.invite_num = invite_num;
        sp.edit().putInt(Key.KEY_OF_INVITE_NUM, invite_num).apply();
    }

    public int getHashrate() {
        return hashrate;
    }

    public void setHashrate(int hashrate) {
        this.hashrate = hashrate;
        sp.edit().putInt(Key.KEY_OF_HASHRATE, hashrate).apply();
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
        sp.edit().putString(Key.KEY_OF_FACE, face).apply();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        sp.edit().putString(Key.KEY_OF_NICK_NAME, nickname).apply();
    }

    public static class Key {
        static final String KEY_OF_SESSION = "session"; //新登录使用
        static final String KEY_OF_CREATE_WALLET = "create_wallet"; //是否需要新建钱包
        static final String KEY_OF_TAKE_ON_GESTURE_PWD = "take_on_gesture_pwd"; //是否开启手势验证密码
        static final String KEY_OF_GESTURE_PWD = "gesture_pwd"; //手式密码
        static final String KEY_OF_LOGIN = "login"; //是否已登录
        static final String KEY_PASSPHRASE = "key_passphrase"; //助记词
        static final String KEY_PAYMENT_PWD ="key_payment_key";//支付密码
        public static final String KEY_OF_PHONE = "phone"; //phone
        public static final String KEY_OF_INVITE_URL = "invite_url"; //invite_url
        public static final String KEY_OF_INVITE_CODE = "invite_code"; //invite_code
        public static final String KEY_OF_INVITE_NUM = "invite_num"; //invite_num
        public static final String KEY_OF_HASHRATE = "hashrate"; //算力
        public static final String KEY_OF_FACE = "face"; //算力
        public static final String KEY_OF_NICK_NAME = "nick_name"; //算力
    }

}
