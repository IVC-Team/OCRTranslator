package com.ndanh.mytranslator;

import android.app.Application;

import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.model.Setting;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.screen.history.DeleteMode;

/**
 * Created by ndanh on 3/31/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ModuleManageImpl.init(this);
        Language.init ( this );
        Setting.initSetting ( this );
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ModuleManageImpl.clean();
        Language.clean ( );
        DeleteMode.clean();
        Setting.clean();
    }
}
