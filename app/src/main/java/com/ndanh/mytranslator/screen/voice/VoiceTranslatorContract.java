package com.ndanh.mytranslator.screen.voice;

import android.content.Context;

import com.ndanh.mytranslator.base.BasePresenter;
import com.ndanh.mytranslator.base.BaseView;
import com.ndanh.mytranslator.model.Language;

/**
 * Created by ndanh on 4/28/2017.
 */

public interface VoiceTranslatorContract {
    interface IVoiceTranslatorView extends BaseView<IVoiceTranslatorPresenter> {
        Context getApplicationContext();
        void displayResultTranslate(String result);
        Language.ELanguage getSrcLang();
        Language.ELanguage getDestLang();
        String getTextSrc();
        void displayMessage(String msg);
    }
    interface IVoiceTranslatorPresenter extends BasePresenter {
        void doTranslate();
    }
}
