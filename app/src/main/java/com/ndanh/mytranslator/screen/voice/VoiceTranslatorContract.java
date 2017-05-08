package com.ndanh.mytranslator.screen.voice;

import com.ndanh.mytranslator.base.BasePresenter;
import com.ndanh.mytranslator.base.BaseView;

/**
 * Created by ndanh on 4/28/2017.
 */

public interface VoiceTranslatorContract {
    interface IVoiceTranslatorView extends BaseView<IVoiceTranslatorPresenter> {
        void displayResultTranslate(String result);
        String getSrcLang();
        String getDestLang();
    }
    interface IVoiceTranslatorPresenter extends BasePresenter {
        void doTranslate(String text);
    }
}
