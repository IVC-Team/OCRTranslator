package com.ndanh.mytranslator.screen.camera;

import android.graphics.Bitmap;

import com.ndanh.mytranslator.base.BasePresenter;
import com.ndanh.mytranslator.base.BaseView;
import com.ndanh.mytranslator.model.Frame;

/**
 * Created by ndanh on 4/18/2017.
 */

public interface CameraContract {
    interface ICameraView extends BaseView<ICameraPresenter>{
        void setResultTranslate(Bitmap bitmap);
        void showMessage(String text);
    }
    interface ICameraPresenter extends BasePresenter{
        void getPreviewFrame(Frame frame);
    }
}
