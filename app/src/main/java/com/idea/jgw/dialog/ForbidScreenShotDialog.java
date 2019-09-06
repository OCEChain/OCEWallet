package com.idea.jgw.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.idea.jgw.BaseCallback;
import com.idea.jgw.R;
import com.idea.jgw.utils.common.DipPixelUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <p>通用选择对话框</p>
 * Created by phper on 2016/5/18.
 */
public class ForbidScreenShotDialog extends Dialog implements View.OnClickListener {
    public static final int SELECTED_CANCEL = 0; //取消事件码
    public static final int SELECTED_OK = -1; //确定事件码
    @BindView(R.id.btn_in_dialog_of_ok)
    Button btnInDialogOfOk;

    private BaseCallback callback;
    private Context context;
    private int requestCode;

    public ForbidScreenShotDialog(Context context, int requestCode, BaseCallback callback) {
        super(context, R.style.defaultDialog);
        this.context = context;
        this.requestCode = requestCode;
        this.callback = callback;
        setContentView(getView());
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
    }

    private View getView() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_of_forbid_screen_shot, null);

        // 设置点击对话框周边消失
        this.setCanceledOnTouchOutside(false);
        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.verticalMargin = DipPixelUtils.dip2px(context, 16);
        mWindow.setGravity(Gravity.CENTER);
        //添加动画
//        mWindow.setWindowAnimations(R.style.dialogAnim);
        mWindow.setAttributes(lp);
        return contentView;
    }

    @Override
    public void dismiss() {
        this.callback = null;
        super.dismiss();
    }

    @OnClick(R.id.btn_in_dialog_of_ok)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_in_dialog_of_ok:
                this.dismiss();
                break;
        }
    }

}
