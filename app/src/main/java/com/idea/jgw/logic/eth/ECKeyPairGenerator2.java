package com.idea.jgw.logic.eth;

import com.idea.jgw.App;
import com.idea.jgw.common.Common;
import com.idea.jgw.utils.SPreferencesHelper;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import org.spongycastle.math.ec.WNafUtil;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 覆盖了 ECKeyPairGenerator，为了获取到随机数（shangInt）
 * Created by Administrator on 2018\6\1 0001.
 */
public class ECKeyPairGenerator2 extends org.spongycastle.crypto.generators.ECKeyPairGenerator implements AsymmetricCipherKeyPairGenerator, ECConstants {

    /**
     * 生成钱包的随机数
     *
     * @TDD 这个地方后面要做处理
     */
    public static BigInteger shangInt;

    ECDomainParameters params;
    SecureRandom random;


    public void init(
            KeyGenerationParameters param) {
        ECKeyGenerationParameters ecP = (ECKeyGenerationParameters) param;

        random = ecP.getRandom();
        params = ecP.getDomainParameters();

        if (this.random == null) {
            this.random = new SecureRandom();
        }
    }


    /**
     * Given the domain parameters this routine generates an EC key
     * pair in accordance with X9.62 section 5.2.1 pages 26, 27.
     */
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger n = params.getN();
        int nBitLength = n.bitLength();
        int minWeight = nBitLength >>> 2;



        for (; ; ) {
            shangInt = new BigInteger(nBitLength, random);

            if (shangInt.compareTo(TWO) < 0 || (shangInt.compareTo(n) >= 0)) {
                continue;
            }

            if (WNafUtil.getNafWeight(shangInt) < minWeight) {
                continue;
            }

            break;
        }
//        shangInt = new BigInteger("53600279875579076219471941826813524085572497673035807190274606665287540218836");
        //种子
//        SPreferencesHelper.getInstance(App.getInstance()).saveData(Common.Eth.PREFERENCES_SHANG_KEY,shangInt.toString());

        ECPoint Q = createBasePointMultiplier().multiply(params.getG(), shangInt);

        return new AsymmetricCipherKeyPair(
                new ECPublicKeyParameters(Q, params),
                new ECPrivateKeyParameters(shangInt, params));
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
