package com.ndanh.mytranslator.modulesimpl;

import android.content.Context;

import com.ndanh.mytranslator.BuildConfig;
import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.ApiUtils;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.RetrofitClient;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslateService;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslatorResponse;
import com.ndanh.mytranslator.services.ITranslate;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ndanh on 3/31/2017.
 */

public final class TranslateModuleImpl implements ITranslate {
    private OnTranslateListener listener;
    private Context mContext;
    private TranslateService translateService;
    public TranslateModuleImpl(Context context) {
        this.mContext = context;
        translateService = ApiUtils.getTestService();
    }
    private TranslateModuleImpl() {

    }

    @Override
    public void translate(String src, String srclang, String destLang) {
        //TODO: implement translate
        Map<String, String> data = new HashMap<>();
        data.put("q", src);
        data.put("source", srclang);
        data.put("target", destLang);
        data.put("format", "text");
        data.put("key", BuildConfig.TRANSLATE_GCLOUD_API_KEY);
        translateService.getTranslateResult(data).enqueue(new Callback<TranslatorResponse>() {
            @Override
            public void onResponse(Call<TranslatorResponse> call, Response<TranslatorResponse> response) {
                if(response.isSuccessful()) {
                    listener.onSuccess(response.body().getData().getTranslations().get(0).getTranslatedText());
                }else {
                    listener.onFailed(String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<TranslatorResponse> call, Throwable t) {
                listener.onFailed(String.valueOf(t.getMessage()));
            }
        });

    }

    @Override
    public void translate(TextDetextModuleImpl.Detection detection) {
        //TODO: implement translate

    }

    @Override
    public void setOnTranslateListener(OnTranslateListener listener) {
        this.listener = listener;
    }

    @Override
    public void release() {
        translateService = null;
    }

}
