package com.ndanh.mytranslator.screen.help;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends BaseActivity {
    @BindView( R.id.webview )
    WebView webView;

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_help );
        ButterKnife.bind ( this );
        webView.loadUrl ( "file:///android_asset/html/help.html" );
    }

    @OnClick(R.id.action_back)
    public void back(View v){
        finish ();
    }
}
