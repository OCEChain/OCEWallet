package com.oce.ocewallet.base;

import com.oce.ocewallet.utils.AppUtils;
import com.oce.ocewallet.utils.FileUtils;


public class Constants {

    public static String BASE_URL = "接口公共url";

    public static String PATH_DATA = FileUtils.createRootPath(AppUtils.getAppContext()) + "/cache";

    public static String PATH_TXT = PATH_DATA + "/token/";

    public static final String SUFFIX_ZIP = ".zip";

    public static String PATH_EPUB = PATH_DATA + "/epub";
}
