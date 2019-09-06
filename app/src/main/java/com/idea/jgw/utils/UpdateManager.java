package com.idea.jgw.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.view.ColorPregressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateManager {
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    private static final int DOWNLOAD_FAILED = 3;
    private String apkUrl = "";
    /* 下载保存路径 */
//	private String mSavePath;
    /* 记录进度条数量 */
//	private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Context mContext;
    /* 更新进度条 */
    private ColorPregressBar mProgress;
    private Dialog mDownloadDialog;
    private long lastTouchTime;
    private DownloadListener downloadListener;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    int progress = msg.arg1;
                    if (downloadListener != null) {
                        downloadListener.onProgress(progress);
                    }
                    // 设置进度条位置
                    if (mProgress != null)
                        mProgress.setProgress(progress);
//				long currentTime = System.currentTimeMillis();
//				if(currentTime - lastTouchTime > 20000) {
//					lastTouchTime = currentTime;
//					mContext.sendBroadcast(new Intent(ScreenListenerService.ACTION_TOUCH_SCREEN));
//					MyLog.e("dispatchTouchEvent ======");
//				}
                    break;
                case DOWNLOAD_FAILED:
                    if (downloadListener != null) {
                        downloadListener.onFail((Exception) msg.obj);
                    }
                    break;
                case DOWNLOAD_FINISH:
                    if (downloadListener != null) {
                        downloadListener.onFinish();
                    }
//                if(msg.obj.toString().endsWith(".apk")) {
//                    // 安装文件
//                    installApk(msg.obj.toString());
//				}
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    public UpdateManager(Context context, DownloadListener downloadListener) {
        this.mContext = context;
        this.downloadListener = downloadListener;
    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.dialog_progress, null);

        mDownloadDialog = new AlertDialog.Builder(mContext).create();

        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.setCancelable(false);
        mProgress = (ColorPregressBar) layout.findViewById(R.id.update_progressbar);

        mDownloadDialog.show();
        Window dialogWindow = mDownloadDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = App.getInstance().getResources().getDisplayMetrics();
        lp.width = d.widthPixels;
        lp.height = d.heightPixels;
        dialogWindow.setAttributes(lp);
        dialogWindow.setContentView(layout);
    }

    public void downloadApk(String url) {
        downloadApk(url, null);
    }

    public void downloadApk(String url, String fileName) {
        downloadApk(url, fileName, false);
    }

    /**
     * 下载apk文件
     */
    public void downloadApk(String url, String fileName, boolean showDialog) {
        if (showDialog)
            showDownloadDialog();
        // 启动新线程下载软件
        apkUrl = url;
        if (TextUtils.isEmpty(fileName)) {
            String[] strings = apkUrl.split("/");
            fileName = strings[strings.length - 1];
        }
        new downloadApkThread(fileName).start();
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        private String fileName;

        public downloadApkThread(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void run() {
            Message msg = Message.obtain();
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    URL url = new URL(apkUrl);

                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File apkFile = new File(fileName);
                    File fileDir = apkFile.getParentFile();
                    if (apkFile.exists()) {
                        apkFile.delete();
                    }
                    // 判断文件目录是否存在
                    if (!fileDir.exists()) {
                        fileDir.mkdir();
                    }
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        msg = Message.obtain();
                        // 计算进度条位置
                        msg.arg1 = (int) (((float) count / length) * 100);
                        if (numread <= 0) {
                            // 下载完成
                            msg.obj = fileName;
                            msg.what = DOWNLOAD_FINISH;
                            mHandler.sendMessage(msg);
                            break;
                        } else {
                            msg.what = DOWNLOAD;
                            mHandler.sendMessage(msg);
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                sendFailedMsg(e);
            } catch (IOException e) {
                e.printStackTrace();
                sendFailedMsg(e);
            }
            // 取消下载对话框显示
            if (mDownloadDialog != null)
                mDownloadDialog.dismiss();
        }
    }

    public void sendFailedMsg(IOException e) {
        Message msg;
        msg = Message.obtain();
        msg.what = DOWNLOAD_FAILED;
        msg.obj = e;
        mHandler.sendMessage(msg);
    }

    public interface DownloadListener {
        void onFinish();

        void onFail(Exception e);

        int onProgress(int progress);
    }
}
