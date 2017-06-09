package com.ndanh.mytranslator.model;

import android.graphics.Rect;

/**
 * Created by dauda on 05/06/2017.
 */

public class DetectResult {
    private String text;
    private Rect position;
    private static int LENGT_POSITION = 16;
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Rect getPosition() {
        return position;
    }

    public void setPosition(Rect position) {
        this.position = position;
    }

    public void mergePosition(Rect rect){
        this.position.top = rect.top < this.position.top ? rect.top : this.position.top;
        this.position.bottom = rect.bottom > this.position.bottom ? rect.bottom : this.position.bottom;
        this.position.right = rect.right;
    }

    public void mergeText(String text){
        this.text += " " + text;
    }

    @Override
    public String toString() {
        return DetectResult.parseRect2String(this.getPosition ()) + this.text ;
    }

    public static DetectResult parseDetectResult(String inst){
        DetectResult result = new DetectResult ();
        result.setPosition ( parseString2Rect (  inst.substring (0 , LENGT_POSITION ) ));
        result.setText ( inst.substring (LENGT_POSITION ));
        return result;
    }

    // parse left -> top -> right -> bottom
    public static String parseRect2String(Rect rect){
        return String.format("%04d",  rect.left) + String.format("%04d",rect.top) + String.format("%04d",  rect.right) + String.format("%04d", rect.bottom);
    };

    public static Rect parseString2Rect(String strPosition){
        return  new Rect ( Integer.valueOf ( strPosition.substring (0 ,4 )),
                Integer.valueOf ( strPosition.substring (4  ,8 )),
                Integer.valueOf ( strPosition.substring (8  ,12)),
                Integer.valueOf ( strPosition.substring (12  ,16)) );
    }
}
