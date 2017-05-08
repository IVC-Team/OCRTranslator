package com.ndanh.mytranslator.modulesimpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ndanh.mytranslator.model.Frame;
import com.ndanh.mytranslator.services.IDetector;
import com.ndanh.mytranslator.util.MyTessOCR;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by ndanh on 4/18/2017.
 */

public class TextDetextModuleImpl implements IDetector {
    private final static String TAG = "TextDetextModuleImpl";
    private MyTessOCR tessOCR;
    private OnDetectListener listener;
    public TextDetextModuleImpl(Context context) {
        tessOCR = new MyTessOCR(context.getAssets(), MyTessOCR.ENG);
    }

    private TextDetextModuleImpl() {

    }

    @Override
    public void release() {
        //TODO: release Runable proccess
    }

    @Override
    public void receiveFrame(Frame frame) {
        detect(frame);
    }


    @Override
    public void setDetectListener(IDetector.OnDetectListener listener) {
        this.listener = listener;
    }

    private void detect(Frame frame){
        //TODO: Detect implement
        Detection result = new Detection(frame);
        if(listener != null){
            if(true){//TODO: Check result
                listener.onSuccess(result);
            } else{
                listener.onFailed("Failded");//TODO: Change message
            }

        }
    }

    public class Detection{
        public class Item{

            private Rect boundingBox;
            private String text;

            public Item() {
                this.boundingBox = new Rect(0,0,0,0);
                this.text = "";
            }

            public Item(@NonNull Rect boundingBox,@NonNull String text) {
                this.boundingBox = boundingBox;
                this.text = text;
            }

            public Rect getBoundingBox() {
                return boundingBox;
            }

            public void setBoundingBox(Rect boundingBox) {
                this.boundingBox = boundingBox;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public Frame getFrame() {
            return frame;
        }

        private List<Item> items;
        private final Frame frame;

        public Detection(Frame frame){
            this.frame = frame;
        }
    }
}
