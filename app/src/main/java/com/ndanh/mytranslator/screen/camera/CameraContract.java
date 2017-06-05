package com.ndanh.mytranslator.screen.camera;

import android.graphics.Bitmap;

import com.ndanh.mytranslator.base.BasePresenter;
import com.ndanh.mytranslator.base.BaseView;
import com.ndanh.mytranslator.model.Language;

import java.util.List;

/**
 * Created by ndanh on 4/18/2017.
 */

public interface CameraContract {
    interface ICameraView extends BaseView<ICameraPresenter>{
        Language.ELanguage getSrcLang();
        Language.ELanguage getDestLang();
    }
    interface ICameraPresenter extends BasePresenter{
        void doTranslate(Bitmap bitmap);
    }
}
