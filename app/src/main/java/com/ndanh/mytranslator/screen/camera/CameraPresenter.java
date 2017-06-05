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
import com.ndanh.mytranslator.model.Language;
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

    private CameraContract.ICameraView mView;

    public CameraPresenter(CameraContract.ICameraView view, IDetector detector, ITranslate translate){
        this.mView = view;
        this.mDetector = detector;
        this.mTranslate = translate;
        mView.setPresenter(this);
        mTranslate.setOnTranslateListener(new ITranslate.OnTranslateListener() {
            @Override
            public void onSuccess(TranslatorResponse result) {
                List<String> lst = new ArrayList<String>();
                mView.displayResult(lst);
            }

            @Override
            public void onFailed(String msg) {

            }
        });
        mDetector.setDetectBitmapCallback(new IDetector.DetectBitmapCallback() {
            @Override
                public void onSuccess(Map<Rect,String> result) {
//                    mTranslate.translate(result,
//                            Language.getShortLanguage ( mView.getSrcLang() ),
//                            Language.getShortLanguage ( mView.getDestLang() ) );
                }
        });
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
        mDetector.detectBitmap(bitmap);
    }






}
