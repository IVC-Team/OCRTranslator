package com.ndanh.mytranslator.screen.camera;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.base.NavigatorFooterActivity;
import com.ndanh.mytranslator.model.DetectResult;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.util.CToast;
import com.ndanh.mytranslator.util.PermissionHelper;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import java.io.IOException;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends NavigatorFooterActivity
        implements SurfaceHolder.Callback, CameraContract.ICameraView {

    //region variable View
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private @IdRes int selectedButton;
    @BindView ( R.id.cameraView ) SurfaceView surfaceView;
    @BindView ( R.id.tranlate_result ) ImageView rawCapture;
    @BindView((R.id.preview)) RelativeLayout cameraView;
    @BindView ( R.id.navigation_footer_camera ) RelativeLayout hiddenPanel;
    @BindView ( R.id.btn_takeButton ) ImageView btnTakeButton;
    @BindView ( R.id.ar_mask ) ImageView arMask;
    @BindView(R.id.action_choose_source) Button chooseSourceLang;
    @BindView(R.id.action_choose_dest) Button chooseDestLang;

    //endregion

    //region variable logic
    private boolean previewMode = true;
    private static final String TAG = "OcrCaptureActivity";
    private int orientation = 90;
    private CameraContract.ICameraPresenter presenter;
    private Language.ELanguage srcLang, destLang;

    private PictureCallback captureImageCallback = new PictureCallback()
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bmp , 0, 0, bmp .getWidth(), bmp .getHeight(), matrix, true);
//            Bitmap scaledBitmap = BitmapFactory.decodeResource (getResources (), R.drawable.capture);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap ,cameraView.getWidth (),cameraView.getHeight (),true);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        this.presenter.resume ();}

    @Override
    protected void onStart() {
        super.onStart();
        initView();
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
        return srcLang;
    }

    @Override
    public Language.ELanguage getDestLang() {
        return destLang;
    }

    @Override
    public void displayResult(List<DetectResult> result, int width, int height) {
        CToast.showMessage ( "Start draw Bitmap" );
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor( getApplicationContext() ,R.color.transparent));
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);

        drawTextToBitmap(bitmap , result);
        arMask.setImageBitmap ( bitmap );
        arMask.bringToFront ();
        CToast.showMessage ( "Finish draw Bitmap" );
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText ( getApplicationContext (),  msg , Toast.LENGTH_LONG ).show ();
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
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
    //endregion

    //region Method

    private void refreshCamera() {
        if(camera == null){
            try {
                camera = Camera.open();
            }
            catch(RuntimeException e) {
                System.err.println(e);
            }
            Camera.Parameters param = camera.getParameters();
            Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

            if(display.getRotation() == Surface.ROTATION_0)
            {
                CToast.showMessage ("Rotation: " + display.getRotation()  );
                orientation = 90;
            }

            if(display.getRotation() == Surface.ROTATION_90)
            {
                CToast.showMessage ("Rotation: " + display.getRotation()  );
                orientation = 0;
            }

            if(display.getRotation() == Surface.ROTATION_180)
            {
                CToast.showMessage ("Rotation: " + display.getRotation()  );
            }

            if(display.getRotation() == Surface.ROTATION_270)
            {
                CToast.showMessage ("Rotation: " + display.getRotation()  );
                orientation = 180;
            }

            List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
            List<Camera.Size> pictureSizes = param.getSupportedPictureSizes ();
            param.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
            param.setPictureSize ( previewSizes.get(0).width, previewSizes.get(0).height );
            camera.setDisplayOrientation(orientation);

            if (param.getSupportedFocusModes().contains (
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            camera.setParameters(param);

            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            }
            catch(Exception e) {
                System.err.println(e);
                return;
            }
        }

        if(surfaceHolder.getSurface() == null) return;

        try {
            camera.stopPreview();
        }
        catch(Exception e) {}

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch(Exception e) {}
    }

    public void captureImage(View view) throws IOException {
        if(previewMode){
            btnTakeButton.setClickable ( false );
            camera.takePicture(null, null, captureImageCallback);
            previewMode = false;
        }else{
            previewMode = true;
            surfaceView.bringToFront ();
        }
    }

    private void initView(){
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceView.bringToFront ();
        srcLang = Language.ELanguage.ENG;
        destLang = Language.ELanguage.JAP;
        chooseSourceLang.setText(Language.getLongLanguage ( srcLang ));
        chooseDestLang.setText(Language.getLongLanguage ( destLang ));
    }

    private void drawTextToBitmap(Bitmap bitmap, List<DetectResult> result) {
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        for (DetectResult item: result) {
            int textSize = determineMaxTextSize(item.getText() , item.getPosition().width(), item.getPosition().height ());
            paint.setTextSize(textSize);
            canvas.drawText(item.getText() , item.getPosition ().left,(item.getPosition ().bottom - textSize/4 ) ,paint);
        }
    }

    private int determineMaxTextSize(String str, float maxWidth, float maxHeight) {
        int size = 0;
        Paint paint = new Paint();
        do {
            paint.setTextSize(++ size);
        } while(paint.measureText(str) < maxWidth);

        Rect bounds = new Rect();

        do {
            paint.getTextBounds("a", 0, 1, bounds);
            paint.setTextSize(-- size);
        } while(bounds.height () > maxHeight);

        return size;
    }

    //endregion

    //region View callback
    @OnClick(R.id.action_change)
    public void changeLanguage(View v){
        //Change Language
        Language.ELanguage tempLang = srcLang;
        srcLang = destLang ;
        destLang = tempLang ;

        chooseSourceLang.setText(Language.getLongLanguage ( srcLang ));
        chooseDestLang.setText(Language.getLongLanguage ( destLang ));
    }

    private PopupMenu.OnMenuItemClickListener popupMenuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Button a = (Button) findViewById( selectedButton );
            switch (selectedButton){
                case R.id.action_choose_source:
                    Language.ELanguage temp = Language.setLangFromMenu(item.getItemId ());
                    if(temp == srcLang ) break;
                    srcLang = temp;
                    a.setText(Language.getLongLanguage ( srcLang ));
                    break;
                case R.id.action_choose_dest:
                    destLang = Language.setLangFromMenu(item.getItemId ());
                    a.setText(Language.getLongLanguage ( destLang ));
                    break;
                default:
                    break;
            };
            return true;
        }
    };

    @OnClick({R.id.action_choose_source,R.id.action_choose_dest})
    public void chooseLanguage(View view){
        selectedButton = view.getId();
        PopupMenu pum = new PopupMenu(this, findViewById(view.getId()));
        pum.inflate(R.menu.lang_choose_popup);
        pum.setOnMenuItemClickListener(popupMenuListener);
        pum.show();
    }
    @OnTextChanged(R.id.action_choose_source)
    protected void onTextChanged(CharSequence text) {
        this.presenter.changeSrcLanguage ();
    }

    //endregion
}