//package com.idea.jgw.test;
//
//import org.spongycastle.asn1.x9.X9ECParameters;
//import org.spongycastle.crypto.digests.SHA256Digest;
//import org.spongycastle.crypto.ec.CustomNamedCurves;
//import org.spongycastle.crypto.params.ECDomainParameters;
//import org.spongycastle.crypto.signers.ECDSASigner;
//import org.spongycastle.crypto.signers.HMacDSAKCalculator;
//import org.web3j.crypto.ECDSASignature;
//import org.web3j.crypto.ECKeyPair;
//import org.web3j.crypto.Hash;
//import org.web3j.crypto.RawTransaction;
//import org.web3j.crypto.Sign;
//import org.web3j.utils.Numeric;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//
//public class Test {
//    private static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
//    static final ECDomainParameters CURVE = new ECDomainParameters(
//            CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
//    public static void main(String[] args) {
//
//        //模拟创建一笔交易
//        BigDecimal decimal = new BigDecimal(10).multiply(new BigDecimal(10).pow(18));
//        BigInteger value = decimal.toBigInteger();
//        RawTransaction etherTransaction = RawTransaction.createEtherTransaction(new BigInteger("1"),new BigInteger("1"),new BigInteger("100000"),"0xa2f7174fb5c7ce4a4fe6594c4d167134b64dbac8",value);
//
//
//        //导入私钥
//        ECKeyPair ke = ECKeyPair
//                .create(Numeric.hexStringToByteArray("C68FB09938412661E20EBD7E31CAF8C4B658AE939AEA102D41E11545968DC464"));
//
//
//        //----签名开始
//        //签名前处理
//        byte[] encode = TransactionEncoder.encode(etherTransaction);
//        byte[] messageHash = Hash.sha3(encode);
//
//        //由芯片通过私钥来做具体签名
//        ECDSASignature sig = ke.sign(messageHash);
//
//        //签名后处理,需要用到公钥匙
//        BigInteger publicKey = ke.getPublicKey();
//        int recId = -1;
//        for (int i = 0; i < 4; i++) {
//            try {
//                Class clazz = Class.forName("org.web3j.crypto.Sign");
//                Method method = clazz.getDeclaredMethod("recoverFromSignature", int.class, ECDSASignature.class, byte[].class);
//                BigInteger k = (BigInteger) method.invoke(null, i, sig, messageHash);
//                if (k != null && k.equals(publicKey)) {
//                    recId = i;
//                    break;
//                }
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
////            BigInteger k = Sign.recoverFromSignature(i, sig, messageHash);
////            if (k != null && k.equals(publicKey)) {
////                recId = i;
////                break;
////            }
//        }
//        if (recId == -1) {
//            throw new RuntimeException(
//                    "Could not construct a recoverable key. This should never happen.");
//        }
//
//        int headerByte = recId + 27;
//
//        // 1 header + 32 bytes for R + 32 bytes for S
//        byte v = (byte) headerByte;
//        byte[] r = Numeric.toBytesPadded(sig.r, 32);
//        byte[] s = Numeric.toBytesPadded(sig.s, 32);
//
//        Sign.SignatureData signatureData=new Sign.SignatureData(v, r, s);
//        byte[] signedMessage = TransactionEncoder.encode(etherTransaction, signatureData);
//        //----签名结束
//
//
//        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
//
//        // ECPrivateKeyParameters privKey = new ECPublicKeyParameters(ke.getPublicKey(), CURVE);
//        //signer.init(false, privKey);
//        // BigInteger[] components = signer.verifySignature(sig,Numeric.toBigInt(signatureData.getR()),Numeric.toBigInt(signatureData.getS()));
//
//
//
//
//        //这是自带的签名,用来比对
//        byte[] bytes = TransactionEncoder.signMessage(etherTransaction, ke);
//
//
//
//
//        System.out.println(Numeric.toHexString(bytes));
//        System.out.println(Numeric.toHexString(signedMessage));
//    }
//}
//
