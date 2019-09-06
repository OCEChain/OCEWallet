package com.idea.jgw.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.idea.jgw.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <p>通用选择对话框</p>
 * Created by phper on 2016/5/18.
 */
public class ChooseLoginTypeDialog extends Dialog implements View.OnClickListener {
    public static final int CANCEL = 0; //取消事件码
    public static final int PWD_LOGIN = -1; //密码登录
    public static final int VOICE_LOGIN = -2; //声纹登录
    @BindView(R.id.tv_of_voice_login)
    TextView tvOfTakePhoto;
    @BindView(R.id.tv_of_pwd_login)
    TextView tvOfPickFromAlbum;
    @BindView(R.id.tv_of_cancel)
    TextView tvOfCancel;

    private Context context;
    private OnChooseListener chooseListener;

    public ChooseLoginTypeDialog(Context context, OnChooseListener chooseListener) {
        super(context, R.style.defaultDialog);
        this.context = context;
        this.chooseListener = chooseListener;
        setContentView(getView());
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
    }

    private View getView() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_of_choose_login_type, null);

        // 设置点击对话框周边消失
        this.setCanceledOnTouchOutside(false);
        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindow.setGravity(Gravity.BOTTOM);
        //添加动画
//        mWindow.setWindowAnimations(R.style.dialogAnim);
        mWindow.setAttributes(lp);
        return contentView;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @OnClick({R.id.tv_of_voice_login, R.id.tv_of_pwd_login, R.id.tv_of_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_of_voice_login:
                chooseListener.choose(VOICE_LOGIN);
                break;
            case R.id.tv_of_pwd_login:
                chooseListener.choose(PWD_LOGIN);
                break;
            case R.id.tv_of_cancel:
                chooseListener.choose(CANCEL);
                break;
        }
    }

    public interface OnChooseListener {
        void choose(int which);
    }
}
