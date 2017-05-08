package com.ndanh.mytranslator.services;

/**
 * Created by ndanh on 3/31/2017.
 */

public interface IModuleManage {

    ITranslate getTranslateModule();

    IVoiceDetect getVoiceDetectModule();

    IDetector getTextDetect();
}