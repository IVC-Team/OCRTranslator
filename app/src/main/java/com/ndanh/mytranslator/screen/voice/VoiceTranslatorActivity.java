package com.ndanh.mytranslator.screen.voice;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.Manifest;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.screen.text.TextTranslatorActivity;
import com.ndanh.mytranslator.util.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceTranslatorActivity extends Activity implements VoiceTranslatorContract.IVoiceTranslatorView {

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

    //private properties
    private @IdRes
    int selectedButtonId;
    private Language srcLang, destLang;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceTranslatorActivity";
    private Map<String, Intent> intentMap = new HashMap<String, Intent>();
    private VoiceTranslatorContract.IVoiceTranslatorPresenter mPresenter;

    //Listeners
    private PopupMenu.OnMenuItemClickListener popupMenuNavigatorListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return true;
        }
    };

    private RecognitionListener recognitionListener = new RecognitionListener () {
        @Override
        public void onBeginningOfSpeech() {
            Log.i(LOG_TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.i(LOG_TAG, "onBufferReceived: " + buffer);
        }

        @Override
        public void onEndOfSpeech() {
            Log.i(LOG_TAG, "onEndOfSpeech");
            toggleButton.setChecked(false);
        }

        @Override
        public void onError(int errorCode) {
            String errorMessage = getErrorText(errorCode);
            Log.d(LOG_TAG, "FAILED " + errorMessage);
            textSource.setText(errorMessage);
            textTranslate.setText("");
            toggleButton.setChecked(false);
            stopRecognizer();
        }

        @Override
        public void onEvent(int arg0, Bundle arg1) {
            Log.i(LOG_TAG, "onEvent");
        }

        @Override
        public void onPartialResults(Bundle arg0) {
            Log.i(LOG_TAG, "onPartialResults");
        }

        @Override
        public void onReadyForSpeech(Bundle arg0) {
            Log.i(LOG_TAG, "onReadyForSpeech");
        }

        @Override
        public void onResults(Bundle results) {
            Log.i(LOG_TAG, "onResults");
            ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = matches.size () > 0 ? matches.get(0) : "";
            textSource.setText(text);
            if(srcLang == destLang)
                textTranslate.setText(text);
            else
                mPresenter.doTranslate (text);
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        }

    };

    private PopupMenu.OnMenuItemClickListener popupMenuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Button tempButton = (Button) findViewById( selectedButtonId );
            switch (selectedButtonId){
                case R.id.action_choose_source:
                    srcLang.changeLanguage(item.getTitle ().toString ());
                    setRecognizeIntent ();
                    tempButton.setText(srcLang.toString ());
                    break;
                case R.id.action_choose_dest:
                    destLang.changeLanguage(item.getTitle ().toString ());
                    tempButton.setText(destLang.toString ());
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
    @OnClick(R.id.nav_text_screen)
    public void transferToTextScreen(View view) {
        Intent intent = new Intent ( VoiceTranslatorActivity.this, TextTranslatorActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity ( intent );
    }

    @OnClick(R.id.nav_other_pages)
    public void doNavOtherPage(View view) {
        PopupMenu pum = new PopupMenu(this, findViewById(view.getId()));
        pum.inflate(R.menu.nav_menu);
        pum.setOnMenuItemClickListener(popupMenuNavigatorListener);
        pum.show();
    }

    @OnClick(R.id.action_change)
    public void changeLanguage(View v){

        //Change Language
        String temp = srcLang.toString();
        srcLang.changeLanguage ( destLang.toString () );
        destLang.changeLanguage ( temp );
        chooseSourceLang.setText(srcLang.toString ());
        chooseDestLang.setText(destLang.toString ());

        //Set text from result text view to edit text
        temp = textTranslate.getText ().toString ();
        textSource.setText ( temp );

        if(temp.equals ( "" )) return;

        // Do translate when change language
        this.mPresenter.doTranslate(temp);
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
        new VoiceTranslatorPresenter (this, ModuleManageImpl.getInstance().getTranslateModule());
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
    public String getSrcLang() {
        return srcLang.getShortLanguage ();
    }

    @Override
    public String getDestLang() {
        return destLang.getShortLanguage ();
    }

    //private methods
    private static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private void initView() {
        srcLang = new Language ( Language.ELanguage.ENGLISH );
        destLang = new Language ( Language.ELanguage.JAPANESE );
        chooseSourceLang.setText(srcLang.toString ());
        chooseDestLang.setText(destLang.toString ());
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
        if(!intentMap.containsKey (srcLang.getLocaleString ())){
            Intent tempIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            tempIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,  srcLang.getLocaleString ());
            tempIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
            tempIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            tempIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            intentMap.put(srcLang.getLocaleString (), tempIntent);
        }
        recognizerIntent = intentMap.get ( srcLang.getLocaleString () );
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
}