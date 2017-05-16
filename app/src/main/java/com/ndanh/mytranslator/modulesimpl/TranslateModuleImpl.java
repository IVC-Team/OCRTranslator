package com.ndanh.mytranslator.modulesimpl;

import android.content.Context;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.ApiUtils;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslateService;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslatorResponse;
import com.ndanh.mytranslator.services.ITranslate;
import com.ndanh.mytranslator.util.Config;

import java.io.IOException;
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
        Map<String, String> data = new HashMap<>();
        data.put(Config.TRANSLATE_GCLOUD_QUERY, src);
        data.put(Config.TRANSLATE_GCLOUD_SOURCE, srclang);
        data.put(Config.TRANSLATE_GCLOUD_TARGET, destLang);
        data.put(Config.TRANSLATE_GCLOUD_FORMAT, Config.TRANSLATE_GCLOUD_FORMAT_TYPE);
        data.put(Config.TRANSLATE_GCLOUD_KEY, Config.TRANSLATE_GCLOUD_API_KEY);
        translateService.getTranslateResult(data).enqueue(new Callback<TranslatorResponse>() {
            @Override
            public void onResponse(Call<TranslatorResponse> call, Response<TranslatorResponse> response) {
                if(listener == null) return;
                if(response.isSuccessful()) {
                    listener.onSuccess(response.body().getData().getTranslations().get(0).getTranslatedText());
                }else {
//                    String.valueOf(response.code())
                    listener.onFailed(mContext.getString( R.string.translate_module_fail_message));
                }
            }

            @Override
            public void onFailure(Call<TranslatorResponse> call, Throwable t) {
//                String.valueOf(t.getMessage());
                if(listener == null) return;
                listener.onFailed(mContext.getString( R.string.translate_module_fail_message));
            }
        });

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
