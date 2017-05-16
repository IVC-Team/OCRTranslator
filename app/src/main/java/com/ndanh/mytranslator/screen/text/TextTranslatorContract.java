package com.ndanh.mytranslator.screen.text;

import com.ndanh.mytranslator.base.BasePresenter;
import com.ndanh.mytranslator.base.BaseView;
import com.ndanh.mytranslator.model.Language;

/**
 * Created by ndanh on 3/30/2017.
 */

public interface TextTranslatorContract {
    interface ITextTranslatorView extends BaseView<ITextTranslatorPresenter> {
        void displayResultTranslate(String result);
        Language.ELanguage getSrcLang();
        Language.ELanguage getDestLang();
        String getTextSrc();
    }
    interface ITextTranslatorPresenter extends BasePresenter{
        void doTranslate();
    }
}
