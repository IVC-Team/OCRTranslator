package com.ndanh.mytranslator.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.nio.ByteBuffer;

/**
 * Created by ndanh on 4/19/2017.
 */

public class Common {
    public static ByteBuffer getGrayscaleImageData(Bitmap bitmap) {
        int var1 = bitmap.getWidth();
        int var2 = bitmap.getHeight();
        int[] var3 = new int[var1 * var2];
        bitmap.getPixels(var3, 0, var1, 0, 0, var1, var2);
        byte[] var4 = new byte[var1 * var2];

        for(int var5 = 0; var5 < var3.length; ++var5) {
            var4[var5] = (byte)((int)((float) Color.red(var3[var5]) * 0.299F + (float)Color.green(var3[var5]) * 0.587F + (float)Color.blue(var3[var5]) * 0.114F));
        }
        return ByteBuffer.wrap(var4);
    }
}
