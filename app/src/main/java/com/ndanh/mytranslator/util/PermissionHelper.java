package com.ndanh.mytranslator.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by ndanh on 4/28/2017.
 */

public class PermissionHelper {

    public static final int REQUEST_PERMISSION_CODE = 100;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestPermission(Activity context, String... permissions){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, permissions, REQUEST_PERMISSION_CODE);
                }
            }
        }
    }
}