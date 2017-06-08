package com.ndanh.mytranslator.screen.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ndanh.mytranslator.App;
import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.base.BaseActivity;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.model.Setting;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.screen.camera.CameraActivity;
import com.ndanh.mytranslator.screen.history.HistoryActivity;
import com.ndanh.mytranslator.screen.settings.SettingActivity;
import com.ndanh.mytranslator.screen.text.TextTranslatorActivity;
import com.ndanh.mytranslator.screen.voice.VoiceTranslatorActivity;


public class SplashActivity extends BaseActivity {

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new AsyncLoadXMLFeed().execute();
    }

    private class AsyncLoadXMLFeed extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... voids){
            return null;
        }

        @Override
        protected void onPostExecute(Void params){
            int screenMode = Setting.getScreenMode ();
            Intent intent;
            switch (screenMode){
                case Setting.CAMERA_SCREEN_MODE:
                    intent = new Intent(SplashActivity.this, CameraActivity.class);
                    break;
                case Setting.TEXT_SCREEN_MODE:
                    intent = new Intent(SplashActivity.this, TextTranslatorActivity.class);
                    break;
                case Setting.VOICE_SCREEN_MODE:
                    intent = new Intent(SplashActivity.this, VoiceTranslatorActivity.class);
                    break;
                default:
                    intent = new Intent(SplashActivity.this, CameraActivity.class);
                    break;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

}

