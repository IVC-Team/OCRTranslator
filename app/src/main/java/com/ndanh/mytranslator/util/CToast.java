package com.ndanh.mytranslator.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ndanh on 6/7/2017.
 */

public class CToast {
    private static final CToast ourInstance = new CToast (  ) ;
    private static boolean isShow = true;
    private Context context;
    static CToast getInstance() {
        return ourInstance;
    }

    private CToast() {
    }
    public static void init(Context context){
        CToast.getInstance ().context = context;
    }

    public static void showMessage(String msg){
        if(isShow) Toast.makeText ( getInstance ().context, msg , Toast.LENGTH_LONG ).show ();
    }
}
