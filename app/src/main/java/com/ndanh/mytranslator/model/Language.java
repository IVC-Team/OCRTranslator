package com.ndanh.mytranslator.model;


import android.content.Context;
import android.support.annotation.NonNull;

import com.ndanh.mytranslator.R;

/**
 * Created by ndanh on 5/3/2017.
 */

public class Language {

    public static String ENGLISH;
    public static String VIETNAMESE;
    public static String JAPANESE;
    public static String ENGLISH_SHORT;
    public static String VIETNAMESE_SHORT;
    public static String JAPANESE_SHORT;
    public static String ENGLISH_LOCALE;
    public static String VIETNAMESE_LOCALE;
    public static String JAPANESE_LOCALE;

    protected static Language language;

    private Language(final Context context) {
        ENGLISH = context.getString ( R.string.lang_eng );
        VIETNAMESE = context.getString ( R.string.lang_vie );
        JAPANESE = context.getString ( R.string.lang_jpn );
        ENGLISH_SHORT = context.getString ( R.string.short_lang_eng );
        VIETNAMESE_SHORT = context.getString ( R.string.short_lang_vie );
        JAPANESE_SHORT = context.getString ( R.string.short_lang_jpn );
        ENGLISH_LOCALE = context.getString ( R.string.locale_lang_eng );
        VIETNAMESE_LOCALE = context.getString ( R.string.locale_lang_vie );
        JAPANESE_LOCALE = context.getString ( R.string.locale_lang_jpn );
    }

    public static void init(Context context){
        language = new Language(context);
    }


    public static String getShortLanguage(@NonNull ELanguage e) {
        switch (e){
            case ENG:
                return language.ENGLISH_SHORT;
            case VIE:
                return language.VIETNAMESE_SHORT;
            case JAP:
                return language.JAPANESE_SHORT;
            default:
                return "";
        }
    }

    public static String getLocaleString(@NonNull ELanguage e) {
        switch (e){
            case ENG:
                return language.ENGLISH_LOCALE;
            case VIE:
                return language.VIETNAMESE_LOCALE;
            case JAP:
                return language.JAPANESE_LOCALE;
            default:
                return "";
        }
    }

    public static String getLongLanguage(@NonNull ELanguage e) {
        switch (e){
            case ENG:
                return language.ENGLISH;
            case VIE:
                return language.VIETNAMESE;
            case JAP:
                return language.JAPANESE;
            default:
                return "";
        }
    }

    public static void clean() {
        ENGLISH = null;
        VIETNAMESE = null;
        JAPANESE = null;
        ENGLISH_SHORT = null;
        VIETNAMESE_SHORT = null;
        JAPANESE_SHORT = null;
        ENGLISH_LOCALE = null;
        VIETNAMESE_LOCALE = null;
        JAPANESE_LOCALE = null;
        language = null;
    }

    public static ELanguage setLangFromMenu(int itemId) {
        switch (itemId){
            case R.id.lang_id1:
                return ELanguage.ENG;
            case R.id.lang_id2:
                return ELanguage.JAP;
            case R.id.lang_id3:
                return ELanguage.VIE;
            default:
                return null;
        }
    }

    public enum ELanguage{
        ENG, VIE, JAP;
    }
}
