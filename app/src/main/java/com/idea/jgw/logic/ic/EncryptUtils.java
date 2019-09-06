package com.idea.jgw.logic.ic;

import android.os.Build;

import com.idea.jgw.App;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.ShareKey;
import com.xdja.SafeKey.JNIAPI;
import com.xdja.SafeKey.XDJA_DEVINFO;
import com.xdja.SafeKey.XDJA_SM2_PRIKEY;
import com.xdja.SafeKey.XDJA_SM2_PUBKEY;

import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;

import io.socket.utf8.UTF8;

public class EncryptUtils {
    public static final String BRAND = "JIAGUWEN";
    public static final String PRODUCT = "t3a";
    public static final String MANUFACTURER = "JIAGUWEN";

    public static boolean isJGWBrand() {
        return (boolean) SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_IC_ENCRYPTION, false);
//        return BRAND.equals(android.os.Build.BRAND) && PRODUCT.equals(Build.PRODUCT) && MANUFACTURER.equals(android.os.Build.MANUFACTURER);
//        return false;
//        return true;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }


    public static boolean writePIN(String pin) {
        boolean result = false;
        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
        int ret = 0;
        int[] devNum = {0};
        long[] handle = {0};
        //枚举卡
        ret = jniapi.EnumDev(JNIAPI.CT_ALL, devNum);
        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
            //未找到卡，出错
            System.out.println("ret = " + ret + "未找到卡");
            result = false;
        } else {
            //打开卡
            ret = jniapi.OpenDev(0, handle);
            if (JNIAPI.XKR_OK != ret) {
                //打开卡出错
                System.out.println("ret = " + ret + "开卡失败");
                result = false;
            } else {
                //修改PIN
//        ret = jniapi.ChangePIN(handle[0], JNIAPI.ROLE_A, "111111".getBytes(), 6, pin.getBytes(), 6);
                ret = jniapi.ChangePIN(handle[0], JNIAPI.ROLE_A, pin.getBytes(), 6, pin.getBytes(), 6);
                if (JNIAPI.XKR_OK != ret) {
                    //出错，返回ret错误码
                    System.out.println("ret = " + ret + "写入PIN失败");
                    result = false;
                } else {
                    System.out.println("ret = " + ret + "写入PIN成功");
                    //认证PIN
                    ret = jniapi.VerifyPIN(handle[0], JNIAPI.ROLE_A, pin.getBytes(), 6);
                    if (JNIAPI.XKR_OK != ret) {
                        //出错，返回ret错误码
                        System.out.println("ret = " + ret + "验证PIN失败");
                        result = false;
                    } else {
                        result = true;
                    }
                }
            }
        }
        jniapi.CloseDev(handle[0]);//关闭卡
        return result;
    }

    public static boolean writePrivateKey(ECKeyPair ecKeyPair) {
        byte[] priviteKey = Numeric.hexStringToByteArray(Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey()));
        byte[] publicKey = Numeric.hexStringToByteArray(Numeric.toHexStringNoPrefix(ecKeyPair.getPublicKey()));
        byte[] publicX = subBytes(publicKey, 0, 32);
        byte[] publicY = subBytes(publicKey, 32, 32);
        System.out.println("priviteKey = " + Numeric.toHexString(priviteKey));
        System.out.println("publicKey = " + Numeric.toHexString(publicKey));
        System.out.println("publicX = " + Numeric.toHexString(publicX));
        System.out.println("publicY = " + Numeric.toHexString(publicY));
        return writePrivateKey(priviteKey, publicX, publicY);
    }

    public static boolean writePrivateKey(byte[] priviteKey, byte[] publicX, byte[] publicY) {
        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
        int ret = 0;
        int[] devNum = {0};
        long[] handle = {0};
        //枚举卡
        ret = jniapi.EnumDev(JNIAPI.CT_ALL, devNum);
        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
            //未找到卡，出错
            System.out.println("ret = " + ret + "未找到卡");
            return false;
        }
        //打开卡
        ret = jniapi.OpenDev(0, handle);
        if (JNIAPI.XKR_OK != ret) {
            //打开卡出错
            System.out.println("ret = " + ret + "开卡失败");
            return false;
        }
        //认证PIN
        ret = jniapi.VerifyPIN(handle[0], JNIAPI.ROLE_A, "111111".getBytes(), 6);
        if (JNIAPI.XKR_OK != ret) {
            //出错，返回ret错误码
            System.out.println("ret = " + ret + "验证PIN失败");
            return false;
        }
        byte[] pubid = new byte[2];
        byte[] priid = new byte[2];
        XDJA_SM2_PUBKEY ECCpub = new XDJA_SM2_PUBKEY();
        XDJA_SM2_PRIKEY ECCPri = new XDJA_SM2_PRIKEY();
        pubid[1] = 0x2a;
        priid[1] = 0x2b;
        ECCPri.d = priviteKey;
        ECCpub.x = publicX;
        ECCpub.y = publicY;
        ret = jniapi.WriteSm2PubKey(handle[0], pubid, ECCpub);
        if (JNIAPI.XKR_OK != ret) {
            //出错，返回ret错误码
            System.out.println("ret = " + ret + "private write 失败");
            return false;
        }
        ret = jniapi.WriteSm2PriKey(handle[0], priid, ECCPri);
        if (JNIAPI.XKR_OK != ret) {
            //出错，返回ret错误码
            System.out.println("ret = " + ret + "public write 失败");
            return false;
        }
        jniapi.CloseDev(handle[0]);//关闭卡
        return true;
    }

    public static byte[] sign(byte[] dataIn) {
        return sign(dataIn, "111111", true, null, null, null);
    }

    public static byte[] sign(byte[] dataIn, String pwd, boolean pin, byte[] priviteKey, byte[] publicX, byte[] publicY) {
        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
        int ret = 0;
        int[] devNum = {0};
        long[] handle = {0};
        byte[] signdata = new byte[64];
        int[] outlen = {0};
        System.out.println("data size = " + dataIn.length);
        System.out.println("data = " + Numeric.toHexString(dataIn));
        System.out.println("data size = " + dataIn.length);
        //枚举卡
        ret = jniapi.EnumDev(JNIAPI.CT_ALL, devNum);
        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
            //未找到卡，出错
            System.out.println("ret = " + ret + "未找到卡");
            return null;
        }
        //打开卡
        ret = jniapi.OpenDev(0, handle);
        if (JNIAPI.XKR_OK != ret) {
            //打开卡出错
            System.out.println("ret = " + ret + "开卡失败");
            return null;
        }
        if (pin) {
            //认证PIN
            ret = jniapi.VerifyPIN(handle[0], JNIAPI.ROLE_A, pwd.getBytes(), 6);
            if (JNIAPI.XKR_OK != ret) {
                //出错，返回ret错误码
                System.out.println("ret = " + ret + "验证PIN失败");
                return null;
            }
        }
        byte[] pubid = new byte[2];
        byte[] priid = new byte[2];
        pubid[1] = 0x2a;
        priid[1] = 0x2b;
        ret = jniapi.ECDSASign(handle[0], pubid, priid, dataIn, signdata, outlen);
        if (JNIAPI.XKR_OK != ret) {
            //出错，返回ret错误码
            System.out.println("ret = " + ret + "Sign data 失败");
            return null;
        }
        ret = jniapi.ECDSASignVerify(handle[0], pubid, null, dataIn, signdata);
        if (JNIAPI.XKR_OK != ret) {
            //出错，返回ret错误码
            System.out.println("ret = " + ret + "Verify Sign data 失败");
            return null;
        }
        System.out.println(" Sign data =" + Numeric.toHexString(signdata));
        jniapi.CloseDev(handle[0]);//关闭卡
        return signdata;
    }

    static final String VERSION_DATE = "date=2018/08/14";
    public static boolean rightVersion() {
        return getDeviceInfo().contains(VERSION_DATE);
    }


    public static String getDeviceInfo() {
        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
        int ret = 0;
        int[] devNum = {0};
        long[] handle = {0};
        //枚举卡
        ret = jniapi.EnumDev(JNIAPI.CT_ALL, devNum);
        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
            //未找到卡，出错
            System.out.println("ret = " + ret + "未找到卡");
            return "";
        }
        //打开卡
        ret = jniapi.OpenDev(0, handle);
        if (JNIAPI.XKR_OK != ret) {
            //打开卡出错
            System.out.println("ret = " + ret + "开卡失败");
            return "";
        }
        //认证PIN
//        ret = jniapi.VerifyPIN(handle[0], JNIAPI.ROLE_A, "111111".getBytes(), 6);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            System.out.println("ret = " + ret + "验证PIN失败");
//            return "";
//        }
        XDJA_DEVINFO devinfo = new XDJA_DEVINFO();
        ret = jniapi.GetDevInfo(handle[0], devinfo);
        if (JNIAPI.XKR_OK != ret) {
            //出错，返回ret错误码
            System.out.println("ret = " + ret + "private write 失败");
            return "";
        }
        System.out.println("ret = " + Numeric.toHexString(devinfo.cosver));
        System.out.println("ret = " + new String(devinfo.cosver));
        try {
            System.out.println("ret = " + new String(devinfo.cosver, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("ret = " + new String(devinfo.cosver, "GB2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("ret = " + new String(devinfo.cosver, "GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("ret = " + new String(devinfo.cosver, "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jniapi.CloseDev(handle[0]);//关闭卡
        return new String(devinfo.cosver);
    }
}
