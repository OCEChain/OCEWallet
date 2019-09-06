package com.idea.jgw.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.ui.BaseActivity;
import com.idea.jgw.utils.SPreferencesHelper;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.common.ShareKey;
import com.idea.jgw.view.GuideView;
import com.voicevault.vvlibrary.RestResponseObject;
import com.voicevault.vvlibrary.ViGoLibrary;
import com.voicevault.vvlibrary.VoiceVaultAPIUserCallback;
import com.voicevault.vvlibrary.VoiceVaultAPIVoiceCallback;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 语音录入
 */
@Route(path = RouterPath.VOICE_INPUT_ACTIVITY)
public class VoiceInputActivity extends BaseActivity implements VoiceVaultAPIVoiceCallback, VoiceVaultAPIUserCallback {

    // Certified ViGo passphrase to prompt user for, or empty string for DIGITS
    //  e.g. "VoiceVault knows me by the sound of my voice"
    private final static String VIGO_PHRASE_TEXT = "";

    // ViGo configuration identifier, MUST match with the VIGO_PHRASE_TEXT above
    private final static String VIGO_CONFIGURATION_ID = "cba4b51c-8b0f-499c-b2a0-67a3be1e4316";

    // Language for passphrase, do NOT alter unless you have a supported CONFIGURATION_ID from VoiceVault
    //  e.g. "EnglishUnitedStates"
    private static final String VIGO_PHRASE_LANGUAGE = "EnglishUnitedStates";

    /**
     * ViGo library (V1.2+) AudioSource control setting
     * Set to 'true' to tune device for voice recognition during audio recording,
     * can make a significant difference in noisy environments.
     * NOTE: Actual results are Device dependent, please refer to
     * AudioSource API docs and specific device manufacturer specification
     * 是否使用ViGO库对语音进行调优，以便嘈杂环境使用。注:实际结果与设备有关
     */
    public static boolean mEnableAudioRecordVoiceRecognitionSetting = false;
    @BindView(R.id.ll_phrase)
    LinearLayout llPhrase;
    @BindView(R.id.tv_step_desc)
    TextView tvStepDesc;

    // Flag to check if dialogue is still in progress
    private boolean mIsDialogueInProgress = false;

    // Reference to our dialogue id
    private String mDialogueId;

    // Reference to our claimant id
    private String mClaimantId;

    // Recording time in milliseconds, set long enough for the mPromptHint used
    private static final int VIGO_RECORD_TIME_MILLISECS = 4000;

    public static final String REGISTRATION_TAG = "REGISTRATION";


    @BindView(R.id.btn_of_back)
    Button btnOfBack;
    @BindView(R.id.tv_of_title)
    TextView tvOfTitle;
    @BindView(R.id.tv_phrase_1)
    TextView tvPhrase1;
    @BindView(R.id.tv_phrase_2)
    TextView tvPhrase2;
    @BindView(R.id.tv_phrase_3)
    TextView tvPhrase3;
    @BindView(R.id.tv_phrase_4)
    TextView tvPhrase4;
    @BindView(R.id.btn_of_input)
    Button btnOfInput;
    @BindView(R.id.tv_voice_notice)
    TextView tvVoiceNotice;

