package com.ndanh.mytranslator.screen.text;

import com.ndanh.mytranslator.services.ITranslate;

/**
 * Created by ndanh on 3/30/2017.
 */

public class TextTranslatorPresenter implements TextTranslatorContract.ITextTranslatorPresenter {

    private TextTranslatorContract.ITextTranslatorView view;
    private ITranslate iTranslate;

    public TextTranslatorPresenter(TextTranslatorContract.ITextTranslatorView view, ITranslate iTranslate){
        this.view = view;
        view.setPresenter(this);
        this.iTranslate = iTranslate;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        view = null;
        iTranslate = null;
        translateListener = null;
    }

    @Override
    public void resume() {
        iTranslate.setOnTranslateListener(translateListener);
    }

    @Override
    public void doTranslate() {
        iTranslate.translate(view.getTextSrc(), view.getSrcLang(), view.getDestLang());
    }

    private ITranslate.OnTranslateListener translateListener = new ITranslate.OnTranslateListener() {
        @Override
        public void onSuccess(String result) {
            view.displayResultTranslate(result);
        }

        @Override
        public void onFailed(String msg) {
            view.displayResultTranslate(msg);
        }
    };
}
