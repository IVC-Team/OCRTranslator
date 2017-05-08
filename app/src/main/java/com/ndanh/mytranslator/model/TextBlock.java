package com.ndanh.mytranslator.model;

import android.graphics.Rect;

/**
 * Created by ndanh on 4/19/2017.
 */

public final class TextBlock {
    private static int SPACE_WIDTH = 30;
    private String text;
    private Rect rectBound;
    private int width, height;
    private int line;

    public TextBlock(String text, Rect rectBound, int width, int height) {
        this.text = text;
        this.rectBound = rectBound;
        this.width = width;
        this.height = height;
    }

    public TextBlock( String text,Rect rectBound, int width, int height , int line) {
        this.height = height;
        this.line = line;
        this.rectBound = rectBound;
        this.text = text;
        this.width = width;
    }

    public static boolean checkNext(TextBlock var1, TextBlock var2){
        if(var1.line != var2.line){
            return false;
        }
        int totalWidth = 0;
        if(var1.rectBound.left < var2.rectBound.left){
            totalWidth = var2.rectBound.right - var1.rectBound.left;
        }else{
            totalWidth = var1.rectBound.right - var2.rectBound.left;
        }
        return totalWidth <= (SPACE_WIDTH + var1.rectBound.width() + var2.rectBound.width());
    }
}
