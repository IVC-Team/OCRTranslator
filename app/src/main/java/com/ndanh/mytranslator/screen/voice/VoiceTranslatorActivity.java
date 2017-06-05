package com.ndanh.mytranslator.screen.voice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.Manifest;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.base.NavigatorFooterActivity;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.HistoryDaoImp;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.util.CLog;
import com.ndanh.mytranslator.util.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceTranslatorActivity extends NavigatorFooterActivity implements VoiceTranslatorContract.IVoiceTranslatorView {

    //Bind View Region
    @BindView(R.id.action_choose_source)
    Button chooseSourceLang;
    @BindView(R.id.action_choose_dest)
    Button chooseDestLang;
    @BindView ( R.id.text_source )
    TextView textSource;
    @BindView ( R.id.text_translate )
    TextView textTranslate;
    @BindView ( R.id.micro_action )
    ToggleButton toggleButton;
    @BindView ( R.id.micro_background )
    RelativeLayout microBG;
    @BindView ( R.id.navigation_footer_voice )
    RelativeLayout hiddenPanel;

    //private properties
    private @IdRes
    int selectedButtonId;
    private Language.ELanguage srcLang, destLang;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private static final String LOG_TAG = "VoiceTranslatorActivity";
    private Map<Language.ELanguage, Intent> intentMap = new HashMap<Language.ELanguage, Intent>();
    private VoiceTranslatorContract.IVoiceTranslatorPresenter mPresenter;
    private static final String STRING_EMPTY = "";
    //Listeners

    private RecognitionListener recognitionListener = new RecognitionListener () {
        @Override
        public void onBeginningOfSpeech() {
            CLog.i(LOG_TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            CLog.i(LOG_TAG, "onBufferReceived: " + buffer);
        }

        @Override
        public void onEndOfSpeech() {
            CLog.i(LOG_TAG, "onEndOfSpeech");
            toggleButton.setChecked(false);
        }

        @Override
        public void onError(int errorCode) {
            String errorMessage = getErrorText(errorCode);
            CLog.d(LOG_TAG, "FAILED " + errorMessage);
            textSource.setText(errorMessage);
            textTranslate.setText(STRING_EMPTY);
            toggleButton.setChecked(false);
            stopRecognizer();
        }

        @Override
        public void onEvent(int arg0, Bundle arg1) {
            CLog.i(LOG_TAG, "onEvent");
        }

        @Override
        public void onPartialResults(Bundle arg0) {
            CLog.i(LOG_TAG, "onPartialResults");
        }

        @Override
        public void onReadyForSpeech(Bundle arg0) {
            CLog.i(LOG_TAG, "onReadyForSpeech");
        }

        @Override
        public void onResults(Bundle results) {
            CLog.i(LOG_TAG, "onResults");
            ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = matches.size () > 0 ? matches.get(0) : STRING_EMPTY;
            textSource.setText(text);
            if(srcLang == destLang)
                textTranslate.setText(text);
            else
                mPresenter.doTranslate ();
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            CLog.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        }

    };

    private PopupMenu.OnMenuItemClickListener popupMenuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Button tempButton = (Button) findViewById( selectedButtonId );
            switch (selectedButtonId){
                case R.id.action_choose_source:
                    srcLang = Language.setLangFromMenu(item.getItemId ());
                    setRecognizeIntent ();
                    tempButton.setText(Language.getLongLanguage ( srcLang ));
                    break;
                case R.id.action_choose_dest:
                    destLang = Language.setLangFromMenu(item.getItemId ());
                    tempButton.setText(Language.getLongLanguage ( destLang ));
                    break;
                default:
                    break;
            };
            return true;
        }
    };

    private CompoundButton.OnCheckedChangeListener toggleButtonListener = new CompoundButton.OnCheckedChangeListener () {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            while(speech == null){
                startRecognizer();
            }
            if (isChecked) {
                microBG.setBackgroundResource ( R.drawable.circle_green );
                speech.startListening(recognizerIntent);
            } else {
                microBG.setBackgroundResource ( R.drawable.circle );
                speech.stopListening();
            }
        }
    };

    //Override activitie methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter ();
        setContentView( R.layout.activity_voice_translator);
        ButterKnife.bind ( this );
        initView();
        PermissionHelper.requestPermission ( this, Manifest.permission.RECORD_AUDIO );
        PermissionHelper.requestPermission ( this, Manifest.permission.WRITE_EXTERNAL_STORAGE );
    }

    @Override
    protected void onStart() {
        super.onStart ();
        this.mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mPresenter.stop();
    }

    //Onclick region

    @OnClick(R.id.action_change)
    public void changeLanguage(View v){

        //Change Language
        Language.ELanguage tempLang = srcLang;
        srcLang = destLang ;
        destLang = tempLang ;

        chooseSourceLang.setText(Language.getLongLanguage ( srcLang ));
        chooseDestLang.setText(Language.getLongLanguage ( destLang ));

        //Set text from result text view to edit text
        String temp = textTranslate.getText ().toString ();
        textSource.setText ( temp );

        if(temp.equals ( STRING_EMPTY )) return;

        // Do translate when change language
        this.mPresenter.doTranslate();
    }

    @OnClick({R.id.action_choose_source,R.id.action_choose_dest})
    public void chooseLanguage(View view){
        selectedButtonId = view.getId();
        PopupMenu pum = new PopupMenu(this, findViewById(view.getId()));
        pum.inflate(R.menu.lang_choose_popup);
        pum.setOnMenuItemClickListener(popupMenuListener);
        pum.show();
    }

    //Override presenter methods
    @Override
    public void initPresenter() {
        new VoiceTranslatorPresenter (this, ModuleManageImpl.getInstance().getTranslateModule(), new HistoryDaoImp ( getApplicationContext () ));
    }

    @Override
    public void setPresenter(VoiceTranslatorContract.IVoiceTranslatorPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void displayResultTranslate(String result) {
        textTranslate.setText(result);
    }

    @Override
    public Language.ELanguage getSrcLang() {
        return srcLang;
    }

    @Override
    public Language.ELanguage getDestLang() {
        return destLang;
    }

    @Override
    public String getTextSrc() {
        return textSource.getText ().toString ();
    }

    //private methods
    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return getString ( R.string.voice_error_audio );
            case SpeechRecognizer.ERROR_CLIENT:
                return getString ( R.string.voice_error_client );
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return getString ( R.string.voice_error_insufficient_permissions );
            case SpeechRecognizer.ERROR_NETWORK:
                return getString ( R.string.voice_error_network );
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return getString ( R.string.voice_error_network_timeout );
            case SpeechRecognizer.ERROR_NO_MATCH:
                return getString ( R.string.voice_error_no_match );
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return getString ( R.string.voice_error_recognizer_busy );
            case SpeechRecognizer.ERROR_SERVER:
                return getString ( R.string.voice_error_server );
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return getString ( R.string.voice_error_speech_timeout );
            default:
                return getString ( R.string.voice_error_default );
        }
    }

    private void initView() {
        srcLang = Language.ELanguage.ENG;
        destLang = Language.ELanguage.JAP;

        chooseSourceLang.setText(Language.getLongLanguage ( srcLang ));
        chooseDestLang.setText(Language.getLongLanguage ( destLang ));

        startRecognizer();
        toggleButton.setOnCheckedChangeListener(toggleButtonListener);
    }

    private void startRecognizer(){
        if(speech != null) return;

        setRecognizeIntent ();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(recognitionListener);

    }

    private void setRecognizeIntent(){
        if(!intentMap.containsKey (srcLang)){
            Intent tempIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            tempIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,  Language.getLocaleString ( srcLang ));
            tempIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            tempIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            tempIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intentMap.put(srcLang , tempIntent);
        }
        recognizerIntent = intentMap.get ( srcLang );
    }

    private void stopRecognizer(){
        if(speech == null) return;
        speech.destroy();
        speech = null;
    }

    @Override
    protected void onResume() {
        super.onResume ();
        this.mPresenter.resume ();
    }

    @Override
    public void invisibleView() {
        hiddenPanel.setVisibility ( View.GONE );
    }
}