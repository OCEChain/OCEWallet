package com.idea.jgw.ui.createWallet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.idea.jgw.App;
import com.idea.jgw.BaseCallback;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.dialog.RemoveKeyWordsDialog;
import com.idea.jgw.logic.btc.BtcWalltUtils;
import com.idea.jgw.logic.btc.model.TLAppDelegate;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.common.MToast;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = RouterPath.CHECK_KEY_WORDS_ACTIVITY)
public class CheckKeyWordActivity extends BaseActivity implements BaseCallback {

    private static final int REMOVE_KEY_WORD = 22;
    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_of_create_step)
    TextView tvOfCreateStep;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.flowlayout_selected)
    TagFlowLayout flowlayoutSelected;
    @BindView(R.id.flowlayout_src)
    TagFlowLayout flowlayoutSrc;

    //测试keywokds
    String[] key_words = new String[] {"angry", "teant ", "organ ", "novel ", "angle ", "hat ", "siren ", "matter ", "mechanit ", "tent ", "biology ", "husband"};
    private List<String> srcKeywords;
    TagAdapter srcTagAdapter;

    //显示选中keywokds
    private List<String> selectedKeywords;
    TagAdapter selectedTagAdapter;

    String passphrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_key_word;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.set_transaction_pin);

        passphrase = BtcWalltUtils.getPassphrase();//获取助记词
        if (TextUtils.isEmpty(passphrase)) {
            return;
        } else {
            key_words = passphrase.split(" ");
        }

        Arrays.sort(key_words);

        srcKeywords = Arrays.asList(key_words);
        selectedKeywords = new ArrayList<>();
        selectedTagAdapter = new TagAdapter<String>(selectedKeywords) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(CheckKeyWordActivity.this).inflate(R.layout.item_flow_tv2,
                        parent, false);
                tv.setText(s);
                return tv;
            }
        };
        srcTagAdapter = new TagAdapter<String>(srcKeywords) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(CheckKeyWordActivity.this).inflate(R.layout.item_flow_tv,
                        flowlayoutSrc, false);
                tv.setText(s);
                return tv;
            }

        };

        flowlayoutSrc.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int i, FlowLayout flowLayout) {
                if (((TagView) view).isChecked()) {
                    selectedKeywords.add(srcKeywords.get(i));
                    srcTagAdapter.setSelected(i);
                } else {
                    selectedKeywords.remove(srcKeywords.get(i));
                    srcTagAdapter.setUnSelected(i);
                }
                flowlayoutSelected.onChanged();
                return true;
            }
        });

        flowlayoutSelected.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int i, FlowLayout flowLayout) {
                String tag = selectedKeywords.get(i);
                selectedKeywords.remove(tag);
                int pos = srcKeywords.indexOf(tag);
                srcTagAdapter.setUnSelected(pos);
//                flowlayoutSrc.onChanged();
                flowlayoutSelected.onChanged();
                return true;
            }
        });

        flowlayoutSrc.setAdapter(srcTagAdapter);
        flowlayoutSelected.setAdapter(selectedTagAdapter);
    }

    @OnClick({R.id.btn_of_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_next:


//
//                if(App.isWalletDebug){
//                    RemoveKeyWordsDialog dialog = new RemoveKeyWordsDialog(this, REMOVE_KEY_WORD, this);
//                    dialog.show();
//                    return;
//                }






                //判断选中的助记词的顺序，是否跟助记词一致
                if (checkSelect()) {
                    RemoveKeyWordsDialog dialog = new RemoveKeyWordsDialog(this, REMOVE_KEY_WORD, this);
                    dialog.show();
                } else {
                    MToast.showLongToast(R.string.passphrase_err);
                }
                break;
        }
    }


    private boolean checkSelect() {

        if(selectedKeywords.isEmpty())
            return false;

        String  [] ps = passphrase.split(" ");
        int selectedLength = selectedKeywords.size();
        int srcKeywordsLenth = ps.length;
        if (selectedLength == srcKeywordsLenth) {
            for (int i = 0; i < selectedLength; i++) {
                if (!selectedKeywords.get(i).equals(ps[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void callback(Object... parameters) {
        if (parameters.length > 1 && Integer.parseInt(parameters[0].toString().trim()) == REMOVE_KEY_WORD) {
            if (Integer.parseInt(parameters[1].toString().trim()) == RemoveKeyWordsDialog.SELECTED_OK) {
                ARouter.getInstance().build(RouterPath.MAIN_ACTIVITY).navigation();
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
