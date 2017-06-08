package com.ndanh.mytranslator.modulesimpl;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.ResultIterator;
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
import java.util.List;

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
        mTess = new TessBaseAPI();
        mTess.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        mTess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
        mTess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        mTess.init(datapath, lang);
    }

    @Override
    public void detectBitmap(Bitmap bitmap) {
        Pix temp = ReadFile.readBitmap ( bitmap );
        temp = Binarize.sauvolaBinarizeTiled ( temp );

        mTess.setImage( WriteFile.writeBitmap ( temp ));

        mTess.getUTF8Text();


        ResultIterator iterator = mTess.getResultIterator ();
        List<DetectResult> result = new ArrayList<DetectResult> ();
        DetectResult item = new DetectResult ();
        while (iterator.next(TessBaseAPI.PageIteratorLevel.RIL_WORD)){
            if(iterator.confidence ( TessBaseAPI.PageIteratorLevel.RIL_WORD ) < 80) continue;
            if(iterator.getUTF8Text ( TessBaseAPI.PageIteratorLevel.RIL_WORD ) == "1")
            if(checkPosition ( item ,iterator.getBoundingRect ( TessBaseAPI.PageIteratorLevel.RIL_WORD ))){
                item.mergeText (iterator.getUTF8Text ( TessBaseAPI.PageIteratorLevel.RIL_WORD ) );
                item.mergePosition (iterator.getBoundingRect ( TessBaseAPI.PageIteratorLevel.RIL_WORD ));
            } else {
                item = new DetectResult ();
                item.setText(iterator.getUTF8Text ( TessBaseAPI.PageIteratorLevel.RIL_WORD ));
                item.setPosition (iterator.getBoundingRect ( TessBaseAPI.PageIteratorLevel.RIL_WORD ));
                result.add ( item );
            }
        }
        mTess.end ();
        if(callback != null){
            callback.onSuccess ( result);
        }
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

    private boolean checkPosition(DetectResult item, Rect rect2){
        if(item.getPosition () == null ||item.getText ()== null || rect2 == null)
            return false;

        if(item.getText ().contains(".")) return false;
        Rect rect1 = item.getPosition ();
        if(rect1.bottom < rect2.top ){
            return false;
        }

        int average = (rect1.width () + rect2.width () )/2;
        int distance = rect1.left - rect2.right;
        return distance <= average;
    }
}
