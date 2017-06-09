package com.ndanh.mytranslator.screen.text;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.base.NavigatorFooterActivity;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.HistoryDaoImp;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.screen.about.AboutActivity;
import com.ndanh.mytranslator.screen.help.HelpActivity;
import com.ndanh.mytranslator.screen.history.HistoryActivity;
import com.ndanh.mytranslator.screen.settings.SettingActivity;
import com.ndanh.mytranslator.screen.voice.VoiceTranslatorActivity;
import com.ndanh.mytranslator.ui.MyLinearLayout;
import com.ndanh.mytranslator.util.PermissionHelper;

import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class TextTranslatorActivity extends NavigatorFooterActivity implements TextTranslatorContract.ITextTranslatorView {
    @BindView(R.id.action_clear)
    ImageView clearText;
    @BindView(R.id.text_translate)
    EditText textTranslate;
    @BindView(R.id.text_result)
    TextView textResult;
    @BindView(R.id.action_keyboard)
    RelativeLayout doTextKeyboard;
    @BindView(R.id.action_choose_source)
    Button chooseSourceLang;
    @BindView(R.id.action_choose_dest)
    Button chooseDestLang;
    @BindView ( R.id.activity_text_translator )
    MyLinearLayout layout_parent;
    @BindView ( R.id.navigator_action_bar )
    LinearLayout navigator_layout;
    @BindView ( R.id.navigation_footer_text )
    RelativeLayout hiddenPanel;

    private @IdRes
    int selectedButton;
    private Language.ELanguage srcLang, destLang;
    private TextTranslatorContract.ITextTranslatorPresenter presenter;
    private static final String STRING_EMPTY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        setContentView(R.layout.activity_text_translator);
        ButterKnife.bind(this);
        PermissionHelper.requestPermission ( this, Manifest.permission.WRITE_EXTERNAL_STORAGE );
        PermissionHelper.requestPermission ( this, Manifest.permission.INTERNET );
        initView();
    }

    public void initView() {
        srcLang = Language.ELanguage.ENG;
        destLang = Language.ELanguage.JAP;

        chooseSourceLang.setText(Language.getLongLanguage ( srcLang ));
        chooseDestLang.setText(Language.getLongLanguage ( destLang ));
        layout_parent.setOnSoftKeyboardListener ( new MyLinearLayout.OnSoftKeyboardListener () {
            @Override
            public void onShown() {
                doTextKeyboard.setVisibility ( View.GONE );
                navigator_layout.setVisibility ( View.GONE );
            }

            @Override
            public void onHidden() {
                doTextKeyboard.setVisibility ( View.VISIBLE );
                navigator_layout.setVisibility ( View.VISIBLE );
            }
        } );
    }

    @OnClick(R.id.action_keyboard)
    public void showSoftKeyboard(View view) {
        if (textTranslate.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textTranslate, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @OnClick(R.id.action_clear)
    public void doClearText(View view){
        textTranslate.setText(STRING_EMPTY);
        textResult.setText(STRING_EMPTY);
    }

    @OnTextChanged(R.id.text_translate)
    public void onTextChangeTranslate(CharSequence s, int start, int before, int count){
        if(s.toString ().length() == 0){
            clearText.setVisibility(View.GONE);
            textResult.setText ( STRING_EMPTY );
        } else {
            clearText.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.action_choose_source,R.id.action_choose_dest})
    public void chooseLanguage(View view){
        selectedButton = view.getId();
        PopupMenu pum = new PopupMenu(this, findViewById(view.getId()));
        pum.inflate(R.menu.lang_choose_popup);
        pum.setOnMenuItemClickListener(popupMenuListener);
        pum.show();
    }

    @OnClick(R.id.action_change)
    public void changeLanguage(View v){
        //Change Language
        Language.ELanguage tempLang = srcLang;
        srcLang = destLang ;
        destLang = tempLang ;

        chooseSourceLang.setText(Language.getLongLanguage ( srcLang ));
        chooseDestLang.setText(Language.getLongLanguage ( destLang ));

        //Set text from result text view to edit text
        String temp = textResult.getText ().toString ();
        textTranslate.setText ( temp );

        if(temp.equals ( STRING_EMPTY )) return;

        // Do translate when change language
        this.presenter.doTranslate();
    }

    @OnClick(R.id.action_translate)
    public void translate(View view){
        String temp = textTranslate.getText ().toString ();
        // Do not translate when there is not any word.
        if(STRING_EMPTY.equals ( temp )) return;
        // Do not translate when request text is same before text.
        this.presenter.doTranslate();
    }

    private PopupMenu.OnMenuItemClickListener popupMenuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Button a = (Button) findViewById( selectedButton );
            switch (selectedButton){
                case R.id.action_choose_source:
                    srcLang = Language.setLangFromMenu(item.getItemId ());
                    a.setText(Language.getLongLanguage ( srcLang ));
                    break;
                case R.id.action_choose_dest:
                    destLang = Language.setLangFromMenu(item.getItemId ());
                    a.setText(Language.getLongLanguage ( destLang ));
                    break;
                default:
                    break;
            };
            return true;
        }
    };

    @Override
    public void initPresenter() {
        new TextTranslatorPresenter(this);
    }

    @Override
    public void setPresenter(TextTranslatorContract.ITextTranslatorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.presenter.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.presenter.start();
    }

    @Override
    public void displayResultTranslate(String result) {
        textResult.setText(result);
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
        return textTranslate.getText().toString();
    }

    @Override
    public void displayMessage(String msg) {
        Toast.makeText ( getApplicationContext (), msg , Toast.LENGTH_SHORT ).show ();
    }

    @Override
    protected void onResume() {
        super.onResume ();
        this.presenter.resume ();
    }

    @Override
    protected void onPause() {
        super.onPause ();
        this.presenter.pause ();
    }

    @Override
    public void invisibleView() {
        hiddenPanel.setVisibility ( View.GONE );
    }
}


