package com.ndanh.mytranslator.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.support.annotation.StringDef;
import android.util.Log;

import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndanh on 3/23/2017.
 */

public class MyTessOCR {
    @StringDef({ ENG ,JPN ,VIE })
    @Retention(RetentionPolicy.SOURCE)
    private @interface Language {}

    public final static String ENG = "eng";
    public final static String JPN = "jpn";
    public final static String VIE = "vie";

    public static final String DATA_PATH = Environment
                .getExternalStorageDirectory().toString() + "/AndroidOCR/";

    private String lang;

    private static final String TAG = "TESSERACT";
    private AssetManager assetManager;

    private TessBaseAPI mTess;

    public MyTessOCR(AssetManager assetManager,@Language String lang) {

    Log.i(TAG, DATA_PATH);
        this.assetManager = assetManager;
        this.lang = lang;
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(new File(DATA_PATH + "tessdata/", lang + ".traineddata"));

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

        mTess = new TessBaseAPI();
        mTess.setDebug(true);
        mTess.init(DATA_PATH, lang);

    }

    public String getResultsFullBlock(byte[] bitmap, int width, int height, int bpp, int bpl){
        mTess.setImage(bitmap,width, height, bpp, bpl);
        mTess.getBoxText(1);

        String a = "";
        ResultIterator iterator = mTess.getResultIterator();
        iterator.begin();
        try {
            do {
                a += " " + iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD);
            }while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  a;
    }

    public String getResultsFullBlock(Bitmap bitmap){
        mTess.setImage(bitmap);
        mTess.getBoxText(1);

        String a = "";
        ResultIterator iterator = mTess.getResultIterator();
        iterator.begin();
        try {
            do {
                a += " " + iterator.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_TEXTLINE);
            }while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_TEXTLINE));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  a;
    }
    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }
}