    boolean mIsRegistration = false;
    String mPromptHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(REGISTRATION_TAG))
            mIsRegistration = getIntent().getBooleanExtra(REGISTRATION_TAG, false);

        mClaimantId = SPreferencesHelper.getInstance(App.getInstance()).getData(ShareKey.KEY_OF_CLAIMANT_ID, "").toString();
        if (mIsRegistration) {
            ViGoLibrary.getInstance().registerClaimant(this);
            tvOfTitle.setText(R.string.voice_input);
        } else {
            registerClaimantCallback(mClaimantId);
            tvOfTitle.setText(R.string.voice_verify);
        }
        startViGo();
    }

    private void startViGo() {
        ViGoLibrary.getInstance().startDialogue(
                mClaimantId, VIGO_CONFIGURATION_ID, VIGO_PHRASE_LANGUAGE, this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_voice_verify;
    }

    @Override
    public void initView() {
        tvOfTitle.setText(R.string.voice_input);
    }

    @OnClick({R.id.btn_of_back, R.id.btn_of_input})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_of_back:
                finish();
                break;
            case R.id.btn_of_input:
                AndPermission.with(this)
                        .runtime()
                        .permission(Permission.RECORD_AUDIO)
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                MToast.showToast(R.string.audio_permission_failed);
                            }
                        })
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                startRecrod();
                            }
                        }).start();

                break;
        }
    }

    private void startRecrod() {
        // update UI
        btnOfInput.setEnabled(false);
        btnOfInput.setSoundEffectsEnabled(false);
        tvVoiceNotice.setText(getString(R.string.status_recording));

        // start recording for preset time
        ViGoLibrary.getInstance().startRecording(VIGO_RECORD_TIME_MILLISECS, mEnableAudioRecordVoiceRecognitionSetting, this);
    }

    @Override
    public void onPhraseSubmitted(RestResponseObject vigoResponse) {

        // only test if valid response
        if (vigoResponse != null) {

            // submitted mPromptHint was accepted but more work to do...
            if (vigoResponse.isAccepted()) {
                // for DIGITS mode the ViGo back-end returns the next prompt
                mPromptHint = vigoResponse.getResult(RestResponseObject.PROMPT_HINT);
                if (initVigoPhrase()) return;

                // prompt for next mPromptHint from user
                tvVoiceNotice.setText(getString(R.string.status_say));
                btnOfInput.setEnabled(true);
                btnOfInput.setSoundEffectsEnabled(false);
            } else if (vigoResponse.isSucceeded()) {
                // CLAIMANT VERIFIED SUCCESSFULLY!

                if (mIsRegistration) {
                    registerSuccess();

                } else {
                    verifySuccess();

                }
            } else {
                // CLAIMANT REJECTED OR UNHANDLED ERROR
                if (!mIsRegistration) {
                    MToast.showToast(R.string.toast_verify_failed);
                } else {
                    MToast.showToast(R.string.voice_register_failed);
                }
                setResult(RESULT_CANCELED);
                finish();
            }
        }

    }

    private boolean initVigoPhrase() {
        // check if DIGITS or PASSPHRASE mode
        if (VIGO_PHRASE_TEXT.isEmpty()) {
            if (TextUtils.isEmpty(mPromptHint) || mPromptHint.length() < 4) {
                MyLog.d("mPromptHint is invalid!");
                return true;
            }
            tvPhrase1.setText(String.valueOf(mPromptHint.charAt(0)));
            tvPhrase2.setText(String.valueOf(mPromptHint.charAt(1)));
            tvPhrase3.setText(String.valueOf(mPromptHint.charAt(2)));
            tvPhrase4.setText(String.valueOf(mPromptHint.charAt(3)));
        }
        return false;
    }

    private void verifySuccess() {
        // SUCCESSFUL VERIFICATION
        // 验证成功
//                    startActivity(new Intent(RecordingActivity.this, LoginActivity.class));
        MToast.showToast(R.string.voice_verify_success);
        setResult(RESULT_OK);
        finish();
    }

    private void registerSuccess() {
        SPreferencesHelper.getInstance(App.getInstance()).saveData(ShareKey.KEY_OF_CLAIMANT_ID, mClaimantId);
        // SUCCESSFUL REGISTRATION
        // 注册成功
        Toast.makeText(getApplicationContext(), getString(R.string.toast_enroll_success), Toast.LENGTH_LONG)
                .show();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onDialogueStarted(String dialogueId, String promptHint) {

        // persist the current dialogue identifier
        mDialogueId = dialogueId;
//        mIsDialogueInProgress = true;

        // update the UI ready to speak
        tvVoiceNotice.setText(getString(R.string.status_say));
        btnOfInput.setEnabled(true);
        btnOfInput.setSoundEffectsEnabled(false);

        mPromptHint = promptHint;
        initVigoPhrase();
    }

    /**
     * Recording complete callback
     * Called after the audio sample has finished recording, and can now be
     * submitted to the ViGo API
     *
     * @param success will be false on audio recorder error
     */
    @Override
    public void onRecordComplete(boolean success) {

        if (!success) {
            // TODO: handle recording failures
            return;
        }

        // update the status from the UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvVoiceNotice.setText(getString(R.string.status_uploading));
            }
        });

        // check if DIGITS or PASSPHRASE mode
        if (VIGO_PHRASE_TEXT.isEmpty()) {
            // for DIGITS send promptHint returned from StartDialogue or onPhraseSubmitted
            ViGoLibrary.getInstance().submitPhrase(mDialogueId, mPromptHint, this);
        } else {
            // for passphrase mode use the predefined mPromptHint
            ViGoLibrary.getInstance().submitPhrase(mDialogueId, VIGO_PHRASE_TEXT, this);
        }

    }

    /**
     * Volume update callback
     * Called every 100mSecs if sample recording or monitoring is started
     *
     * @param level Volume level from 0 to 100
     */
    @Override
    public void onVolumeUpdate(int level) {

        // TODO: update UI volume indicator here...

    }

    /**
     * Record abort callback
     * Called after the audio recorder interface has been stopped and released
     *
     * @param success true if recorder successfully released, or false on error
     */
    @Override
    public void onRecordAborted(boolean success) {

        // TODO: add any record workflow code here...

    }

    /*************************************************************
     * VoiceVaultAPIUserCallback INTERFACE METHODS
     *************************************************************/

    /**
     * ViGo Claimant registration callback
     * Called after a user has been registered as a new claimant by the ViGo back-end
     *
     * @param claimantId the new ViGo user claimant ID, or null on error
     */
    @Override
    public void registerClaimantCallback(String claimantId) {

        if (claimantId == null) {

            // TODO: handle the ViGo registration or network error...
            MToast.showToast(R.string.vigo_get_id_failed);
            tvVoiceNotice.setText(R.string.vigo_get_id_failed);
            return;
        }

        // persist the new user ID
        mClaimantId = claimantId;
        startViGo();
    }
    boolean showGuideView = true;

    @Override
    protected void onResume() {
        super.onResume();
        if(showGuideView && mIsRegistration) {
            setGuideView();
            showGuideView = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        ViGoLibrary.getInstance().abortRecord();
        if (mIsDialogueInProgress)
            ViGoLibrary.getInstance().abortDialogue(mDialogueId);
    }

    GuideView guideView;
    GuideView guideView1;
    GuideView guideView2;
    GuideView guideView3;

    private void setGuideView() {

        // 使用文字
        TextView tv = new TextView(this);
        tv.setText(R.string.voice_steps1);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER);

        // 使用文字
        TextView tv1 = new TextView(this);
        tv1.setText(R.string.voice_steps2);
        tv1.setTextColor(getResources().getColor(R.color.white));
        tv1.setTextSize(16);
        tv1.setGravity(Gravity.CENTER);

        // 使用文字
        final TextView tv2 = new TextView(this);
        tv2.setText(R.string.voice_steps3);
        tv2.setTextColor(getResources().getColor(R.color.white));
        tv2.setTextSize(16);
        tv2.setGravity(Gravity.CENTER);

        // 使用文字
        final TextView tv3 = new TextView(this);
        tv3.setText(R.string.voice_steps4);
        tv3.setTextColor(getResources().getColor(R.color.white));
        tv3.setTextSize(16);
        tv3.setGravity(Gravity.CENTER);


        guideView = GuideView.Builder
                .newInstance(this)
                .setTargetView(btnOfInput)//设置目标
                .setCustomGuideView(tv)
                .setDirction(GuideView.Direction.TOP)
                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置圆形显示区域，
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView.hide();
                        guideView1.show();
                    }
                })
                .build();


        guideView1 = GuideView.Builder
                .newInstance(this)
                .setTargetView(llPhrase)
                .setCustomGuideView(tv1)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置椭圆形显示区域，
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView1.hide();
                        guideView2.show();
                    }
                })
                .build();

        guideView2 = GuideView.Builder
                .newInstance(this)
                .setTargetView(tvVoiceNotice)
                .setCustomGuideView(tv2)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置椭圆形显示区域，
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView2.hide();
                        guideView3.show();
                    }
                })
                .build();


        guideView3 = GuideView.Builder
                .newInstance(this)
                .setTargetView(tvStepDesc)
                .setCustomGuideView(tv3)
                .setDirction(GuideView.Direction.TOP)
                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置矩形显示区域，
                .setRadius(80)          // 设置圆形或矩形透明区域半径，默认是targetView的显示矩形的半径，如果是矩形，这里是设置矩形圆角大小
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView3.hide();
//                        guideView.show();
                    }
                })
                .build();

        guideView.show();
    }
}
