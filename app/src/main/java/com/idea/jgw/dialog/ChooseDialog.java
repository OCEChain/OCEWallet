package com.idea.jgw.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.idea.jgw.BaseCallback;
import com.idea.jgw.R;
import com.idea.jgw.utils.common.DipPixelUtils;

/**
 * <p>通用选择对话框</p>
 * Created by phper on 2016/5/18.
 */
public class ChooseDialog extends Dialog implements View.OnClickListener {
    public static final int SELECTED_CANCEL = 0; //取消事件码
    public static final int SELECTED_OK = -1; //确定事件码
    private BaseCallback callback;
    private Context context;
    private int requestCode;
    private ImageView iv_in_dialog_of_icon;
    private TextView tv_in_dialog_of_title;
    private TextView tv_in_dialog_of_content;
    private Button btn_in_dialog_of_ok;
    private Button btn_in_dialog_of_cancel;

    public ChooseDialog(Context context, int requestCode, BaseCallback callback) {
        super(context, R.style.defaultDialog);
        this.context = context;
        this.requestCode = requestCode;
        this.callback = callback;
        setContentView(getView());

    }

    private View getView() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_of_choose, null);
        iv_in_dialog_of_icon = (ImageView) contentView.findViewById(R.id.iv_in_dialog_of_icon);
        tv_in_dialog_of_title = (TextView) contentView.findViewById(R.id.tv_in_dialog_of_title);
        tv_in_dialog_of_content = (TextView) contentView.findViewById(R.id.tv_in_dialog_of_content);
        btn_in_dialog_of_cancel = (Button) contentView.findViewById(R.id.btn_in_dialog_of_cancel);
        btn_in_dialog_of_cancel.setOnClickListener(this);
        btn_in_dialog_of_ok = (Button) contentView.findViewById(R.id.btn_in_dialog_of_ok);
        btn_in_dialog_of_ok.setOnClickListener(this);

        // 设置点击对话框周边消失
        this.setCanceledOnTouchOutside(false);
        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = DipPixelUtils.dip2px(context, 173);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindow.setGravity(Gravity.BOTTOM);
        //添加动画
//        mWindow.setWindowAnimations(R.style.dialogAnim);
//        mWindow.setAttributes(lp);
        return contentView;
    }

    /**
     * <p>设置标题</p>
     * @param title 标题
     */
    public void setTitle(String title) {
        if(tv_in_dialog_of_title != null) {
            if(!TextUtils.isEmpty(title)) {
                tv_in_dialog_of_title.setText(title);
                tv_in_dialog_of_title.setVisibility(View.VISIBLE);
            } else {
                tv_in_dialog_of_title.setVisibility(View.GONE);
            }
        } else {
            Log.e("consumer error", "the title is on a null reference");
        }
    }

    /**
     * <p>设置显示内容</p>
     * @param msg 显示内容
     */
    public void setMsg(String msg) {
        if(tv_in_dialog_of_content != null) {
            if(!TextUtils.isEmpty(msg)) {
                tv_in_dialog_of_content.setText(msg);
                tv_in_dialog_of_content.setVisibility(View.VISIBLE);
            } else {
                tv_in_dialog_of_content.setVisibility(View.GONE);
            }
        } else {
            Log.e("consumer error", "the msg is on a null reference");
        }
    }

    /**
     * <p>设置确定按钮文字</p>
     * @param okText 要设置文字
     */
    public void setOKText(String okText) {
        if(btn_in_dialog_of_ok != null) {
            btn_in_dialog_of_ok.setText(okText);
        } else {
            Log.e("consumer error", "the okText is on a null reference");
        }
    }

    /**
     * <p>设置确定按钮文字</p>
     * @param cancelText 要设置文字
     */
    public void setCancelText(String cancelText) {
        if(btn_in_dialog_of_cancel != null) {
            btn_in_dialog_of_cancel.setText(cancelText);
        } else {
            Log.e("consumer error", "the cancelText is on a null reference");
        }
    }

    /**
     * <p>设置dialog图标</p>
     * @param resId 图片资源id
     */
    public void setIconResId(int resId) {
        if(iv_in_dialog_of_icon != null) {
            if(resId <= 0) {
                iv_in_dialog_of_icon.setVisibility(View.GONE);
            } else {
                iv_in_dialog_of_icon.setBackgroundResource(resId);
                iv_in_dialog_of_icon.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("consumer error", "the msg is on a null reference");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_in_dialog_of_cancel:
                callback.callback(requestCode, SELECTED_CANCEL);
                this.dismiss();
                break;
            case R.id.btn_in_dialog_of_ok:
                callback.callback(requestCode, SELECTED_OK);
                this.dismiss();
                break;
        }
    }

}
