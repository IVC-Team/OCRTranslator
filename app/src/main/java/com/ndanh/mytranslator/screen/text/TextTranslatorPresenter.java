package com.ndanh.mytranslator.screen.text;

import com.ndanh.mytranslator.model.History;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.HistoryDaoImp;
import com.ndanh.mytranslator.services.DAO.HistoryDao;
import com.ndanh.mytranslator.services.ITranslate;

/**
 * Created by ndanh on 3/30/2017.
 */

public class TextTranslatorPresenter implements TextTranslatorContract.ITextTranslatorPresenter {

    private HistoryDao historyDao;
    private TextTranslatorContract.ITextTranslatorView view;
    private ITranslate iTranslate;

    public TextTranslatorPresenter(TextTranslatorContract.ITextTranslatorView view, ITranslate iTranslate, HistoryDao historyDao){
        this.view = view;
        view.setPresenter(this);
        this.iTranslate = iTranslate;
        this.historyDao = historyDao;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        view = null;
        iTranslate = null;
        translateListener = null;
        historyDao = null;
    }

    @Override
    public void resume() {
        iTranslate.setOnTranslateListener(translateListener);
    }

    @Override
    public void doTranslate() {
        iTranslate.translate(view.getTextSrc(), Language.getShortLanguage ( view.getSrcLang() ),Language.getShortLanguage ( view.getDestLang() ) );
    }

    private ITranslate.OnTranslateListener translateListener = new ITranslate.OnTranslateListener() {
        @Override
        public void onSuccess(String result) {
            historyDao.addHistory ( new History ( Language.getLongLanguage (view.getSrcLang ()), Language.getLongLanguage ( view.getDestLang ()), view.getTextSrc (), result) );
            view.displayResultTranslate(result);
        }

        @Override
        public void onFailed(String msg) {
            view.displayResultTranslate(msg);
        }
    };
}
