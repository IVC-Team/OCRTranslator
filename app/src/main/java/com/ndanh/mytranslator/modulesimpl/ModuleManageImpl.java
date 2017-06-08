package com.ndanh.mytranslator.modulesimpl;

import android.content.Context;

import com.ndanh.mytranslator.services.IDetector;
import com.ndanh.mytranslator.services.ITranslate;
import com.ndanh.mytranslator.services.IVoiceDetect;
import com.ndanh.mytranslator.services.ModuleManager;

/**
 * Created by ndanh on 3/31/2017.
 */

public final class ModuleManageImpl extends ModuleManager {
    private Context context;
    private ModuleManageImpl(final Context context) {
        this.context = context;
    }

    public static void init(Context context){
        manager = new ModuleManageImpl(context);
    }

    public static void release() {
        manager = null;
    }


    @Override
    public ITranslate getTranslateModule() {
        return new TranslateModuleImpl(context);
    }

    @Override
    public IVoiceDetect getVoiceDetectModule() {
        return new VoiceDetectModuleImpl(context);
    }

    @Override
    public IDetector getTextDetect() {
        return  new TextDetextModuleImpl(context);
    }
}
