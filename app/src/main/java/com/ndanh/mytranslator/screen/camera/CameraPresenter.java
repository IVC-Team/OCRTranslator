package com.ndanh.mytranslator.screen.camera;

import com.ndanh.mytranslator.services.IDetector;
import com.ndanh.mytranslator.services.ITranslate;

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

}
