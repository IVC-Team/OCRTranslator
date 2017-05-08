package com.ndanh.mytranslator.services;

import com.ndanh.mytranslator.model.Frame;
import com.ndanh.mytranslator.modulesimpl.TextDetextModuleImpl;

import java.nio.ByteBuffer;

/**
 * Created by ndanh on 4/18/2017.
 */

public interface IDetector {
    void release();
    void receiveFrame(Frame frame);
    void setDetectListener(OnDetectListener listener);
    interface OnDetectListener{
        void onSuccess(TextDetextModuleImpl.Detection detection);
        void onFailed(String msg);
    }
}
