package com.ndanh.mytranslator.screen.voice;

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
 * Created by ndanh on 4/28/2017.
 */

public class VoiceTranslatorPresenter implements VoiceTranslatorContract.IVoiceTranslatorPresenter {

    private HistoryDao historyDao;
    private VoiceTranslatorContract.IVoiceTranslatorView mView;
    private ITranslate mTranslate;

    public VoiceTranslatorPresenter (VoiceTranslatorContract.IVoiceTranslatorView view){
        this.mView = view;
        mView.setPresenter ( this );
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        mView = null;
    }

    @Override
    public void resume() {
        this.mTranslate = ModuleManageImpl.getInstance().getTranslateModule();
        this.historyDao = new HistoryDaoImp ( mView.getApplicationContext () );
        this.mTranslate.setOnTranslateListener ( new ITranslate.OnTranslateListener () {
            @Override
            public void onSuccess(TranslatorResponse result) {
                String textTranslated = "";
                for (Translation item : result.getData().getTranslations()) {
                    if(textTranslated != "")
                        textTranslated += "\n";
                    historyDao.addHistory ( new History (
                            Language.getLongLanguage (mView.getSrcLang ()),
                            Language.getLongLanguage ( mView.getDestLang ()),
                            mView.getTextSrc (),
                            item.getTranslatedText())
                    );
                    textTranslated += item.getTranslatedText();
                }
                mView.displayResultTranslate(textTranslated);
            }

            @Override
            public void onFailed(String msg) {
                mView.displayMessage ( msg );
            }
        } );
    }

    @Override
    public void pause() {
        mTranslate = null;
        historyDao = null;
        ModuleManageImpl.pause ();
    }

    @Override
    public void doTranslate() {
        List<String> src = new ArrayList<String>();
        src.add( mView.getTextSrc ());
        mTranslate.translate( src, Language.getShortLanguage ( mView.getSrcLang() ),Language.getShortLanguage ( mView.getDestLang() ) );
    }
}
