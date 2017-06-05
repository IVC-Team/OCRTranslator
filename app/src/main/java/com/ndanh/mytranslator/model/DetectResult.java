package com.ndanh.mytranslator.model;


import android.graphics.Rect;

import java.util.Map;

/**
 * Created by dauda on 05/06/2017.
 */

public class DetectResult {
    public Map<Rect, String> getData() {
        return data;
    }

    public void setData(Map<Rect, String> data) {
        this.data = data;
    }

    private Map<Rect,String> data;
}
