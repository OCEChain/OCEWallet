package com.idea.jgw.utils.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.TELEPHONY_SERVICE;

public class CommonUtils {

    /**
     * 获取SD卡根目录
     *
     * @return
     */
    public static String getSDPath(Context context) {
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))// 如果SD卡存在，则获取跟目录
        {
            return Environment.getExternalStorageDirectory().getAbsolutePath();// 获取跟目录
        } else {
            // 不存在返回当前程序根目录下的temp文件夹
//			return null;
            return context.getFilesDir().getAbsolutePath().toString() + System.getProperty("file.separator") + "temp";
        }
    }

    /**
     * 调用相机照相
     *
     * @param activity
     * @return fileName
     */
    public static String doCamra(Activity activity, String fileName, int requestCode) {
        String filePath = null;
        String sdCarPath = activity.getExternalCacheDir().getPath();
        // 判断获得的SD卡路径是否为null
        if (sdCarPath != null) {
            String hldPath = sdCarPath + "/HldImage/";
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            filePath = hldPath + fileName + "_" + timeStamp;
            // 创建文件夹，名称为HldImage，不会重复创建
            File file = new File(hldPath);
            if (!file.exists() && !file.isDirectory())
                file.mkdir();
            // 调用android自带的照相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = Uri.fromFile(new File(filePath));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(activity, "com.idea.jgw.fileprovider", new File(filePath));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            }
            // 指定照片存储目录
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, requestCode);
        } else {
            filePath = null;
        }
        return filePath;
    }

    /**
     * 调用系统图库截图
     *
     * @param activity
     * @param filePath
     */
    public static String cropImageUri(Activity activity, String filePath, int requestCode) {
        String cropFile = "cropFile.jpg";
        String cropPath;
        String sdCarPath = activity.getExternalCacheDir().getPath();
        // 判断sd卡是否存在
        if (sdCarPath != null) {
            String hldPath = sdCarPath + "/HldImage/";
            // 创建文件夹，名称为HldImage，不会重复创建
            File file = new File(hldPath);
            if (!file.exists() && !file.isDirectory())
                file.mkdir();
            cropPath = hldPath + cropFile;
            // 调用系统截图
            Intent intent = new Intent("com.android.camera.action.CROP");
            Uri imageUri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(activity, "com.idea.jgw.fileprovider", new File(filePath));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            } else {
                imageUri = Uri.fromFile(new File(filePath));
            }
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("scale", true);
            intent.putExtra("aspectX", 5);
            intent.putExtra("aspectY", 5);
//			intent.putExtra("outputX", 720);
            intent.putExtra("outputX", 900);
            intent.putExtra("outputY", 900);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cropPath)));
            intent.putExtra("return-data", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", true); // no face detection
            activity.startActivityForResult(intent, requestCode);
        } else {
            cropPath = null;
        }
        return cropPath;
    }

    /**
     * 调用系统图库
     *
     * @param activity
     * @param requestCode
     */
    public static void openSysPick(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
//        String filePath = null;
//        String sdCarPath = activity.getExternalCacheDir().getPath();
//        // 判断sd卡是否存在
//        if (sdCarPath != null) {
//            String hldPath = sdCarPath + "/HldImage/";
//            filePath = hldPath + fileName;
//            // 创建文件夹，名称为HldImage，不会重复创建
//            File file = new File(hldPath);
//            if (!file.exists() && !file.isDirectory())
//                file.mkdir();
//            // 调用系统图库
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////            intent.setType("image/*");
////            intent.putExtra("crop", "true");
////            intent.putExtra("aspectX", 5);
////            intent.putExtra("aspectY", 5);
////            intent.putExtra("outputX", 900);
////            intent.putExtra("outputY", 900);
////            intent.putExtra("scale", true);
////			intent.putExtra("return-data", false);
////            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
////            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
////            intent.putExtra("noFaceDetection", false); // no face detection
//        } else {
//            filePath = null;
//        }
//        return filePath;
    }

    /**
     * <p>调用系统的文件选择器选择文件</p>
     *
     * @param activity 调用的activity
     */
    public static void pickFile(Activity activity) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, "选择文件上传"), 1001);
        } catch (android.content.ActivityNotFoundException ex) {
            MToast.showToast("找不到文件管理功能");
        }
    }

    /**
     * <p>调用系统的文件选择器选择图片</p>
     *
     * @param activity 调用的activity
     */
    public static void pickImage(Activity activity) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, "选择文件上传"), 1001);
        } catch (android.content.ActivityNotFoundException ex) {
            MToast.showToast("找不到文件管理功能");
        }
    }

    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * <p>根据uri得到文件路径
     * android4.4以下版本</p>
     *
     * @param context 运行环境
     * @param uri     文件的uri
     * @return 文件的存储路径
     */
    public static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        String uriPath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    uriPath = cursor.getString(column_index);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            uriPath = uri.getPath();
        }
        return uriPath;
    }

    /**
     * <p>根据uri得到文件路径
     * android4.4及以上版本</p>
     *
     * @param context 运行环境
     * @param uri     文件的uri
     * @return 文件的存储路径
     */
    @TargetApi(19)
    public static String getRealPathFromUriAboveApi19(Context context, Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * <p>判断某个服务是否正在运行的方法</p>
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceRunning(Context mContext, String serviceName) {
        boolean running = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(60);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                running = true;
                break;
            }
        }
        return running;
    }

    /**
     * 判断当前activity是否运行再前台
     *
     * @param mContext
     * @param activityName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isFrontActivity(Context mContext, String activityName) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        if(activityName.equals(runningActivity)) {
            return true;
        }
        return false;
    }

    //当前应用是否处于前台
    public static boolean isForeground(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo: processes) {
                if (processInfo.processName.equals(context.getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * <p>获取当前app版本号</p>
     *
     * @param context 程序Context
     * @return 当前程序版本号
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * <p>获取当前app版本名称</p>
     *
     * @param context 程序Context
     * @return 当前程序版本号
     */
    public static String getAppName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.1";
    }

    /**
     * <p>获取当前app版本名称</p>
     *
     * @param context 程序Context
     * @return 当前程序版本号
     */
    public static String getAppVersionName2(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName + "." + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0.1";
    }

    /**
     * <p>获取磁盘缓存路径</p>
     *
     * @param context    程序Context
     * @param uniqueName 数据类型区分值
     * @return 磁盘缓存的文件夹
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //部分手机不能获取外部路径，所以要做一个空判断
            if (context.getExternalCacheDir() == null) {
                cachePath = context.getCacheDir().getPath();
            } else {
                cachePath = context.getExternalCacheDir().getPath();
            }
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * <p>获取磁盘缓存路径</p>
     *
     * @param context 运行环境
     * @return 磁盘缓存路径名
     */
    public static String getDiskCachePath(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //获取外部存储的缓存路径
            if (context.getExternalCacheDir() == null) {
                cachePath = context.getCacheDir().getPath();
            } else {
                cachePath = context.getExternalCacheDir().getPath();
            }
        } else {
            //获取内部存储的缓存路径
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * <p>获取磁盘中程序的文件夹路径</p>
     *
     * @param context 运行环境
     * @return 磁盘文件夹路径名
     */
    public static String getDiskFilePath(Context context, String fileName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //获取外部存储的缓存路径
            if (context.getExternalFilesDir(fileName) == null) {
                cachePath = context.getFilesDir().getPath();
            } else {
                cachePath = context.getExternalFilesDir(fileName).getPath();
            }
        } else {
            //获取内部存储的缓存路径
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }

    public static String getDiskFilePath(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //获取外部存储的缓存路径
            cachePath = context.getFilesDir().getPath();
        } else {
            //获取内部存储的缓存路径
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }

    /**
     * <p>得到字符串的hash值</p>
     *
     * @param key 输入的字符串
     * @return 返回的字符串hash值
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * <p>字符串转六十进制</p>
     *
     * @param bytes 要转换的字节数组
     * @return 返回转换后的十六进制
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */

    public static boolean isNetworkAvailable(Context context) {
//        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                // 获取NetworkInfo对象
                NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

                if (networkInfo != null && networkInfo.length > 0) {
                    for (int i = 0; i < networkInfo.length; i++) {
                        System.out.println(i + "===状态===" + networkInfo[i].getState());
                        System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                        // 判断当前网络状态是否为连接状态
                        if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            } else {
//获取所有网络连接的信息
                Network[] networks = connectivityManager.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                //通过循环将网络信息逐个取出来
                for (int i=0; i < networks.length; i++){
                    //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(networks[i]);
                    if(networkInfo.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 得到默认缓存大小，为程序运行内存的8分之一
     *
     * @param context 运行时环境
     * @return 默认的缓存大小
     */
    public static int getMemorySize(Context context) {
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        return maxMemory / 8; //默认内存缓存大小，为程序运行内存的8分之一
    }

    /**
     * <p>获取当前正在运行的activity名字</p>
     *
     * @param context 运行的context
     * @return activity的类名
     */
    public static String getCurrentActivity(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getClassName();
            }
        } else {
            UsageStatsManager sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            if (sUsageStatsManager == null) {
                sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            }
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getClassName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }

    /**
     * <p>调用浏览器打开指定链接</p>
     *
     * @param context 运行的context
     * @param url     要打开的链接
     */
    public static void startActionView(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * <p>通过资源名称字符串和资源类型得到资源id</p>
     *
     * @param variableName 资源名称字符串
     * @param c            资源类型的class
     * @return 资源id
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 数字转百分数
     *
     * @param d       要转换的数字
     * @param decimal 要保留的百分数小数
     * @return 对应的百分比数字
     */
    public static String numberToPercent(double d, int decimal) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(decimal);
        return nf.format(d);
    }

    /**
     * 返回指定小数位数的float
     *
     * @param f       要处理的float值
     * @param decimal 小数位数
     * @return 返回的float
     */
    public static float getFloat(float f, int decimal) {
        BigDecimal bigDecimal = new BigDecimal(f);
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 安装APK文件
     * @param context 运行环境
     * @param filePath 文件路径
     */
    public static void installApk(Context context, String filePath) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context 运行环境
     * @param packageName (包名)(若想判断QQ,则改为com.tencent.mobileqq,若想判断微信,则改为com.tencent.mm)
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        //获取手机系统的所有APP包名，然后进行一一比较
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public static void shareImage(Context context, String packName, String targetActivity, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(packName, targetActivity));//若是分享到QQ，可将包名改为com.tencent.mobileqq，分享页面名改为com.tencent.mobileqq.activity.JumpActivity
        context.startActivity(intent);
    }

    public static void shareImages(Context context, String packName, String targetActivity, ArrayList<Uri> uriList) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(packName, targetActivity));//若是分享到QQ，可将包名改为com.tencent.mobileqq，分享页面名改为com.tencent.mobileqq.activity.JumpActivity
        context.startActivity(intent);
    }

    public static void shareVideoFile(Context context, String packName, String targetActivity, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(packName, targetActivity));//若是分享到QQ，可将包名改为com.tencent.mobileqq，分享页面名改为com.tencent.mobileqq.activity.JumpActivity
        context.startActivity(intent);
    }

    public static void shareVideoFiles(Context context, String packName, String targetActivity, ArrayList<Uri> uriList) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("video/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(packName, targetActivity));//若是分享到QQ，可将包名改为com.tencent.mobileqq，分享页面名改为com.tencent.mobileqq.activity.JumpActivity
        context.startActivity(intent);
    }

    public static double formatDouble2(double d) {
        // 旧方法，已经不再推荐使用
//        BigDecimal bg = new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP);


        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);


        return bg.doubleValue();
    }

    public static float formatFloat2(double d) {
        // 旧方法，已经不再推荐使用
//        BigDecimal bg = new BigDecimal(d).setScale(2, BigDecimal.ROUND_HALF_UP);


        // 新方法，如果不需要四舍五入，可以使用RoundingMode.DOWN
        BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);


        return bg.floatValue();
    }

    /* @author sichard
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     * @return 是否能访问外网
    */
    public static final boolean ping() {

        String result = null;
        try {
            Log.d("------ping-----", "ping start : ");
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
            MyLog.e("----result---"  + "result = " + result);
        }
        return false;
    }
    public static boolean isValidMac(String macStr) {

        if (macStr == null || macStr.equals("")) {
            return false;
        }
        String macAddressRule = "([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}";
        // 这是真正的MAC地址；正则表达式；
        if (macStr.matches(macAddressRule)) {
            Log.i("MAC", "it is a valid MAC address");
            return true;
        } else {
            Log.e("MAC", "it is not a valid MAC address!!!");
            return false;
        }
    }

    public static String replaceNonnumeric(String phonenum) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(phonenum);
        return m.replaceAll("");
    }

    public static boolean pingNet(String ipString) {
        boolean flag = false;
        try {
            // ping ip地址
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 5 " + ipString);
            int status = p.waitFor();
            if (status == 0) {
                flag = true;
            }
        } catch (Exception e) {
            MyLog.e(e.getLocalizedMessage());
        }
        return flag;
    }

    public static boolean compare(String version1, String version2) {
        String flag1 = version1.substring(0, 1);
        String flag2 = version2.substring(0, 1);
        if (!flag1.equals("v") && flag2.equals("v")) {
            return false;
        } else if (flag1.equals("v") && !flag2.equals("v")) {
            return true;
        }

        String regEx = "d|v";
        Pattern p = Pattern.compile(regEx);
        String[] str1 = p.matcher(version1).replaceAll("").split("\\.");
        String[] str2 = p.matcher(version2).replaceAll("").split("\\.");
        if (Integer.parseInt(str1[0]) > Integer.parseInt(str2[0])) {
            return true;
        } else if (Integer.parseInt(str1[0]) == Integer.parseInt(str2[0]) && str1.length > 2 && Integer.parseInt
                (str1[1]) > Integer.parseInt(str2[1])) {
            return true;
        } else if (Integer.parseInt(str1[0]) == Integer.parseInt(str2[0]) && Integer.parseInt
                (str1[1]) == Integer.parseInt(str2[1]) && Integer.parseInt(str1[2]) > Integer
                .parseInt(str2[2])) {
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isSupport5G(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return  manager.is5GHzBandSupported();
    }

    public static void vibrator(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    /**
     * gps获取ip
     * @return
     */
    public static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * wifi获取ip
     * @param context
     * @return
     */
    public static String getIp(Context context){
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager)context. getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化ip地址（192.168.11.1）
     * @param i
     * @return
     */
    private static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    /**
     *  3G/4g网络IP
     */
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本机的ip地址（3中方法都包括）
     * @param context
     * @return
     */
    public static String getIpAdress(Context context){
        String ip = null;
        try {
            ip=getIp(context);
            if (ip==null){
                ip = getIpAddress();
                if (ip==null){
                    ip = getLocalIpAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }
    public static String getIMEI(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getApplicationContext().getSystemService(TELEPHONY_SERVICE);
//        System.out.println("IMEI = " + TelephonyMgr.getImei());
//        System.out.println("IMEI1 = " + TelephonyMgr.getImei(0));
//        System.out.println("  IMEI2 = " + TelephonyMgr.getImei(1));
        return TelephonyMgr.getImei();
    }

    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public static String replace(String srcStr, String replaceStr) {
        String str = "";
        if(!TextUtils.isEmpty(replaceStr)){
            StringBuilder sb = new StringBuilder(srcStr);
            str = sb.replace(3, 7, replaceStr).toString();
        }
        return str;
    }

    public static void setTextPwdInputType(EditText editText) {
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        editText.setSelection(editText.getText().length());
    }

    public static void setTextInputType(EditText editText) {
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_CLASS_TEXT);
        editText.setSelection(editText.getText().length());
    }

}