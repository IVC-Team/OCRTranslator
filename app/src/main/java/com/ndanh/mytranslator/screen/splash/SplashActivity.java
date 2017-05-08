package com.ndanh.mytranslator.screen.splash;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ndanh.mytranslator.App;
import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.screen.text.TextTranslatorActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new AsyncLoadXMLFeed().execute();
    }

    private class AsyncLoadXMLFeed extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute(){
            // show your progress dialog

        }

        @Override
        protected Void doInBackground(Void... voids){
            // load your xml feed asynchronously
            try {
                Thread.sleep ( 5000 );
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params){
            Intent intent = new Intent(SplashActivity.this, TextTranslatorActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

}

