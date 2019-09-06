package com.idea.jgw.ui;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.logic.ic.EncryptUtils;
import com.idea.jgw.test.TransactionEncoder;
import com.idea.jgw.utils.FingerManagerUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.xdja.SafeKey.JNIAPI;
import com.xdja.SafeKey.XDJA_SM2_PRIKEY;
import com.xdja.SafeKey.XDJA_SM2_PUBKEY;

import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.ec.CustomNamedCurves;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TEmpActivity extends AppCompatActivity implements View.OnClickListener {
    public static final boolean IC_TEST = true;

    private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    static final ECDomainParameters CURVE = new ECDomainParameters(
            CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 60;
    int nonce = 80;
    @BindView(R.id.wallet_sign)
    Button walletSign;
    @BindView(R.id.write_key)
    Button writeKey;
    @BindView(R.id.private_key_sign)
    Button privateKeySign;
    @BindView(R.id.write_pin)
    Button writePin;
    @BindView(R.id.write_pin2)
    Button writePin2;
    @BindView(R.id.finger_test)
    Button fingerTest;
    @BindView(R.id.ic_sign_test)
    Button icSignTest;
    @BindView(R.id.ic_version_test)
    Button icVersionTest;

    //    private String privetKeyString = "de3f76d259b1e30cb2862acc0607b08ea7fc3b4a57d0a52b751f3cd9ea48e48b";
    private String privetKeyString = "9a44bc98fad902cea00c4ac623497ce585c3cc032c984870355e34b6ab438d22"; //测试网地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        ButterKnife.bind(this);
        findViewById(R.id.wallet_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletSign(true);
                MToast.showToast("签名测试结果：" + (walletSign(false).equals(walletSign(true)) ? "成功" : "失败"));
            }
        });
        findViewById(R.id.write_key).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导入私钥
                writePriviteKey();
