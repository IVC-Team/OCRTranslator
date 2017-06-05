package com.ndanh.mytranslator.modulesimpl;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.googlecode.tesseract.android.PageIterator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.ndanh.mytranslator.model.DetectResult;
import com.ndanh.mytranslator.services.IDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndanh on 4/18/2017.
 */

public class TextDetextModuleImpl implements IDetector {
    private final static String TAG = "TextDetextModuleImpl";
    private TessBaseAPI mTess;
    private  String datapath = "";
    private Context mContext;
    private DetectBitmapCallback callback;

    public TextDetextModuleImpl(Context context) {
        this.mContext = context;

        datapath = context.getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();

    }

    private TextDetextModuleImpl() {

    }

    @Override
    public void release() {
        callback = null;
        mTess.end();
        mTess = null;
    }

    @Override
    public void setLanguage(String lang) {
        checkFile(new File(datapath + "tessdata/"), lang);
        mTess.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        mTess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
        mTess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        mTess.init(datapath, lang);
    }

    @Override
    public void detectBitmap(Bitmap bitmap) {
//        mTess.setImage(bitmap);
//        mTess.getUTF8Text();
//        PageIterator iterator = mTess.getResultIterator();
//        Map<Rect, String> result = new HashMap<Rect, String> ();
//        while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD)){
//            result.put(iterator.getBoundingRect(TessBaseAPI.PageIteratorLevel.RIL_WORD))
//        }

//        if(callback != null){
//            callback.onSuccess ( result);
//        }
    }

    private void checkFile(File dir, String lang) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles(lang);
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/"+ lang +".traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles(lang);
            }
        }
    }

    private void copyFiles(String lang) {
        try {
            String filepath = datapath +"/tessdata/"+ lang +".traineddata";
            AssetManager assetManager = mContext.getAssets();

            InputStream instream = assetManager.open("tessdata/"+ lang +".traineddata");
            OutputStream outstream = new FileOutputStream (filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File (filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDetectBitmapCallback(DetectBitmapCallback callback){
        this.callback = callback;
    }



}
