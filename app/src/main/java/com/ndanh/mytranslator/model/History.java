package com.ndanh.mytranslator.model;

/**
 * Created by ndanh on 5/8/2017.
 */

public class History {
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLangsrc() {
        return langsrc;
    }

    public void setLangsrc(String langsrc) {
        this.langsrc = langsrc;
    }

    public String getLangdes() {
        return langdes;
    }

    public void setLangdes(String langdes) {
        this.langdes = langdes;
    }

    public String getTextsrc() {
        return textsrc;
    }

    public void setTextsrc(String textsrc) {
        this.textsrc = textsrc;
    }

    public String getTextdes() {
        return textdes;
    }

    public void setTextdes(String textdes) {
        this.textdes = textdes;
    }

    private long timestamp;

    public History( String langsrc, String langdes, String textsrc, String textdes) {
        this();
        this.langsrc = langsrc;
        this.langdes = langdes;
        this.textsrc = textsrc;
        this.textdes = textdes;
    }

    public History(){
        this.timestamp = System.currentTimeMillis () / 1000;
    }
    private String langsrc;
    private String langdes;
    private String textsrc;
    private String textdes;
}
