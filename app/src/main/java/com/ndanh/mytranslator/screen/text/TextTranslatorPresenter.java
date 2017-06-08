package com.ndanh.mytranslator.screen.text;

import com.ndanh.mytranslator.model.History;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.HistoryDaoImp;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
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

    public TextTranslatorPresenter(TextTranslatorContract.ITextTranslatorView view){
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        view = null;
    }

    @Override
    public void resume() {
        this.iTranslate = ModuleManageImpl.getInstance().getTranslateModule();
        this.historyDao = new HistoryDaoImp ( view.getApplicationContext () );
        this.iTranslate.setOnTranslateListener ( new ITranslate.OnTranslateListener () {
            @Override
            public void onSuccess(TranslatorResponse result) {
                String textTranslated = "";
                for (Translation item : result.getData().getTranslations()) {
                    if(textTranslated != ""){
                        textTranslated += "\n";
                    }
                    historyDao.addHistory ( new History (
                            Language.getLongLanguage (view.getSrcLang ()),
                            Language.getLongLanguage ( view.getDestLang ()),
                            view.getTextSrc (),
                            item.getTranslatedText())
                    );
                    textTranslated += item.getTranslatedText() ;
                }
                view.displayResultTranslate(textTranslated);
            }

            @Override
            public void onFailed(String msg) {
                view.displayMessage ( msg );
            }
        } );
    }

    @Override
    public void pause() {
        iTranslate = null;

        historyDao = null;
        ModuleManageImpl.pause ();
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
