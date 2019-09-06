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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.idea.jgw.R;
import com.idea.jgw.view.PayPsdInputView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <p>通用选择对话框</p>
 * Created by phper on 2016/5/18.
 */
public class InputTransactionPwdDialog extends Dialog implements View.OnClickListener {
    public static final int SELECTED_CANCEL = 0; //取消事件码
    public static final int SELECTED_OK = -1; //确定事件码
    @BindView(R.id.iv_of_close_dialog)
    ImageView ivOfCloseDialog;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.piv_of_password)
    PayPsdInputView pivOfPassword;
    @BindView(R.id.tv_of_of_notice)
    TextView tvOfOfNotice;

    private PayPsdInputView.OnPasswordListener passwordListener;
    private Context context;

    public InputTransactionPwdDialog(Context context,  PayPsdInputView.OnPasswordListener passwordListener) {
        super(context, R.style.defaultDialog);
        this.context = context;
        this.passwordListener = passwordListener;
        setContentView(getView());
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        pivOfPassword.setOnPasswordListener(passwordListener);
    }

    private View getView() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_of_input_transaction_pwd, null);

        // 设置点击对话框周边消失
        this.setCanceledOnTouchOutside(false);
        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindow.setGravity(Gravity.CENTER);
        //添加动画
//        mWindow.setWindowAnimations(R.style.dialogAnim);
        mWindow.setAttributes(lp);
        return contentView;
    }

    @Override
    public void show() {
        super.show();
        pivOfPassword.setFocusableInTouchMode(true);
        pivOfPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.showSoftInput(pivOfPassword, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
    }

    @Override
    public void dismiss() {
        this.passwordListener = null;
        super.dismiss();
    }

    /**
     * <p>设置标题</p>
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (tvOfTitle != null) {
            if (!TextUtils.isEmpty(title)) {
                tvOfTitle.setText(title);
                tvOfTitle.setVisibility(View.VISIBLE);
            } else {
                tvOfTitle.setVisibility(View.GONE);
            }
        } else {
            Log.e("consumer error", "the title is on a null reference");
        }
    }

    /**
     * <p>设置错误信息</p>
     *
     * @param errMsg  错误信息
     */
    public void setErrorMsg(String errMsg) {
        if (tvOfOfNotice != null) {
            if (!TextUtils.isEmpty(errMsg)) {
                tvOfOfNotice.setText(errMsg);
                tvOfOfNotice.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("consumer error", "the title is on a null reference");
        }
    }

    @OnClick(R.id.iv_of_close_dialog)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_of_close_dialog:
                this.dismiss();
                break;
        }
    }

}
