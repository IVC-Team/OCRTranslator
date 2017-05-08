package com.ndanh.mytranslator.screen.camera;

import com.ndanh.mytranslator.model.Detection;
import com.ndanh.mytranslator.model.Frame;
import com.ndanh.mytranslator.modulesimpl.TextDetextModuleImpl;
import com.ndanh.mytranslator.services.IDetector;
import com.ndanh.mytranslator.services.ITranslate;

/**
 * Created by ndanh on 4/18/2017.
 */

public class CameraPresenter implements CameraContract.ICameraPresenter {

    private IDetector mDetector;
    private ITranslate mTranslate;
    private static final String TAG = "CameraPresenter";
    private IDetector.OnDetectListener detectListener = new IDetector.OnDetectListener() {
        @Override
        public void onSuccess(TextDetextModuleImpl.Detection detection) {
            mTranslate.translate(detection);//TODO: implement translate
        }

        @Override
        public void onFailed(String msg) {

        }
    };

    private ITranslate.OnTranslateListener translateListener = new ITranslate.OnTranslateListener() {
        @Override
        public void onSuccess(String result) {

        }

        @Override
        public void onFailed(String msg) {

        }
    };

    private CameraContract.ICameraView mView;

    public CameraPresenter(CameraContract.ICameraView view, IDetector IDetector, ITranslate translate){
        this.mView = view;
        this.mDetector = IDetector;
        this.mTranslate = translate;
        mView.setPresenter(this);
        mDetector.setDetectListener(detectListener);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void getPreviewFrame(Frame frame) {
        mDetector.receiveFrame(frame);
    }
}
