package com.ndanh.mytranslator.services;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.ndanh.mytranslator.model.DetectResult;

import java.util.List;
import java.util.Map;

/**
 * Created by ndanh on 4/18/2017.
 */

public interface IDetector {
    void release();
    void detectBitmap(Bitmap bitmap);
    void setLanguage(String lang);
    void setDetectBitmapCallback(DetectBitmapCallback callback);
    interface DetectBitmapCallback{
        void onSuccess(List<DetectResult> result);
    }
}
