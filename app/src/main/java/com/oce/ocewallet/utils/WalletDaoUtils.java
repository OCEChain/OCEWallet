package com.oce.ocewallet.utils;

import android.text.TextUtils;

import com.oce.ocewallet.OCEWalletApplication;
import com.oce.ocewallet.domain.OCEWallet;
import com.oce.ocewallet.domain.OCEWalletDao;

import java.util.List;



public class WalletDaoUtils {
    public static OCEWalletDao oceWalletDao = OCEWalletApplication.getsInstance().getDaoSession().getOCEWalletDao();

    /**
     * 插入新创建钱包
     *
     * @param oceWallet 新创建钱包
     */
    public static void insertNewWallet(OCEWallet oceWallet) {
        updateCurrent(-1);
        oceWallet.setCurrent(true);
        oceWalletDao.insert(oceWallet);
    }

    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
    public static OCEWallet updateCurrent(long id) {
        List<OCEWallet> oceWallets = oceWalletDao.loadAll();
        OCEWallet currentWallet = null;
        for (OCEWallet ethwallet : oceWallets) {
            if (id != -1 && ethwallet.getId() == id) {
                ethwallet.setCurrent(true);
                currentWallet = ethwallet;
            } else {
                ethwallet.setCurrent(false);
            }
            oceWalletDao.update(ethwallet);
        }
        return currentWallet;
    }

    /**
     * 获取当前钱包
     *
     * @return 钱包对象
     */
    public static OCEWallet getCurrent() {
        List<OCEWallet> oceWallets = oceWalletDao.loadAll();
        for (OCEWallet ethwallet : oceWallets) {
            if (ethwallet.isCurrent()) {
                ethwallet.setCurrent(true);
                return ethwallet;
            }
        }
        return null;
    }

    /**
     * 查询所有钱包
     */
    public static List<OCEWallet> loadAll() {
        return oceWalletDao.loadAll();
    }

    /**
     * 检查钱包名称是否存在
     *
     * @param name
     * @return
     */
    public static boolean walletNameChecking(String name) {
        List<OCEWallet> oceWallets = loadAll();
        for (OCEWallet oceWallet : oceWallets
                ) {
            if (TextUtils.equals(oceWallet.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置isBackup为已备份
     *
     * @param walletId 钱包Id
     */
    public static void setIsBackup(long walletId) {
        OCEWallet oceWallet = oceWalletDao.load(walletId);
        oceWallet.setIsBackup(true);
        oceWalletDao.update(oceWallet);
    }

    /**
     * 以助记词检查钱包是否存在
     *
     * @param mnemonic
     * @return
     */
    public static boolean checkRepeatByMenmonic(String mnemonic) {
        List<OCEWallet> oceWallets = loadAll();
        for (OCEWallet oceWallet : oceWallets
                ) {
            if (TextUtils.equals(oceWallet.getMnemonic().trim(), mnemonic.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkRepeatByKeystore(String keystore) {
        return false;
    }

    /**
     * 修改钱包名称
     *
     * @param walletId
     * @param name
     */
    public static void updateWalletName(long walletId, String name) {
        OCEWallet wallet = oceWalletDao.load(walletId);
        wallet.setName(name);
        oceWalletDao.update(wallet);
    }

    public static void setCurrentAfterDelete() {
        List<OCEWallet> oceWallets = oceWalletDao.loadAll();
        if (oceWallets != null && oceWallets.size() > 0) {
            OCEWallet oceWallet = oceWallets.get(0);
            oceWallet.setCurrent(true);
            oceWalletDao.update(oceWallet);
        }
    }
}