//                String ss = "28ec6703adccf5643d4d9f48d97eed4bb01160c54fd88507e188b9d53705626dcb4273eb043268922a69fcce2f3ff1746fce077f4bd871a1b998f64dcff98147";
//                byte[] pubX = Numeric.hexStringToByteArray(ss.substring(0, 64));
//                byte[] pubY = Numeric.hexStringToByteArray(ss.substring(64, 128));
//                writePrivateKey("", false, Numeric.hexStringToByteArray("7493a49ebbf9961a97a5b0de76d4f8bcf4dd45520cce2d030c701d73b6bc21e3"), pubX, pubY);
            }
        });
        findViewById(R.id.private_key_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for(int i=0;i<10;i++) {
//                    walletSign(false);
//                }
                walletSign(false);
            }
        });
        findViewById(R.id.write_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                writePIN();
            }
        });
        findViewById(R.id.finger_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyguardManager mKeyguardManager = (KeyguardManager) App.getInstance().getSystemService(Context.KEYGUARD_SERVICE);
                Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
                if (intent != null) {
                    startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
                }
                fingerPrinter();
            }
        });
        findViewById(R.id.write_pin2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EncryptUtils.getDeviceInfo();
                MToast.showToast("芯片固件版本" + (EncryptUtils.rightVersion() ? "是" : "不是") + "：2018/08/14");
//                System.out.println("writePIN = " + EncryptUtils.writePIN(String.valueOf(ppp)));
//                System.out.println("ppp = " + ppp);
//                ppp ++;

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for(int i=0;i<20;i++) {
//                            if(EncryptUtils.writePIN(String.valueOf(ppp))) {
//                                break;
//                            }
//
//                            System.out.println("ppp = " + ppp);
//                            ppp ++;
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
        });

        if(IC_TEST) {
            icSignTest.setVisibility(View.VISIBLE);
            icVersionTest.setVisibility(View.VISIBLE);

            walletSign.setVisibility(View.GONE);
            writeKey.setVisibility(View.GONE);
            writePin.setVisibility(View.GONE);
            writePin2.setVisibility(View.GONE);
            fingerTest.setVisibility(View.GONE);
            privateKeySign.setVisibility(View.GONE);
        } else {
            icSignTest.setVisibility(View.GONE);
            icVersionTest.setVisibility(View.GONE);

            walletSign.setVisibility(View.VISIBLE);
            writeKey.setVisibility(View.VISIBLE);
            writePin.setVisibility(View.VISIBLE);
            writePin2.setVisibility(View.VISIBLE);
            fingerTest.setVisibility(View.VISIBLE);
            privateKeySign.setVisibility(View.VISIBLE);
        }
    }

    int ppp = 123456;

    void fingerPrinter() {
        FingerManagerUtils.callFingerPrint(new FingerManagerUtils.OnCallBackListenr() {
            AlertDialog dialog;

            @Override
            public void onSupportFailed() {
                MToast.showToast("当前设备不支持指纹");
            }

            @Override
            public void onInsecurity() {
                MToast.showToast("当前设备未处于安全保护中");
            }

            @Override
            public void onEnrollFailed() {
                MToast.showToast("请到设置中设置指纹");
            }

            @Override
            public void onAuthenticationStart() {
                AlertDialog.Builder builder = new AlertDialog.Builder(TEmpActivity.this);
//                View view = LayoutInflater.from(TEmpActivity.this).inflate(R.layout.layout_fingerprint,null);
//                initView(view);
//                builder.setView(view);
                builder.setCancelable(false);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        handler.removeMessages(0);
                        FingerManagerUtils.cancel();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                MToast.showToast(errString.toString());
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
//                    handler.removeMessages(0);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                MToast.showToast("解锁失败");
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                MToast.showToast(helpString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                MToast.showToast("解锁成功");
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
//                    handler.removeMessages(0);
                }

            }
        });
    }

    String walletSign(boolean wallet) {

        //模拟创建一笔交易
        BigDecimal decimal = new BigDecimal(1).multiply(new BigDecimal(10).pow(16));
        BigInteger value = decimal.toBigInteger();

        //第一次参数为nonce,每次交易要递增,当前是5,下一笔应为6,
        // 可在https://ropsten.etherscan.io/address/0xb1b537b8b2344e2ec68b29577d0d3b5afa85549e点开最新一笔支出交易查看当前nonce值
        //
        RawTransaction etherTransaction = RawTransaction.createEtherTransaction(new BigInteger(String.valueOf(nonce)), new BigInteger("10000000000"), new BigInteger("100000"), "0xB8D30Cb670f2c55bfCfFBDFd1b69Dd21a2a3Cdf5", value);


        //导入私钥
        ECKeyPair ke = ECKeyPair
                .create(Numeric.hexStringToByteArray(privetKeyString));


        //----签名开始
        //签名前处理
        byte[] encode = TransactionEncoder.encode(etherTransaction);
        byte[] messageHash = Hash.sha3(encode);
        System.out.println("messageHash  = " + Numeric.toHexString(messageHash));
        System.out.println("messageHash length = " + messageHash.length);
        System.out.println("nonce  = " + nonce);

        ECDSASignature sig = null;
        if (wallet) {
            //由芯片通过私钥来做具体签名
            sig = ke.sign(messageHash);
            System.out.println("sig.r = " + sig.r);
            System.out.println("sig.s = " + sig.s);
            System.out.println("sig.r = " + Numeric.toHexString(Numeric.toBytesPadded(sig.r, 32)));
            System.out.println("sig.s = " + Numeric.toHexString(Numeric.toBytesPadded(sig.s, 32)));
        } else {
            byte[] ingnData = EncryptUtils.sign(messageHash);
//            byte[] ingnData = sign(Numeric.hexStringToByteArray("86bf6f04673c36b6b15b45fe61c0cb42cb14b6a56ca9a2c2ebaeff57b9591a34"));

            if (ingnData == null) {
                System.out.println("Error: ingnData = " + ingnData);
                return "";
            }
            MToast.showToast("芯片签名通过！");
            //将IC签名后的16进制串分成2个32位,分别给r和s
            byte[] bytes1 = subBytes(ingnData, 0, 32);
            byte[] bytes2 = subBytes(ingnData, 32, 32);
            BigInteger rr = Numeric.toBigInt(bytes1);
            BigInteger ss = Numeric.toBigInt(bytes2);
//            System.out.println("sig.r = " + rr);
//            System.out.println("sig.s = " + ss);
//            System.out.println("sig.r = " + Numeric.toHexString(bytes1));
//            System.out.println("sig.s = " + Numeric.toHexString(bytes2));

            sig = new ECDSASignature(rr, ss).toCanonicalised();
        }

        //签名后处理,需要用到公钥匙
        BigInteger publicKey = ke.getPublicKey();
        System.out.println(Numeric.toHexString(publicKey.toByteArray()));
        int recId = -1;
        for (int i = 0; i < 4; i++) {
            try {
                Class clazz = Class.forName("org.web3j.crypto.Sign");
                Method method = clazz.getDeclaredMethod("recoverFromSignature", int.class, ECDSASignature.class, byte[].class);
                method.setAccessible(true);
                BigInteger k = (BigInteger) method.invoke(null, i, sig, messageHash);
                if (k != null && k.equals(publicKey)) {
                    recId = i;
                    break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
//            BigInteger k = Sign.recoverFromSignature(i, sig, messageHash);
//            if (k != null && k.equals(publicKey)) {
//                recId = i;
//                break;
//            }
        }
//        for (int i = 0; i < 4; i++) {
//            BigInteger k = Sign.recoverFromSignature(i, sig, messageHash);
//            if (k != null && k.equals(publicKey)) {
//                recId = i;
//                break;
//            }
//        }
        if (recId == -1) {
//            throw new RuntimeException(
//                    "Could not construct a recoverable key. This should never happen.");
            Log.e("TEmpActivity", "Could not construct a recoverable key. This should never happen.");
        }

        int headerByte = recId + 27;

        // 1 header + 32 bytes for R + 32 bytes for S
        byte v = (byte) headerByte;
        byte[] r = Numeric.toBytesPadded(sig.r, 32);
        byte[] s = Numeric.toBytesPadded(sig.s, 32);

        Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);
        byte[] signedMessage = TransactionEncoder.encode(etherTransaction, signatureData);
        //----签名结束


        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

        // ECPrivateKeyParameters privKey = new ECPublicKeyParameters(ke.getPublicKey(), CURVE);
        //signer.init(false, privKey);
        // BigInteger[] components = signer.verifySignature(sig,Numeric.toBigInt(signatureData.getR()),Numeric.toBigInt(signatureData.getS()));


        //这是自带的签名,用来比对
        byte[] bytes = TransactionEncoder.signMessage(etherTransaction, ke);


        System.out.println(Numeric.toHexString(publicKey.toByteArray()));
        System.out.println(Numeric.toHexString(ke.getPrivateKey().toByteArray()));
        System.out.println(Numeric.toHexString(bytes));
        System.out.println("signedMessage = " + Numeric.toHexString(signedMessage));
        nonce++;
        return Numeric.toHexString(signedMessage);
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }


//    void writePIN() {
//
//        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
//        int ret = 0;
//        int[] devNum = {0};
//        long[] handle = {0};
//        //枚举卡
//        ret = jniapi.EnumDev(JNIAPI.CT_ALL, devNum);
//        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
//            //未找到卡，出错
//            MyLog.d("未找到卡");
//            return;
//        }
//        //打开卡
//        ret = jniapi.OpenDev(0, handle);
//        if (JNIAPI.XKR_OK != ret) {
//            //打开卡出错
//            MyLog.d("开卡失败");
//            return;
//        }
//        //修改PIN
//        ret = jniapi.ChangePIN(handle[0], JNIAPI.ROLE_A, "111111".getBytes(), 6, "222222".getBytes(), 6);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            MyLog.d("写入PIN失败");
//            return;
//        }
//        //认证PIN
//        ret = jniapi.VerifyPIN(handle[0], JNIAPI.ROLE_A, "222222".getBytes(), 6);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            MyLog.d("验证PIN失败");
//            return;
//        }
//        jniapi.CloseDev(handle[0]);//关闭卡
//    }

//    void writePrivateKey(String pwd, boolean pin, byte[] priviteKey, byte[] publicX, byte[] publicY) {
//        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
//        int ret = 0;
//        int[] devNum = {0};
//        long[] handle = {0};
////        byte[] dataIn = new byte[32];//待签名数据必须经过HASH运算，长度是32，测试程序用32个0x01模拟
//        byte[] signdata = new byte[64];
//        int[] outlen = {0};
//        //枚举卡
//        ret = jniapi.EnumDev(JNIAPI.CT_ALL, devNum);
//        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
//            //未找到卡，出错
//            MyLog.d("未找到卡");
//            return;
//        }
//        //打开卡
//        ret = jniapi.OpenDev(0, handle);
//        if (JNIAPI.XKR_OK != ret) {
//            //打开卡出错
//            MyLog.d("开卡失败");
//            return;
//        }
//        if (pin) {
//            //认证PIN
//            ret = jniapi.VerifyPIN(handle[0], JNIAPI.ROLE_A, pwd.getBytes(), 6);
//            if (JNIAPI.XKR_OK != ret) {
//                //出错，返回ret错误码
//                MyLog.d("验证PIN失败");
//                return;
//            }
//        }
//        byte[] pubid = new byte[2];
//        byte[] priid = new byte[2];
//        XDJA_SM2_PUBKEY ECCpub = new XDJA_SM2_PUBKEY();
//        XDJA_SM2_PRIKEY ECCPri = new XDJA_SM2_PRIKEY();
////        ret = jniapi.GenECDSAKeyPair(handle[0],pubid,priid,ECCpub,ECCPri);
////        if (JNIAPI.XKR_OK != ret)
////        {
////            //出错，返回ret错误码
////            return;
////        }
//        pubid[1] = 0x2a;
//        priid[1] = 0x2b;
//        ECCPri.d = priviteKey;
//        ECCpub.x = publicX;
//        ECCpub.y = publicY;
//        ret = jniapi.WriteSm2PubKey(handle[0], pubid, ECCpub);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            MyLog.d("private write 失败");
//            return;
//        }
//        ret = jniapi.WriteSm2PriKey(handle[0], priid, ECCPri);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            MyLog.d("public write 失败");
//            return;
//        }
////        ret = jniapi.ECDSASign(handle[0], pubid, priid, dataIn, signdata, outlen);
////        if (JNIAPI.XKR_OK != ret) {
////            //出错，返回ret错误码
////            MyLog.d("Sign data 失败");
////            return;
////        }
////        ret = jniapi.ECDSASignVerify(handle[0], pubid, null, dataIn, signdata);
////        if (JNIAPI.XKR_OK != ret) {
////            //出错，返回ret错误码
////            MyLog.d("Verify Sign data 失败");
////            return;
////        }
//        jniapi.CloseDev(handle[0]);//关闭卡
//    }

//    public static byte[] sign(byte[] dataIn) {
//        return sign(dataIn, "", false, null, null, null);
//    }
//
//    public static byte[] sign(byte[] dataIn, String pwd, boolean pin, byte[] priviteKey, byte[] publicX, byte[] publicY) {
//        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
//        int ret = 0;
//        int[] devNum = {0};
//        long[] handle = {0};
////        byte[] dataIn = new byte[32];//待签名数据必须经过HASH运算，长度是32，测试程序用32个0x01模拟
//        byte[] signdata = new byte[64];
//        int[] outlen = {0};
//        MyLog.d("data size = " + dataIn.length);
//        System.out.println("data = " + Numeric.toHexString(dataIn));
//        System.out.println("data size = " + dataIn.length);
//        //枚举卡
//        ret = jniapi.EnumDev(JNIAPI.CT_ALL, devNum);
//        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
//            //未找到卡，出错
//            MyLog.d("未找到卡");
//            return null;
//        }
//        //打开卡
//        ret = jniapi.OpenDev(0, handle);
//        if (JNIAPI.XKR_OK != ret) {
//            //打开卡出错
//            MyLog.d("开卡失败");
//            return null;
//        }
//        if (pin) {
//            //认证PIN
//            ret = jniapi.VerifyPIN(handle[0], JNIAPI.ROLE_A, pwd.getBytes(), 6);
//            if (JNIAPI.XKR_OK != ret) {
//                //出错，返回ret错误码
//                MyLog.d("验证PIN失败");
//                return null;
//            }
//        }
//        byte[] pubid = new byte[2];
//        byte[] priid = new byte[2];
////        XDJA_SM2_PUBKEY ECCpub = new XDJA_SM2_PUBKEY();
////        XDJA_SM2_PRIKEY ECCPri = new XDJA_SM2_PRIKEY();
//        pubid[1] = 0x2a;
//        priid[1] = 0x2b;
////        ECCPri.d = priviteKey;
////        ECCpub.x = publicX;
////        ECCpub.y = publicY;
////        ret = jniapi.WriteSm2PubKey(handle[0],pubid,ECCpub);
////        if (JNIAPI.XKR_OK != ret)
////        {
////            //出错，返回ret错误码
////                            MyLog.d("private write 失败");
////            return null;
////        }
////        ret = jniapi.WriteSm2PriKey(handle[0],priid,ECCPri);
////        if (JNIAPI.XKR_OK != ret)
////        {
////            //出错，返回ret错误码
////                                        MyLog.d("public write 失败");
////            return null;
////        }
//        ret = jniapi.ECDSASign(handle[0], pubid, priid, dataIn, signdata, outlen);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            MyLog.d("Sign data 失败");
//            return null;
//        }
//        ret = jniapi.ECDSASignVerify(handle[0], pubid, null, dataIn, signdata);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            MyLog.d("Verify Sign data 失败");
//            return null;
//        }
//        MyLog.d(" Sign data =" + Numeric.toHexString(signdata));
//        System.out.println(" Sign data =" + Numeric.toHexString(signdata));
//        jniapi.CloseDev(handle[0]);//关闭卡
//        return signdata;
//    }
//
//    void writeKey() {
//        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
//        int ret = 0;
//        int[] devNum = {0};
//        long[] handle = {0};
//        byte[] dataIn = new byte[32];//待签名数据必须经过HASH运算，长度是32，测试程序用32个0x01模拟
//        byte[] signdata = new byte[64];
//        int[] outlen = {0};
//        for (int i = 0; i < 32; i++) {
//            dataIn[i] = 0x01;
//        }
//        //枚举卡
//        ret = jniapi.EnumDev(jniapi.CT_ALL, devNum);
//        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
//            //未找到卡，出错
//            return;
//        }
//        //打开卡
//        ret = jniapi.OpenDev(0, handle);
//        if (JNIAPI.XKR_OK != ret) {
//            //打开卡出错
//            return;
//        }
//        //修改PIN
//        ret = jniapi.ChangePIN(handle[0], jniapi.ROLE_A, "111111".getBytes(), 6, "222222".getBytes(), 6);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            return;
//        }
//        //认证PIN
//        ret = jniapi.VerifyPIN(handle[0], jniapi.ROLE_A, "222222".getBytes(), 6);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            return;
//        }
//        byte[] pubid = new byte[2];
//        byte[] priid = new byte[2];
//        XDJA_SM2_PUBKEY ECCpub = new XDJA_SM2_PUBKEY();
//        XDJA_SM2_PRIKEY ECCPri = new XDJA_SM2_PRIKEY();
//        ret = jniapi.GenECDSAKeyPair(handle[0], pubid, priid, ECCpub, ECCPri);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            return;
//        }
//        pubid[1] = 0x2a;
//        priid[1] = 0x2b;
//        ret = jniapi.WriteSm2PubKey(handle[0], pubid, ECCpub);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            return;
//        }
//        ret = jniapi.WriteSm2PriKey(handle[0], priid, ECCPri);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            return;
//        }
//        ret = jniapi.ECDSASign(handle[0], pubid, priid, dataIn, signdata, outlen);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            return;
//        }
//        ret = jniapi.ECDSASignVerify(handle[0], pubid, null, dataIn, signdata);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            return;
//        }
//        jniapi.CloseDev(handle[0]);//关闭卡
//    }
//
//    public void test3() {
//        log("开始操作卡");
//        JNIAPI jniapi = new JNIAPI();//加载JAR包接口类
//        int ret = 0;
//        int[] devNum = {0};
//        long[] handle = {0};
//        byte[] dataIn = new byte[32];//待签名数据必须经过HASH运算，长度是32，测试程序用32个0x01模拟
//        byte[] signdata = new byte[64];
//        int[] outlen = {0};
//        for (int i = 0; i < 32; i++) {
//            dataIn[i] = 0x01;
//        }
//        log("dataIn = " + Numeric.toHexString(dataIn));
//        //枚举卡
//        ret = jniapi.EnumDev(jniapi.CT_ALL, devNum);
//        log("devNum = " + devNum.length);
//        if (ret != JNIAPI.XKR_OK || 0 == devNum[0]) {
//            //未找到卡，出错
//            log("未找到卡，出错");
//            return;
//        }
//        //打开卡
//        ret = jniapi.OpenDev(0, handle);
//        if (JNIAPI.XKR_OK != ret) {
//            //打开卡出错
//            log("打开卡出错");
//            return;
//        }
//        //验证PIN
//        ret = jniapi.VerifyPIN(handle[0], jniapi.ROLE_A, "111111".getBytes(), 6);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            log("出错，返回ret错误码, code = " + ret);
//            return;
//        }
//        //产生
////         pubid = new byte[2];
////         priid = new byte[2];
//        XDJA_SM2_PUBKEY ECCpub = new XDJA_SM2_PUBKEY();
//        XDJA_SM2_PRIKEY ECCPri = new XDJA_SM2_PRIKEY();
////        for (int i=0;i<32;i++)
////        {
////            ECCPri.d[i] = (byte) count;
////        }
//
//        ECCPri.d = Numeric.hexStringToByteArray("393718e110a67020cdbbf8b7f44fe053e541b1ac3a12d8e378be26469be64518");
//        ECCpub.x = Numeric.hexStringToByteArray("67ac7e2de855bf87a01fc84b2c8dc714f09f2365e74c42d243349ad2ffeb49ad0ba8cbcfe24f3b219c199e024ef4a80c0e6c3cc5a6f840809704831b1c4c5420".substring(0, 64));
//        ECCpub.y = Numeric.hexStringToByteArray("67ac7e2de855bf87a01fc84b2c8dc714f09f2365e74c42d243349ad2ffeb49ad0ba8cbcfe24f3b219c199e024ef4a80c0e6c3cc5a6f840809704831b1c4c5420".substring(64, 128));
//        ret = jniapi.GenECDSAKeyPair(handle[0], pubid, priid, ECCpub, ECCPri);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            log("GenECDSAKeyPair 出错，返回ret错误码, code = " + ret);
//            return;
//        }
//        log("产生 pubid = " + Numeric.toHexString(pubid));
//        log("产生 priid = " + Numeric.toHexString(priid));
//        log("产生 ECCpub ECCpub.x = " + Numeric.toHexString(ECCpub.x) + "  ECCpub.y = " + Numeric.toHexString(ECCpub.y));
//        log("产生 ECCPri = " + Numeric.toHexString(ECCPri.d));
//        pubid[1] = 0x2a;
//        priid[1] = 0x2b;
//
//        ret = jniapi.WriteSm2PubKey(handle[0], pubid, ECCpub);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            log("WriteSm2PubKey 出错，返回ret错误码, code = " + ret);
//            return;
//        }
//        ret = jniapi.WriteSm2PriKey(handle[0], priid, ECCPri);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            log("WriteSm2PriKey 出错，返回ret错误码, code = " + ret);
//            return;
//        }
//        log("产生2 pubid = " + Numeric.toHexString(pubid));
//        log("产生2 priid = " + Numeric.toHexString(priid));
//        log("产生2 ECCpub ECCpub.x = " + Numeric.toHexString(ECCpub.x) + "  ECCpub.y = " + Numeric.toHexString(ECCpub.y));
//        log("产生2 ECCPri = " + Numeric.toHexString(ECCPri.d));
//
//        ret = jniapi.ECDSASign(handle[0], pubid, priid, dataIn, signdata, outlen);
//        log("signData = " + Numeric.toHexString(signdata));
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            log("ECDSASign 出错，返回ret错误码, code = " + ret);
////            String signData = "";
////            for(byte b:signdata) {
////                signData += b;
////            }
//            return;
//        }
//        log("outlen = " + outlen[0]);
//        ret = jniapi.ECDSASign(handle[0], pubid, priid, dataIn, signdata, outlen);
//        log("signData = " + Numeric.toHexString(signdata));
//        ret = jniapi.ECDSASignVerify(handle[0], pubid, null, dataIn, signdata);
//        if (JNIAPI.XKR_OK != ret) {
//            //出错，返回ret错误码
//            log("ECDSASignVerify 出错，返回ret错误码, code = " + ret);
////            String signData = "";
////            for(byte b:signdata) {
////                signData += b;
////            }
//            return;
//        }
//        log("signData = " + Numeric.toHexString(signdata));
//        jniapi.CloseDev(handle[0]);//关闭卡
//        log("成功！关闭卡");
////        String signData = "";
////        for(byte b:signdata) {
////            signData += b;
////        }
////        tv_msg.setText(Numeric.toHexString(signdata));
//    }

    byte[] pubid = new byte[2];
    byte[] priid = new byte[2];

    void log(String msg) {
        Log.d(getClass().getName(), msg);
    }

    @OnClick({R.id.wallet_sign, R.id.write_key, R.id.private_key_sign, R.id.write_pin, R.id.write_pin2, R.id.finger_test, R.id.ic_sign_test, R.id.ic_version_test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wallet_sign:
                walletSign(true);
                MToast.showToast("签名测试结果：" + (walletSign(false).equals(walletSign(true)) ? "成功" : "失败"));
                break;
            case R.id.write_key:
                //导入私钥
                writePriviteKey();
                break;
            case R.id.private_key_sign:
                walletSign(false);
                break;
            case R.id.write_pin:
//                writePIN();
                break;
            case R.id.write_pin2:
                EncryptUtils.getDeviceInfo();
                MToast.showToast("芯片固件版本" + (EncryptUtils.rightVersion() ? "是" : "不是") + "：2018/08/14，版本号" + (EncryptUtils.rightVersion() ? "正确" : "错误"));
                break;
            case R.id.finger_test:
                KeyguardManager mKeyguardManager = (KeyguardManager) App.getInstance().getSystemService(Context.KEYGUARD_SERVICE);
                Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
                if (intent != null) {
                    startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
                }
                fingerPrinter();
                break;
            case R.id.ic_sign_test:
                writePriviteKey();

                walletSign(false);
                break;
            case R.id.ic_version_test:

                EncryptUtils.getDeviceInfo();
                MToast.showToast("芯片固件版本" + (EncryptUtils.rightVersion() ? "是" : "不是") + "：2018/08/14");
                break;
        }
    }

    private void writePriviteKey() {
        //导入私钥
        ECKeyPair ke = ECKeyPair
                .create(Numeric.hexStringToByteArray(privetKeyString));
        EncryptUtils.writePrivateKey(ke);
    }
}
