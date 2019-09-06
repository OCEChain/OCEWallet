package com.idea.jgw.ui.wallet;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.SurfaceView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.zxing.Result;
import com.google.zxing.client.android.AnimeViewCallback;
import com.google.zxing.client.android.BaseCaptureActivity;
import com.google.zxing.client.android.ViewfinderView;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;


@Route(path = RouterPath.QR_SCAN_ACTIVITY)
/**
 * 二维码扫描界面
 * Created by vam on 2018\6\4 0004.
 */
public class QrSanActivity extends BaseCaptureActivity {


    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;

    /**
     * 二维码扫描结果
     */
    public static final String EXTRA_RESULT_QR = "QrSanActivity.EXTRA_RESULT_QR";
    /**
     * 请求的code
     */
    public static final int REQ_CODE = 2 * 234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
    }

    public SurfaceView getSurfaceView() {
        return (surfaceView == null) ? (SurfaceView) findViewById(R.id.preview_view) : surfaceView;
    }


    @Override
    public void dealDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        playBeepSoundAndVibrate();
//        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_LONG).show();
//        对此次扫描结果不满意可以调用
//        reScan();

        String code = rawResult.getText();
        setResult(RESULT_OK, getIntent().putExtra(EXTRA_RESULT_QR, code));
        finish();
    }

    @Override
    public AnimeViewCallback getViewfinderHolder() {
        return (viewfinderView == null) ? (ViewfinderView) findViewById(R.id.viewfinder_view) : viewfinderView;
    }
}
