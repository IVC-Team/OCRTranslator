package com.ndanh.mytranslator.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.sql.Timestamp;

/**
 * Created by ndanh on 4/19/2017.
 */

public class Frame {
    @IntDef({
           ImageFormat.RGB_565,
           ImageFormat.NV16,
           ImageFormat.YUY2,
           ImageFormat.YV12,
           ImageFormat.JPEG,
           ImageFormat.NV21,
           ImageFormat.YUV_420_888,
           ImageFormat.YUV_422_888,
           ImageFormat.YUV_444_888,
           ImageFormat.FLEX_RGB_888,
           ImageFormat.FLEX_RGBA_8888,
           ImageFormat.RAW_SENSOR,
           ImageFormat.RAW_PRIVATE,
           ImageFormat.RAW10,
           ImageFormat.RAW12,
           ImageFormat.DEPTH16,
           ImageFormat.DEPTH_POINT_CLOUD,
           ImageFormat.PRIVATE
    })
    @Retention(RetentionPolicy.SOURCE)
    public  @interface FrameFormat {}
    private int Id;
    private Timestamp timestamp;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getHeight() {
        return bitmap.getHeight();
    }

    public int getId() {
        return Id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getWitdh() {
        return bitmap.getWidth();
    }

    public Frame(){
    }

    public static class Builder{
        private Frame mFrame = new Frame();

        public Builder() {
        }

        public Frame.Builder setBitmap(Bitmap bitmap) {
            mFrame.bitmap = bitmap;
            return this;
        }

        public Frame.Builder setImageData(ByteBuffer data, int width, int height,@FrameFormat @NonNull int formatImage) {
            if(data == null) {
                throw new IllegalArgumentException("Null image data supplied.");
            } else if(data.capacity() < width * height) {
                throw new IllegalArgumentException("Invalid image data size.");
            } else {
                YuvImage yuvImage = new YuvImage(data.array(), formatImage, width, height, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, os);
                byte[] jpegByteArray = os.toByteArray();
                mFrame.bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return this;
            }
        }

        public Frame.Builder setId(int var1) {
            this.mFrame.Id = var1;
            return this;
        }

        public Frame.Builder setTimestampMillis(long var1) {
            this.mFrame.timestamp = new Timestamp(var1);
            return this;
        }

        public Frame.Builder setTimestampMillis(Timestamp var1) {
            this.mFrame.timestamp = var1;
            return this;
        }

        public Frame build() {
            if(this.mFrame.bitmap == null) {
                throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
            } else {
                return this.mFrame;
            }
        }
    }
}
