package com.ndanh.mytranslator.screen.text;

import com.ndanh.mytranslator.model.History;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.HistoryDaoImp;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.Translation;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslatorResponse;
import com.ndanh.mytranslator.services.DAO.HistoryDao;
import com.ndanh.mytranslator.services.ITranslate;

import java.util.ArrayList;
import java.util.List;

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
        List<String> srcString = new ArrayList<String>();
        srcString.add(view.getTextSrc());
        iTranslate.translate(srcString, Language.getShortLanguage ( view.getSrcLang() ),Language.getShortLanguage ( view.getDestLang() ) );
    }

    private ITranslate.OnTranslateListener translateListener = new ITranslate.OnTranslateListener() {
        @Override
        public void onSuccess(TranslatorResponse result) {
            String textTranslated = "";
            for (Translation item : result.getData().getTranslations()) {
                historyDao.addHistory ( new History (
                        Language.getLongLanguage (view.getSrcLang ()),
                        Language.getLongLanguage ( view.getDestLang ()),
                        view.getTextSrc (),
                        item.getTranslatedText())
                );
                textTranslated += item.getTranslatedText() + "\n";
            }
            view.displayResultTranslate(textTranslated);
        }

        @Override
        public void onFailed(String msg) {
            view.displayResultTranslate(msg);
        }
    };
}
