package com.ndanh.mytranslator.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ndanh.mytranslator.util.CToast;

/**
 * Created by ndanh on 5/15/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        CToast.init(getApplicationContext ());
    }
}
