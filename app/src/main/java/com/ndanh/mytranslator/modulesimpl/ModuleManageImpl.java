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
    private Context context;
    private ModuleManageImpl(final Context context) {
        this.context = context;
    }

    public static void init(Context context){
        manager = new ModuleManageImpl(context);
    }

    public static void clean() {
        if(iDetector != null) iDetector.release ();
        if(iTranslate != null)iTranslate.release ();
        if(iVoiceDetect != null) iVoiceDetect.release ();
        manager = null;
    }

    public static void pause() {
        iDetector = null;
        iTranslate = null;
        iVoiceDetect = null;
    }

    @Override
    public ITranslate getTranslateModule() {
        if(iTranslate == null){
            iTranslate = new TranslateModuleImpl(context);
        }
        return iTranslate;
    }

    @Override
    public IVoiceDetect getVoiceDetectModule() {
        if(iVoiceDetect == null){
            iVoiceDetect = new VoiceDetectModuleImpl(context);
        }
        return iVoiceDetect;
    }

    @Override
    public IDetector getTextDetect() {
        if (iDetector == null){
            iDetector = new TextDetextModuleImpl(context);
        }
        return iDetector;
    }
}
