package com.ndanh.mytranslator;

import android.app.Application;

import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;

/**
 * Created by ndanh on 3/31/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ModuleManageImpl.init(App.this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ModuleManageImpl.clean();
    }
}
