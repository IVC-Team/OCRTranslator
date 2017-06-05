package com.ndanh.mytranslator.services;

import android.graphics.Bitmap;

/**
 * Created by ndanh on 4/18/2017.
 */

public interface IDetector {
    void release();
    void detectBitmap(Bitmap bitmap);
    void setLanguage(String lang);

}
