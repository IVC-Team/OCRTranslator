package com.ndanh.mytranslator.screen.voice;

import com.ndanh.mytranslator.model.History;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.Translation;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslatorResponse;
import com.ndanh.mytranslator.services.DAO.HistoryDao;
import com.ndanh.mytranslator.services.ITranslate;

import java.util.ArrayList;
import java.util.List;

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
        List<String> src = new ArrayList<String>();
        src.add( mView.getTextSrc ());
        mTranslate.translate( src, Language.getShortLanguage ( mView.getSrcLang() ),Language.getShortLanguage ( mView.getDestLang() ) );
    }

    private ITranslate.OnTranslateListener translateListener = new ITranslate.OnTranslateListener() {
        @Override
        public void onSuccess(TranslatorResponse result) {
            String textTranslated = "";
            for (Translation item : result.getData().getTranslations()) {
                historyDao.addHistory ( new History (
                        Language.getLongLanguage (mView.getSrcLang ()),
                        Language.getLongLanguage ( mView.getDestLang ()),
                        mView.getTextSrc (),
                        item.getTranslatedText())
                );
                textTranslated += item.getTranslatedText() + "\n";
            }
            mView.displayResultTranslate(textTranslated);
        }

        @Override
        public void onFailed(String msg) {
            mView.displayResultTranslate(msg);
        }
    };
}
