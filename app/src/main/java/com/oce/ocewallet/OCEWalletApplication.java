/**
 * Copyright 2016 oce Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oce.ocewallet;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.oce.ocewallet.domain.DaoMaster;
import com.oce.ocewallet.domain.DaoSession;
import com.oce.ocewallet.utils.AppFilePath;
import com.oce.ocewallet.utils.AppUtils;
import com.oce.ocewallet.utils.SharedPreferencesUtil;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;



public class OCEWalletApplication extends Application {

    private static OCEWalletApplication sInstance;

    private RefWatcher refWatcher;

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static RefWatcher getRefWatcher(Context context) {
        OCEWalletApplication application = (OCEWalletApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        sInstance = this;
        AppUtils.init(this);
        AppFilePath.init(this);
//        CrashHandler.getInstance().init(this);
        initPrefs();
        initGreenDao();
    }

    private void initGreenDao() {
        //创建数据库表
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "wallet", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
    }

    public static OCEWalletApplication getsInstance() {
        return sInstance;
    }

    /**
     * 初始化SharedPreference
     */
    protected void initPrefs() {
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

}
