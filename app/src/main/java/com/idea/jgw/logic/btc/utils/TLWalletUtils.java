package com.idea.jgw.logic.btc.utils;

import org.spongycastle.util.encoders.Hex;
//org.spongycastle.util.encoders.Hex.TLWalletUtils
public class TLWalletUtils {
    public enum TLSendFromType {
        HDWallet,
        ColdWalletAccount,
        ImportedAccount,
        ImportedWatchAccount,
        ImportedAddress,
        ImportedWatchAddress;
        public static TLSendFromType toMyEnum (String myEnumString) {
            try {
                return valueOf(myEnumString);
            } catch (Exception ex) {
                return HDWallet;
            }
        }

        public static TLSendFromType getSendFromType(int idx) {
            if (idx == 0) {
                return HDWallet;
            }
            if (idx == 1) {
                return ColdWalletAccount;
            }
            if (idx == 2) {
                return ImportedAccount;
            }
            if (idx == 3) {
                return ImportedWatchAccount;
            }
            if (idx == 4) {
                return ImportedAddress;
            }
            if (idx == 5) {
                return ImportedWatchAddress;
            }
            return HDWallet;
        }

        public static int getSendFromTypeIdx(TLSendFromType type) {
            if (type == HDWallet) {
                return 0;
            }
            if (type == ColdWalletAccount) {
                return 1;
            }
            if (type == ImportedAccount) {
                return 2;
            }
            if (type == ImportedWatchAccount) {
                return 3;
            }
            if (type == ImportedAddress) {
                return 4;
            }
            if (type == ImportedWatchAddress) {
                return 5;
            }
            return 0;
        }
    }

    public enum TLAccountTxType {
        Send,
        Receive,
        MoveBetweenAccount
    }

    public enum TLAccountType {
        Unknown,
        ColdWallet,
        HDWallet,
        Imported,
        ImportedWatch;
    }

    public enum TLAccountAddressType {
        Imported,
        ImportedWatch
    }

   public  static boolean SHOULD_SAVE_ARCHIVED_ADDRESSES_IN_JSON = false;
    public  static boolean ENABLE_STEALTH_ADDRESS = false;
    public static boolean ALLOW_MANUAL_SCAN_FOR_STEALTH_PAYMENT = true;

    public static String dataToHexString(byte[] data) {
        return Hex.toHexString(data);
    }

    public static byte[] hexStringToData(String hexString){
        return Hex.decode(hexString);
    }

    public static boolean ENABLE_STEALTH_ADDRESS(){
        return ENABLE_STEALTH_ADDRESS;
    }
    public static boolean ALLOW_MANUAL_SCAN_FOR_STEALTH_PAYMENT(){
        return ALLOW_MANUAL_SCAN_FOR_STEALTH_PAYMENT;
    }
}

