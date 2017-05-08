package com.ndanh.mytranslator.screen.text;

import com.ndanh.mytranslator.base.BasePresenter;
import com.ndanh.mytranslator.base.BaseView;

/**
 * Created by ndanh on 3/30/2017.
 */

public interface TextTranslatorContract {
    interface ITextTranslatorView extends BaseView<ITextTranslatorPresenter> {
        void displayResultTranslate(String result);
        String getSrcLang();
        String getDestLang();
        String getTextSrc();
    }
    interface ITextTranslatorPresenter extends BasePresenter{
        void doTranslate();
    }
}
