package com.ndanh.mytranslator.screen.voice;

import com.ndanh.mytranslator.services.ITranslate;

/**
 * Created by ndanh on 4/28/2017.
 */

public class VoiceTranslatorPresenter implements VoiceTranslatorContract.IVoiceTranslatorPresenter {

    private VoiceTranslatorContract.IVoiceTranslatorView mView;
    private ITranslate mTranslate;

    public VoiceTranslatorPresenter (VoiceTranslatorContract.IVoiceTranslatorView view, ITranslate translate){
        this.mView = view;
        mView.setPresenter ( this );
        this.mTranslate = translate;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        mView = null;
        mTranslate = null;
        translateListener = null;
    }

    @Override
    public void resume() {
        mTranslate.setOnTranslateListener(translateListener);
    }

    @Override
    public void doTranslate(String text) {
        mTranslate.translate(text, mView.getSrcLang(), mView.getDestLang());
    }

    private ITranslate.OnTranslateListener translateListener = new ITranslate.OnTranslateListener() {
        @Override
        public void onSuccess(String result) {
            mView.displayResultTranslate(result);
        }

        @Override
        public void onFailed(String msg) {
            mView.displayResultTranslate(msg);
        }
    };
}
