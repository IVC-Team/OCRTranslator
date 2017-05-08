package com.ndanh.mytranslator.screen.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.model.Frame;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class CameraActivity extends AppCompatActivity implements CameraContract.ICameraView {

    @BindView(R.id.tranlate_result)
    ImageView imageViewResult;
    @BindView((R.id.preview))
    LinearLayout cameraView;

    private Camera camera;
    private CameraPreview mPreview;
    private Context context;


    private static final String TAG = "OcrCaptureActivity";
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraContract.ICameraPresenter presenter;

    private Map<byte[], ByteBuffer> mBytesToByteBuffer = new HashMap<>();
    private Size mPreviewSize;

    private Thread mProcessingThread;
    private FrameProcessingRunnable mFrameProcessor;

    //    -------------------------- Activity methods override implement ----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasCamera(context)) {
            Toast toast = Toast.makeText(context, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        restartCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.presenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.presenter.stop();
        mFrameProcessor.setActive(false);
        mFrameProcessor.release();
        camera = null;
        mPreview = null;
        context = null;
        mBytesToByteBuffer  = null;
        mPreviewSize = null;
        mProcessingThread = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            initialize();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
    }

//    -------------------------- CameraContract CameraView implement ----------------------------

    @Override
    public void initPresenter() {
        new CameraPresenter(this, ModuleManageImpl.getInstance().getTextDetect(), ModuleManageImpl.getInstance().getTranslateModule());
    }

    @Override
    public void setPresenter( CameraContract.ICameraPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setResultTranslate(Bitmap bitmap) {
        imageViewResult.setImageBitmap(bitmap);
    }

    @Override
    public void showMessage(String text) {
//        Toast.makeText(context, text,Toast.LENGTH_LONG).show();
    }

    //    -------------------------- Private methods ----------------------------


    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallbackWithBuffer(null);
            mPreview.getHolder().removeCallback(mPreview);
            camera.release();
            camera = null;
        }
    }

    private void restartCamera(){
        if (camera == null) {
            camera = new CameraBuilder(this.context)
                    .setAutoFocus(true)
                    .setPreviewFormat(ImageFormat.NV21)
                    .build();
            mFrameProcessor = new FrameProcessingRunnable();
            mPreviewSize = new Size(camera.getParameters().getPreviewSize().width,camera.getParameters().getPreviewSize().height );
            camera.setPreviewCallbackWithBuffer(new CameraPreviewCallback());
            camera.addCallbackBuffer(createPreviewBuffer(mPreviewSize));
            camera.addCallbackBuffer(createPreviewBuffer(mPreviewSize));
            camera.addCallbackBuffer(createPreviewBuffer(mPreviewSize));
            camera.addCallbackBuffer(createPreviewBuffer(mPreviewSize));
            mPreview.setCamera(camera);
            mPreview.getHolder().addCallback(mPreview);

            mProcessingThread = new Thread(mFrameProcessor);
            mFrameProcessor.setActive(true);
            mProcessingThread.start();

        }
    }

    private boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private byte[] createPreviewBuffer(Size previewSize) {
        if(previewSize ==null) return null;
        int bitsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21);
        long sizeInBits = previewSize.height * previewSize.width * bitsPerPixel;
        int bufferSize = (int) Math.ceil(sizeInBits / 8.0d) + 1;

        byte[] byteArray = new byte[bufferSize];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        if (!buffer.hasArray() || (buffer.array() != byteArray)) {
            throw new IllegalStateException("Failed to create valid buffer for camera source.");
        }
        mBytesToByteBuffer.put(byteArray, buffer);
        return byteArray;
    }

    private void initialize() {
        context = getApplicationContext();
        mPreview = new CameraPreview(context, camera);
        cameraView.addView(mPreview);
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
    }

    //    -------------------------- Inner Class ----------------------------

    private class CameraPreviewCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            mFrameProcessor.setNextFrame(data,camera);
        }
    }

    private class FrameProcessingRunnable implements Runnable {
        private long mStartTimeMillis = SystemClock.elapsedRealtime();

        // This lock guards all of the member variables below.
        private final Object mLock = new Object();
        private boolean mActive = true;

        // These pending variables hold the state associated with the new frame awaiting processing.
        private long mPendingTimeMillis;
        private int mPendingFrameId = 0;
        private ByteBuffer mPendingFrameData;

        FrameProcessingRunnable(){
        }

        void release() {
            assert (mProcessingThread.getState() == Thread.State.TERMINATED);
        }

        void setActive(boolean active) {
            synchronized (mLock) {
                mActive = active;
                mLock.notifyAll();
            }
        }

        void setNextFrame(byte[] data, Camera camera) {
            synchronized (mLock) {
                if (mPendingFrameData != null) {
                    camera.addCallbackBuffer(mPendingFrameData.array());
                    mPendingFrameData = null;
                }

                if (!mBytesToByteBuffer.containsKey(data)) {
                    Log.d(TAG,
                            "Skipping frame.  Could not find ByteBuffer associated with the image " +
                                    "data from the camera.");
                    return;
                }

                mPendingTimeMillis = SystemClock.elapsedRealtime() - mStartTimeMillis;
                mPendingFrameId++;
                mPendingFrameData = mBytesToByteBuffer.get(data);
                mLock.notifyAll();
            }
        }

        @Override
        public void run() {
            Frame outputFrame;
            while (true) {
                synchronized (mLock) {
                    while (mActive && (mPendingFrameData == null)) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException e) {
                            Log.d(TAG, "Frame processing loop terminated.", e);
                            return;
                        }
                    }

                    if (!mActive) {
                        return;
                    }

                    outputFrame = new Frame.Builder()
                            .setImageData(mPendingFrameData, mPreviewSize.width,
                                    mPreviewSize.height, ImageFormat.NV21)
                            .setId(mPendingFrameId)
                            .setTimestampMillis(mPendingTimeMillis)
                            .build();
                    camera.addCallbackBuffer(mPendingFrameData.array());
                }

                try {
                    Log.e(TAG, "Duy Anh");
                    presenter.getPreviewFrame(outputFrame);
                } catch (Throwable t) {
                    Log.e(TAG, "Exception thrown from receiver.", t);
                } finally {
                }
            }
        }
    }

    public class Size{
        public Size(int w, int h) {
            width = w;
            height = h;
        }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Size)) {
                return false;
            }
            Size s = (Size) obj;
            return width == s.width && height == s.height;
        }
        @Override
        public int hashCode() {
            return width * 32713 + height;
        }
        public int width;
        public int height;
    }

    private class CameraBuilder{
        private Camera mCamera;
        private Context mContext;

        public CameraBuilder(Context context){
            mCamera = getCameraInstance();
            mCamera.setDisplayOrientation(90);
            mContext = context;

            Size previewSize = getOptimalPreviewSize(mCamera, mContext);
            if(previewSize != null){
                Camera.Parameters params = mCamera.getParameters();
                params.setPreviewSize(previewSize.width, previewSize.height);
                mCamera.setParameters(params);
            }
        }

        public CameraBuilder setAutoFocus(boolean autoFocus){
            if(!autoFocus) return this;
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
            return this;
        }

        public CameraBuilder setPreviewCallbackWithBuffer(Camera.PreviewCallback callback){
            if (callback == null) return this;
            mCamera.setPreviewCallbackWithBuffer(callback);
            return this;
        }

        public CameraBuilder setPreviewFormat( int imageFormat){
            Camera.Parameters params = mCamera.getParameters();
            params.setPreviewFormat(imageFormat);
            mCamera.setParameters(params);
            return this;
        }

        public Camera build(){
            return mCamera;
        }

        private Camera getCameraInstance(){
            Camera c = null;
            try {
                c = Camera.open(); // attempt to get a Camera instance
            }
            catch (Exception e){
                // Camera is not available (in use or does not exist)
            }
            return c; // returns null if camera is unavailable
        }

        private Size getOptimalPreviewSize(Camera mCamera, Context _context) {
            List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
            DisplayMetrics metrics = _context.getResources().getDisplayMetrics();
            int w = metrics.widthPixels;
            int h = metrics.heightPixels;

            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) h / w;

            if (w > h)
                targetRatio = (double) w / h;

            if (sizes == null)
                return null;

            Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            for (Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (size.height >= size.width)
                    ratio = (float) size.height / size.width;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                    continue;
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }

            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (Camera.Size size : sizes) {
                    if (Math.abs(size.height - h) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - h);
                    }
                }
            }
            return new Size(optimalSize.width, optimalSize.height);
        }
    }



}
