package com.ndanh.mytranslator.model;

import android.graphics.Rect;

/**
 * Created by dauda on 05/06/2017.
 */

public class DetectResult {
    private String text;
    private Rect position;

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
        this.text.replace(">","");
        return DetectResult.parseRect2String(this.getPosition ()) + this.text ;
    }

    public static DetectResult parseDetectResult(String inst){
        DetectResult result = new DetectResult ();

        int idx = inst.indexOf ( '>' );

        result.setPosition ( parseString2Rect (  inst.substring (1 , idx ) ));
        result.setText ( inst.substring (idx + 1 ));

        return result;
    }

    // parse left -> top -> right -> bottom
    public static String parseRect2String(Rect rect){
        return "<" + rect.left + "-" + rect.top + "-" + rect.right + "-" + rect.bottom + ">";
    };

    public static Rect parseString2Rect(String strPosition){
        String[] parts = strPosition.split("-");
        if(parts.length == 4){
            return  new Rect ( Integer.valueOf (parts[0]), Integer.valueOf (parts[1]), Integer.valueOf (parts[2]), Integer.valueOf (parts[3]) );
        } else  {
            return null;
        }

    }
}
