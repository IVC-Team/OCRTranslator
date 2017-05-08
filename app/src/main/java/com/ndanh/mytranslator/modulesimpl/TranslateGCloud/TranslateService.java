package com.ndanh.mytranslator.modulesimpl.TranslateGCloud;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by ndanh on 4/4/2017.
 */

public interface TranslateService {
    @FormUrlEncoded
    @POST("/language/translate/v2")
    Call<TranslatorResponse> getTranslateResult(@FieldMap Map<String, String> options);
}
