package com.ndanh.mytranslator.screen.camera;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.base.NavigatorFooterActivity;
import com.ndanh.mytranslator.model.DetectResult;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.util.PermissionHelper;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.io.IOException;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class CameraActivity extends NavigatorFooterActivity
        implements SurfaceHolder.Callback, CameraContract.ICameraView {

    //region variable
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private boolean previewMode = true;
    private static final String TAG = "OcrCaptureActivity";
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private CameraContract.ICameraPresenter presenter;

    @BindView ( R.id.cameraView ) SurfaceView surfaceView;
    @BindView ( R.id.tranlate_result ) ImageView rawCapture;
    @BindView((R.id.preview)) RelativeLayout cameraView;
    @BindView ( R.id.navigation_footer_camera ) RelativeLayout hiddenPanel;
    @BindView ( R.id.btn_takeButton ) ImageView btnTakeButton;
    @BindView ( R.id.ar_mask ) ImageView arMask;



    private PictureCallback captureImageCallback = new PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera)
        {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bmp , 0, 0, bmp .getWidth(), bmp .getHeight(), matrix, true);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap ,cameraView.getWidth ()/2,cameraView.getHeight ()/2,true);

//            drawTextToBitmap(scaledBitmap);
//            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, scaledBitmap, 0);

            rawCapture.setImageBitmap(scaledBitmap);
            rawCapture.bringToFront ();
            presenter.doTranslate(scaledBitmap.copy(scaledBitmap.getConfig(), true));
            refreshCamera();
            btnTakeButton.setClickable ( true );

        }
    };
    //endregion

    //region Activity methods override implement
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initPresenter();
        ButterKnife.bind(this);
        PermissionHelper.requestPermission ( this, Manifest.permission.CAMERA );
        PermissionHelper.requestPermission ( this, Manifest.permission.WRITE_EXTERNAL_STORAGE );
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceView.bringToFront ();
    }

    @Override
    public void onResume() {
        super.onResume();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //endregion

    //region CameraContract CameraView implement

    @Override
    public void initPresenter() {
        new CameraPresenter(this, ModuleManageImpl.getInstance().getTextDetect(), ModuleManageImpl.getInstance().getTranslateModule());
    }

    @Override
    public void setPresenter( CameraContract.ICameraPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Language.ELanguage getSrcLang() {
        return Language.ELanguage.ENG;
    }

    @Override
    public Language.ELanguage getDestLang() {
        return Language.ELanguage.VIE;
    }

    @Override
    public void displayResult(List<DetectResult> result, int width, int height) {

//        arMask.setImageBitmap ( result );
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor( getApplicationContext() ,R.color.transparent));
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);

        drawTextToBitmap(bitmap , result);
        arMask.setImageBitmap ( bitmap );
        arMask.bringToFront ();
    }

    //endregion

    //region NavigatorFooterActivity implement
    @Override
    public void invisibleView() {
        hiddenPanel.setVisibility ( View.GONE );
    }


    //endregion

    //region SurfaceHolder Callback implement
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
    //endregion

    //region Method
    public void refreshCamera()
    {
        if(camera == null){
            try
            {
                camera = Camera.open();
            }
            catch(RuntimeException e)
            {
                System.err.println(e);
            }
            Camera.Parameters param;
            param = camera.getParameters();
            param.getPictureSize ();
            param.getPreviewSize ();
            List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
            List<Camera.Size> pictureSizes = param.getSupportedPictureSizes ();
            param.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
            param.setPictureSize ( previewSizes.get(0).width, previewSizes.get(0).height );

            param.getPictureSize ();
            param.getPreviewSize ();
            if (param.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            camera.setDisplayOrientation(90);
            camera.setParameters(param);
            try
            {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
            catch(Exception e)
            {
                System.err.println(e);
                return;
            }
        }

        if(surfaceHolder.getSurface() == null) return;

        try
        {
            camera.stopPreview();
        }
        catch(Exception e) {}

        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch(Exception e) {}
    }

    public void captureImage(View view) throws IOException
    {
        if(previewMode){
            btnTakeButton.setClickable ( false );
            camera.takePicture(null, null, captureImageCallback);
            previewMode = false;
        }else{
            previewMode = true;
            surfaceView.bringToFront ();
        }
    }
    //endregion

    private void drawTextToBitmap(Bitmap bitmap, List<DetectResult> result) {
        Canvas canvas = new Canvas(bitmap);
        Canvas canvas1;
//        bitmap.eraseColor(android.graphics.Color.TRANSPARENT);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);


        paint.setColor(Color.WHITE);

        Bitmap bm;

        for (DetectResult item: result) {
            int textSize = determineMaxTextSize(item.getText() , item.getPosition().width());
            paint.setTextSize(textSize);
            float textWidth = paint.measureText(item.getText() );
            bm = Bitmap.createBitmap((int)textWidth, textSize, Bitmap.Config.ARGB_8888);
            canvas1 = new Canvas(bm);
            canvas1.drawText(item.getText() , 0,0 ,paint);
            canvas.drawBitmap(bm,  null,item.getPosition(),null);
        }
    }

    private int determineMaxTextSize(String str, float maxWidth)
    {
        int size = 0;
        Paint paint = new Paint();

        do {
            paint.setTextSize(++ size);
        } while(paint.measureText(str) < maxWidth);

        return size;
    }
}