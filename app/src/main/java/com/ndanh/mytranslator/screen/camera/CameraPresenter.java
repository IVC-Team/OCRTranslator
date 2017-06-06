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

    public CameraPresenter(CameraContract.ICameraView view, IDetector detector, ITranslate translate){
        this.mView = view;
        this.mDetector = detector;
        this.mTranslate = translate;
        mView.setPresenter(this);
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

            }
        });
        mDetector.setDetectBitmapCallback ( new IDetector.DetectBitmapCallback () {
            @Override
            public void onSuccess(List<DetectResult> result) {
                List<String> srcString = new ArrayList<String> (  );
                for (DetectResult item : result) {
                    srcString.add ( item.toString () );
                }
                mTranslate.translate(srcString, Language.getShortLanguage ( mView.getSrcLang() ),Language.getShortLanguage ( mView.getDestLang() ) );
            }
        } );
        mDetector.setLanguage ( "eng" );
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

    }

    @Override
    public void doTranslate(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        mDetector.detectBitmap(bitmap);
    }






}
