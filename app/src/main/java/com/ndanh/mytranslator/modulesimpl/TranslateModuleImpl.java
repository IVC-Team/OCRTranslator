package com.ndanh.mytranslator.modulesimpl;

import android.content.Context;
import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.ApiUtils;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.Data;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslateService;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.Translation;
import com.ndanh.mytranslator.modulesimpl.TranslateGCloud.TranslatorResponse;
import com.ndanh.mytranslator.services.ITranslate;
import com.ndanh.mytranslator.util.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public void translate(List<String> src, String srclang, String destLang) {
        if(srclang == destLang){
            if(listener == null) return;
            TranslatorResponse response = new TranslatorResponse ();
            Data data = new Data ();
            List<Translation> translations = new ArrayList<> (  );
            Translation translation;
            for (String item : src) {
                translation = new Translation ();
                translation.setTranslatedText ( item );
                translations.add ( translation );
            }
            data.setTranslations ( translations );
            response.setData ( data );
            listener.onSuccess(response);
            return;
        }
        Map<String, String> data = new HashMap<>();
        data.put(Config.TRANSLATE_GCLOUD_SOURCE, srclang);
        data.put(Config.TRANSLATE_GCLOUD_TARGET, destLang);
        data.put(Config.TRANSLATE_GCLOUD_FORMAT, Config.TRANSLATE_GCLOUD_FORMAT_TYPE);
        data.put(Config.TRANSLATE_GCLOUD_KEY, Config.TRANSLATE_GCLOUD_API_KEY);
        translateService.getTranslateResult(src, data).enqueue(new Callback<TranslatorResponse>() {
            @Override
            public void onResponse(Call<TranslatorResponse> call, Response<TranslatorResponse> response) {
                if(listener == null) return;
                if(response.isSuccessful()) {
                    listener.onSuccess(response.body());
                }else {

                    listener.onFailed(mContext.getString( R.string.translate_module_fail_message));
                }
            }

            @Override
            public void onFailure(Call<TranslatorResponse> call, Throwable t) {
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
