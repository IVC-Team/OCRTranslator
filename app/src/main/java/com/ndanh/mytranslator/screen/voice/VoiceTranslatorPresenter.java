package com.ndanh.mytranslator.screen.voice;

import com.ndanh.mytranslator.model.History;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.services.DAO.HistoryDao;
import com.ndanh.mytranslator.services.ITranslate;

/**
 * Created by ndanh on 4/28/2017.
 */

public class VoiceTranslatorPresenter implements VoiceTranslatorContract.IVoiceTranslatorPresenter {

    private HistoryDao historyDao;
    private VoiceTranslatorContract.IVoiceTranslatorView mView;
    private ITranslate mTranslate;

    public VoiceTranslatorPresenter (VoiceTranslatorContract.IVoiceTranslatorView view, ITranslate translate, HistoryDao historyDao){
        this.mView = view;
        mView.setPresenter ( this );
        this.mTranslate = translate;
        this.historyDao = historyDao;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        mView = null;
        mTranslate = null;
        translateListener = null;
        historyDao = null;
    }

    @Override
    public void resume() {
        mTranslate.setOnTranslateListener(translateListener);
    }

    @Override
    public void doTranslate() {
        mTranslate.translate(  mView.getTextSrc (), Language.getShortLanguage ( mView.getSrcLang() ),Language.getShortLanguage ( mView.getDestLang() ) );
    }

    private ITranslate.OnTranslateListener translateListener = new ITranslate.OnTranslateListener() {
        @Override
        public void onSuccess(String result) {
            historyDao.addHistory ( new History ( Language.getLongLanguage (mView.getSrcLang ()), Language.getLongLanguage ( mView.getDestLang ()), mView.getTextSrc (), result) );
            mView.displayResultTranslate(result);
        }

        @Override
        public void onFailed(String msg) {
            mView.displayResultTranslate(msg);
        }
    };
}
