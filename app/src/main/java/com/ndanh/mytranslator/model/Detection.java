package com.ndanh.mytranslator.model;


import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ndanh on 4/19/2017.
 */

public class Detection {
    @StringDef({ ENG ,JPN ,VIE })
    @Retention(RetentionPolicy.SOURCE)
    private @interface Language {}

    private Timestamp timestamp;
    private List<TextBlock> textBlocks;
    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(@Language String lang) {
        this.lang = lang;
    }

    public List<TextBlock> getTextBlocks() {
        return textBlocks;
    }

    public void setTextBlocks(List<TextBlock> textBlocks) {
        this.textBlocks = textBlocks;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public final static String ENG = "eng";
    public final static String JPN = "jpn";
    public final static String VIE = "vie";

    public Detection(List<TextBlock> textBlocks,@Language String lang){
        this.lang = lang;
        timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        this.textBlocks = textBlocks;
    }
}
