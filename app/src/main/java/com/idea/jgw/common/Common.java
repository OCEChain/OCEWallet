package com.idea.jgw.common;

import android.os.Environment;

/**
 * Created by vam on 2018\6\4 0004.
 */

public class Common {

    public static final class FilePath {
        public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        public static final String ROOT_DIR = SDCARD_PATH + "/jgw";
        public static final String WALLET_ETH_PATH = ROOT_DIR + "/wallet/eth";
    }


    public static final class Jgw{
        /**
         * 请求的url
         */
        public static final String URL = "http://120.132.120.253:8546" ;   //"https://api.myetherapi.com/eth";// " http://120.132.120.253:8546";

        /**
         * 智能合约地址
         */
        public static final String SMART_CONTRACT = "0x9f6e483ca730907583de27ad30596448a562b362";// "0x48a4e9f23a4abfbe1a7b598b4ac2675f5206baed";//



    }


    public static enum CoinTypeEnum {
        BTC("btc",0),
        ETH("eth",1),
        JGW("jgw",2),
        OCE("oce",3)
        ;
        // 成员变量
        private String name;
        private int index;

        // 构造方法
        private CoinTypeEnum(String name, int index) {
            this.name = name;
            this.index = index;
        }

        // 普通方法
        public static String getName(int index) {
            for (BtcErrorEnum c : BtcErrorEnum.values()) {
                if (c.getIndex() == index) {
                    return c.name;
                }
            }
            return null;
        }

        // get set 方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public static final class Eth {



        /**
         * 请求的url
         */
//        public static final String URL = "http://api.myetherapi.com/eth";//"http://10.0.2.2:8555";// " http://120.132.120.253:8546";
//        public static final String URL = "http://120.132.120.253:8546";//测试  http://120.132.120.253:8546
        public static final String URL = "http://120.132.120.252:8545";//正式  http://120.132.120.252:8545
        /**
         * 智能合约地址
         */
        public static final String SMART_CONTRACT = "0x9f6e483ca730907583de27ad30596448a562b362";// "0x48a4e9f23a4abfbe1a7b598b4ac2675f5206baed";//




        /**
         * 种子
         */
        public static final String PREFERENCES_SHANG_KEY = "eth_PREFERENCES_SHANG_KEY";
        /**
         * 以太币私钥
         */
        public static final String PREFERENCES_PRIVET_KEY = "eth_PREFERENCES_PRIVET_KEY";
        /**
         * 以太币公钥
         */
        public static final String PREFERENCES_PUBLIC_KEY = "eth_PREFERENCES_PUBLIC_KEY";
        /**
         * 以太的密码
         */
        public static final String PREFERENCES_PWD_KEY = "eth_PREFERENCES_PWD_KEY";

        public static final String  PREFERENCES_ADDRESS_KEY="eth_PREFERENCES_ADDRESS_KEY";

        /**
         * 文件路径
         */
        public static final String FILE_DIR = "eth_file_dir";
        /**
         * 文件名字
         */
        public static final String FILE_NAME ="eth_file_name";
    }

    public static final class Btc {
        /**
         * 助记词
         */
        public static final String PREFERENCES_PASSPHRASE_KEY = "btc_PREFERENCES_PASSPHRASE_KEY";

        public static final String ACCESS_NAME = "access_01";
    }

    public static final class Broadcast {
        /**
         * 发送以太的结果 action
         **/
        public static final String SEND_ETH_RESULT = "eth_SEND_ETH_RESULT";
        /**
         * 发送以太的结果 data
         */
        public static final String SEND_ETH_RESULT_DATA = "eth_SEND_ETH_RESULT_DATA";
    }

    public static enum BtcErrorEnum {
        //        无法获得动态的费用。回落固定交易费。（费可以在审核支付进行配置）
        ERROR_ADDRES_INVALID("地址不合格", 1), ERROR_AMOUNT_INVALID("金额不合法", 2), ERROR_DYNAMIC_FEES_FAIL("无法获得动态的费用。回落固定交易费。（费可以在审核支付进行配置）", 3),
        ERROR_FETCHING_UNSPENT_OUTPUTS_TRY_AGAIN_LATER("错误：获取未使用的输出。稍后再试。", 4), ERROR_MORE_FUNDS_NEEDED("余额不足", 5)
        ,ERROR_FETCHING_UNSPENT_OUTPUTS_TRY_AGAIN("错误：获取未使用的输出。再试一次。",6)
        ,ERROR_FUNDS_MAY_BE_PENDING_CONFIRMATION("有些在确认中，不可用",7)
        ,ERROR_INSUFFICIENT_FUNDS_DUST("余额不足",8)
        ,ERROR_INSUFFICIENT_FUNDS_ACCOUNT_BALANCE("余额不足",9)
        ,ERROR_CANNOT_CREATE_TRANSACTIONS_WITH_OUTPUTS_LESS_THEN("太低的事务不接受",10)
        ,ERROR_UNDEFINABLE_EXCEPTION("不可描述的异常",11)
        ,ERROR_DOES_NOT_SUPPORT_CLOUD_WALLET("不支持云钱包",12)
        ,ERROR("错误",13)
        ;
        // 成员变量
        private String name;
        private int index;

        // 构造方法
        private BtcErrorEnum(String name, int index) {
            this.name = name;
            this.index = index;
        }

        // 普通方法
        public static String getName(int index) {
            for (BtcErrorEnum c : BtcErrorEnum.values()) {
                if (c.getIndex() == index) {
                    return c.name;
                }
            }
            return null;
        }

        // get set 方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public static class Constant {
        public static final String LOGIN_PWD = "login_pwd";//加密后的
        public static final String LOGIN_PASSWORDD = "login_password";//未加密
    }

}
