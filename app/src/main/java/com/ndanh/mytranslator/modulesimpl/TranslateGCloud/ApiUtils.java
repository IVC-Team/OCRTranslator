package com.ndanh.mytranslator.modulesimpl.TranslateGCloud;

import com.ndanh.mytranslator.util.Config;

/**
 * Created by ndanh on 3/28/2017.
 */

public class ApiUtils {
    public static final String GOOGLE_TRANSLATE_API_URL =  Config.GOOGLE_TRANSLATE_API_URL;


    public static TranslateService getTestService() {
        return RetrofitClient.getClient(GOOGLE_TRANSLATE_API_URL).create(TranslateService.class);
    }

}
