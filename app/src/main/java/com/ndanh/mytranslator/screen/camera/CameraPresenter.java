package com.ndanh.mytranslator.screen.camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.opengl.GLUtils;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.model.DetectResult;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.Translation;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslatorResponse;
import com.ndanh.mytranslator.services.IDetector;
import com.ndanh.mytranslator.services.ITranslate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ndanh on 4/18/2017.
 */

public class CameraPresenter implements CameraContract.ICameraPresenter {

    private IDetector mDetector;
    private ITranslate mTranslate;
    private static final String TAG = "CameraPresenter";
    private int width, height;
    private CameraContract.ICameraView mView;
    private List<DetectResult> detectResults;
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
                mView.showMessage ( "CameraPresenter ITranslate onSuccess" );
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
                detectResults = result;
                mView.showMessage ( "CameraPresenter DetectBitmapCallback onSuccess" );
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
        ModuleManageImpl.pause ();
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
        if(detectResults != null) {
            List<String> srcString = new ArrayList<String> (  );
            for (DetectResult item : detectResults) {
                srcString.add ( item.toString () );
            }
            mTranslate.translate(srcString, Language.getShortLanguage ( mView.getSrcLang() ),Language.getShortLanguage ( mView.getDestLang() ) );
        }

    }
}
