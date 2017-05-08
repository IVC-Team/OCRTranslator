package com.ndanh.mytranslator.screen.text;

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

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.screen.voice.VoiceTranslatorActivity;
import com.ndanh.mytranslator.ui.MyLinearLayout;

import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class TextTranslatorActivity extends AppCompatActivity implements TextTranslatorContract.ITextTranslatorView {
    @BindView(R.id.action_clear)
    ImageView clearText;
    @BindView(R.id.text_translate)
    EditText textTranslate;
    @BindView(R.id.action_translate)
    Button doTranslate;
    @BindView(R.id.text_result)
    TextView textResult;
    @BindView(R.id.action_change)
    ImageView doChange;
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
    @BindView(R.id.nav_other_pages)
    ImageView threeDot;

    private @IdRes
    int selectedButton;
    private Language srcLang, destLang;
    private AtomicBoolean inputShown = new AtomicBoolean ( false );
    private TextTranslatorContract.ITextTranslatorPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        setContentView(R.layout.activity_text_translator);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        srcLang = new Language ( Language.ELanguage.ENGLISH );
        destLang = new Language ( Language.ELanguage.JAPANESE );
        chooseSourceLang.setText(srcLang.toString ());
        chooseDestLang.setText(destLang.toString ());
        layout_parent.setOnSoftKeyboardListener ( new MyLinearLayout.OnSoftKeyboardListener () {
            @Override
            public void onShown() {
                inputShown.set ( true );
                doTextKeyboard.setVisibility ( View.GONE );
                navigator_layout.setVisibility ( View.GONE );
            }

            @Override
            public void onHidden() {
                inputShown.set ( false );
                doTextKeyboard.setVisibility ( View.VISIBLE );
                navigator_layout.setVisibility ( View.VISIBLE );
            }

            @Override
            public void onNoChanged() {
                if(inputShown.get ()){
                    doTextKeyboard.setVisibility ( View.GONE );
                    navigator_layout.setVisibility ( View.GONE );
                }else{
                    doTextKeyboard.setVisibility ( View.VISIBLE );
                    navigator_layout.setVisibility ( View.VISIBLE );
                }
            }
        } );
    }

    @OnClick(R.id.action_clear)
    public void doClearText(View view){
        textTranslate.setText("");
        textResult.setText("");
    }

    @OnTextChanged(R.id.text_translate)
    public void onTextChangeTranslate(CharSequence s, int start, int before, int count){
        if(s.toString ().length() == 0){
            clearText.setVisibility(View.GONE);
            textResult.setText ( "" );
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
        String temp = srcLang.toString();
        srcLang.changeLanguage ( destLang.toString () );
        destLang.changeLanguage ( temp );
        chooseSourceLang.setText(srcLang.toString ());
        chooseDestLang.setText(destLang.toString ());

        //Set text from result text view to edit text
        temp = textResult.getText ().toString ();
        textTranslate.setText ( temp );

        if(temp.equals ( "" )) return;

        // Do translate when change language
        this.presenter.doTranslate();
    }

    @OnClick(R.id.action_translate)
    public void translate(View view){
        String temp = textTranslate.getText ().toString ();
        // Do not translate when there is not any word.
        if("".equals ( temp )) return;
        // Do not translate when request text is same before text.
        this.presenter.doTranslate();
    }

    private PopupMenu.OnMenuItemClickListener popupMenuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Button a = (Button) findViewById( selectedButton );
            switch (selectedButton){
                case R.id.action_choose_source:
                    srcLang.changeLanguage(item.getTitle ().toString ());
                    a.setText(srcLang.toString ());
                    break;
                case R.id.action_choose_dest:
                    destLang.changeLanguage(item.getTitle ().toString ());
                    a.setText(destLang.toString ());
                    break;
                default:
                    break;
            };
            return true;
        }
    };

    private PopupMenu.OnMenuItemClickListener popupMenuNavigatorListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return true;
        }
    };

    @Override
    public void initPresenter() {
        new TextTranslatorPresenter(this, ModuleManageImpl.getInstance().getTranslateModule());
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
    public String getSrcLang() {
        return srcLang.getShortLanguage ();
    }

    @Override
    public String getDestLang() {
        return destLang.getShortLanguage ();
    }

    @Override
    public String getTextSrc() {
        return textTranslate.getText().toString();
    }

    @OnClick(R.id.action_keyboard)
    public void showSoftKeyboard(View view) {
        if (textTranslate.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService( Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(textTranslate, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @OnClick(R.id.nav_other_pages)
    public void doNavOtherPage(View view) {
        PopupMenu pum = new PopupMenu(this, findViewById(view.getId()));
        pum.inflate(R.menu.nav_menu);
        pum.setOnMenuItemClickListener(popupMenuNavigatorListener);
        pum.show();
    }

    @OnClick(R.id.nav_voice_screen)
    public void transferToVoiceScreen(View view) {
        Intent intent = new Intent ( TextTranslatorActivity.this, VoiceTranslatorActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity ( intent );
    }

    @Override
    protected void onResume() {
        super.onResume ();
        this.presenter.resume ();
    }
}


