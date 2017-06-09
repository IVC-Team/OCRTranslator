package com.ndanh.mytranslator.screen.camera;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.ndanh.mytranslator.model.DetectResult;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.Translation;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslatorResponse;
import com.ndanh.mytranslator.services.IDetector;
import com.ndanh.mytranslator.services.ITranslate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndanh on 4/18/2017.
 */

public class CameraPresenter implements CameraContract.ICameraPresenter {

    private IDetector mDetector;
    private ITranslate mTranslate;
    private static final String TAG = "CameraPresenter";
    private int width, height;
    private CameraContract.ICameraView mView;
    private List<DetectResult> detectResultCache;
    public CameraPresenter(CameraContract.ICameraView view){
        this.mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        this.mView = null;
    }

    @Override
    public void resume() {
        this.mDetector =  ModuleManageImpl.getInstance().getTextDetect();
        this.mTranslate =  ModuleManageImpl.getInstance().getTranslateModule();
        mTranslate.setOnTranslateListener(new ITranslate.OnTranslateListener() {
            @Override
            public void onSuccess(TranslatorResponse result) {
                List<DetectResult> detectResults = new ArrayList<DetectResult> (  );
                for (Translation item : result.getData ().getTranslations ()) {
                    detectResults.add ( DetectResult.parseDetectResult ( item.getTranslatedText () ) );
                }
                mView.displayResult(detectResults, width, height);
            }

            @Override
            public void onFailed(String msg) {
                mView.showMessage ( msg );
            }
        });
        mDetector.setDetectBitmapCallback ( new IDetector.DetectBitmapCallback () {
            @Override
            public void onSuccess(List<DetectResult> result) {
                detectResultCache = result;
                List<String> srcString = new ArrayList<String> (  );
                for (DetectResult item : result) {
                    srcString.add ( item.toString () );
                }
                mTranslate.translate(srcString, Language.getShortLanguage ( mView.getSrcLang() ),Language.getShortLanguage ( mView.getDestLang() ) );
            }
        } );
    }

    @Override
    public void pause() {
        this.mDetector = null;
        this.mTranslate = null;
    }

    @Override
    public void doTranslate(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        mDetector.detectBitmap(bitmap);
    }

    @Override
    public void changeSrcLanguage() {
        mDetector.setLanguage ( Language.getShortLanguage ( mView.getSrcLang() ) );
    }

    @Override
    public void changeDestLanguage() {
        if(detectResultCache != null) {
            List<String> srcString = new ArrayList<String> (  );
            for (DetectResult item : detectResultCache) {
                srcString.add ( item.toString () );
            }
            mTranslate.translate(srcString, Language.getShortLanguage ( mView.getSrcLang() ),Language.getShortLanguage ( mView.getDestLang() ) );
        }

    }
}
