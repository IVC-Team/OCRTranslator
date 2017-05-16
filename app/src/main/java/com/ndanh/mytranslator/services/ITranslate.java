package com.ndanh.mytranslator.services;

import com.ndanh.mytranslator.modulesimpl.TextDetextModuleImpl;

/**
 * Created by ndanh on 3/31/2017.
 */

public interface ITranslate {
    void translate(String src, String srclang, String destLang);
    void setOnTranslateListener(OnTranslateListener listener);
    interface OnTranslateListener{
        void onSuccess(String result);
        void onFailed(String msg);
    }
    void release();
}
