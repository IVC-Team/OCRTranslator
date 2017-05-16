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

    private static IVoiceDetect iVoiceDetect;
    private static ITranslate iTranslate;
    private static IDetector iDetector;


    private ModuleManageImpl(final Context context) {
        iVoiceDetect = new VoiceDetectModuleImpl(context);
        iTranslate = new TranslateModuleImpl(context);
        iDetector = new TextDetextModuleImpl(context);
    }

    public static void init(Context context){
        manager = new ModuleManageImpl(context);
    }

    public static void clean() {
        iDetector.release ();
        iTranslate.release ();
        iVoiceDetect.release ();
        manager = null;
    }

    @Override
    public ITranslate getTranslateModule() {
        return iTranslate;
    }

    @Override
    public IVoiceDetect getVoiceDetectModule() {
        return iVoiceDetect;
    }

    @Override
    public IDetector getTextDetect() { return iDetector; }
}
