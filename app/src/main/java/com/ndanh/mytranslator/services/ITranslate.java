package com.ndanh.mytranslator.services;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslatorResponse;

import java.util.List;

/**
 * Created by ndanh on 3/31/2017.
 */

public interface ITranslate {
    void translate(List<String> src, String srclang, String destLang);
    void setOnTranslateListener(OnTranslateListener listener);
    interface OnTranslateListener{
        void onSuccess(TranslatorResponse result);
        void onFailed(String msg);
    }
    void release();
}
