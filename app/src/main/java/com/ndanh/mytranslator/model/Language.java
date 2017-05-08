package com.ndanh.mytranslator.model;


import java.util.Locale;

/**
 * Created by ndanh on 5/3/2017.
 */

public class Language {

    private ELanguage lang;

    public Language(ELanguage e){
        this.lang = e;
    }

    @Override
    public String toString() {
        switch (lang){
            case ENGLISH:
                return "English";
            case VIETNAMESE:
                return "Vietnamese";
            case JAPANESE:
                return "Japanese";
            default:
                return "";
        }
    }

    public void changeLanguage(String title) {
        switch (title){
            case "Japanese":
                lang = ELanguage.JAPANESE;
                break;
            case "English":
                lang = ELanguage.ENGLISH;
                break;
            case "Vietnamese":
                lang = ELanguage.VIETNAMESE;
                break;
        }
    }

    public String getShortLanguage() {
        switch (lang){
            case ENGLISH:
                return "eng";
            case VIETNAMESE:
                return "vie";
            case JAPANESE:
                return "jpn";
            default:
                return "";
        }
    }

    public String getLocaleString() {
        switch (lang){
            case ENGLISH:
                return "en_US";
            case VIETNAMESE:
                return "vi_VN";
            case JAPANESE:
                return "ja_JP";
            default:
                return "";
        }
    }


    public enum ELanguage{
        ENGLISH, VIETNAMESE, JAPANESE;
    }
}
