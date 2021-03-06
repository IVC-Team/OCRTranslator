package com.ndanh.mytranslator.screen.camera;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.base.NavigatorFooterActivity;
import com.ndanh.mytranslator.model.DetectResult;
import com.ndanh.mytranslator.model.Language;
import com.ndanh.mytranslator.util.PermissionHelper;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraActivity extends NavigatorFooterActivity
        implements SurfaceHolder.Callback, CameraContract.ICameraView ,Observer {

    //region variable View
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private @IdRes int selectedButton;
    @BindView ( R.id.cameraView ) SurfaceView surfaceView;
    @BindView((R.id.preview)) RelativeLayout cameraView;
    @BindView ( R.id.navigation_footer_camera ) RelativeLayout hiddenPanel;
    @BindView ( R.id.btn_takeButton ) ImageView btnTakeButton;
    @BindView ( R.id.ar_mask ) ImageView arMask;
    @BindView(R.id.action_choose_source) Button chooseSourceLang;
    @BindView(R.id.action_choose_dest) Button chooseDestLang;
    @BindView(R.id.panel_progress_bar) LinearLayout panelProgressBar;
    //endregion

    //region variable logic
    private PreviewMode previewController;
    private ProcessingMode processingController;
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
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap ,cameraView.getWidth (),cameraView.getHeight (),true);
            presenter.doTranslate(scaledBitmap);
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
        PermissionHelper.requestPermission ( this, Manifest.permission.INTERNET );
    }

    @Override
    public void onResume() {
        super.onResume();
        this.presenter.resume ();
        initView();
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
        previewController.deleteObserver(this);
        processingController.deleteObserver(this);
        previewController = null;
        processingController = null;
        this.presenter.pause ();
    }

    //endregion

    //region CameraContract CameraView implement

    @Override
    public void initPresenter() {
        new CameraPresenter(this);
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
        arMask.setImageBitmap ( drawTextToBitmap(width, height, result, getDestLang ()) );
        processingController.off();
        arMask.bringToFront();
    }



    @Override
    public void showMessage(String msg) {
        processingController.off();
        Toast.makeText ( getApplicationContext (),  msg , Toast.LENGTH_SHORT ).show ();
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
                orientation = 90;
            }

            if(display.getRotation() == Surface.ROTATION_90)
            {
                orientation = 0;
            }

            if(display.getRotation() == Surface.ROTATION_180)
            {
            }

            if(display.getRotation() == Surface.ROTATION_270)
            {
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
        if(previewController.isPreviewMode()){
            processingController.on();
            camera.takePicture(null, null, captureImageCallback);
            previewController.off();
        }else{
            previewController.on();
            refreshCamera();
        }
    }

    public void initView(){
        previewController = new PreviewMode();
        previewController.addObserver(this);
        processingController = new ProcessingMode();
        processingController.addObserver(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        srcLang = Language.ELanguage.ENG;
        destLang = Language.ELanguage.JAP;
        chooseSourceLang.setText(Language.getLongLanguage ( srcLang ));
        chooseDestLang.setText(Language.getLongLanguage ( destLang ));
    }

    public Bitmap drawTextToBitmap(int width, int height , List<DetectResult> result, Language.ELanguage eLanguage) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor( getApplicationContext() ,R.color.transparent));
        canvas.drawRect(0F, 0F, (float) width, (float) height, paint);

        paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(ContextCompat.getColor( getApplicationContext() ,R.color.colorTextAR));
        paint.setStyle(Paint.Style.FILL);

        int textSize;
        for (DetectResult item: result) {
            textSize = determineMaxTextSize(item.getTranslatedText() , item.getPosition().width(), item.getPosition().height () * 0.7f );
            paint.setTextSize(textSize);
            canvas.drawText(item.getTranslatedText() , item.getPosition ().left, item.getPosition ().bottom - (item.getPosition ().height () * 0.15f ) ,paint);
        }
        return bitmap;
    }

    private int determineMaxTextSize(String str, float maxWidth, float maxHeight) {
        if(str == "") return 0;
        int size = 0;

        int maxSize = 400 , minSize = 0;
        Paint paint = new Paint();

        paint.setTextSize(maxSize);
        if(paint.measureText(str) == 0){
            return 0;
        }
        while (paint.measureText(str) < maxWidth) {
            minSize = maxSize;
            maxSize *= 2;
            paint.setTextSize(maxSize);
        };
        int guessSize = (maxSize - minSize) / 2;
        while (true){
            paint.setTextSize(guessSize);
            if(paint.measureText(str) == maxWidth){
                size = guessSize;
                break;
            }
            else if(paint.measureText(str) > maxWidth){
                maxSize = guessSize;
                guessSize = (maxSize -minSize)/2  + minSize;
            }
            else{
                minSize = guessSize;
                guessSize = ((maxSize -minSize) / 2) + minSize;
            }
            if((maxSize - minSize) <= 1){
                size = maxSize;
                break;
            }
        }

        Rect bounds = new Rect();
        maxSize = size;
        minSize = maxSize / 2;

        paint.setTextSize(maxSize);
        paint.getTextBounds(str, 0, str.length(), bounds);
        if(bounds.height () < maxHeight)
            return size;

        while (true){
            paint.setTextSize(maxSize - minSize);
            paint.getTextBounds(str, 0, str.length(), bounds);
            if(minSize <= 1 ){
                size = maxSize;
                break;
            }
            if(bounds.height () > maxHeight){
                maxSize = maxSize - minSize;
                minSize = maxSize / 2;
            }
            else{
                minSize = minSize / 2;
            }

        }
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
            Button combobox = (Button) findViewById( selectedButton );
            switch (selectedButton){
                case R.id.action_choose_source:
                    Language.ELanguage temp = Language.setLangFromMenu(item.getItemId ());
                    if(temp == srcLang ) break;
                    srcLang = temp;
                    combobox.setText(Language.getLongLanguage ( srcLang ));
                    break;
                case R.id.action_choose_dest:
                    destLang = Language.setLangFromMenu(item.getItemId ());
                    combobox.setText(Language.getLongLanguage ( destLang ));
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

    @OnTextChanged(R.id.action_choose_dest)
    protected void onDestLangChanged(CharSequence text) {
        if(!previewController.isPreviewMode())
            this.presenter.changeDestLanguage ();
    }
    //endregion

    //region Observer implement
    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof PreviewMode){
            if(previewController.isPreviewMode()){
                surfaceView.bringToFront();
            } else {

            }
        } else if (o instanceof ProcessingMode){
            if(processingController.isProcessingMode()){
                panelProgressBar.setVisibility(View.VISIBLE);
                panelProgressBar.bringToFront();
                btnTakeButton.setClickable(false);
            } else {
                panelProgressBar.setVisibility(View.GONE);
                btnTakeButton.setClickable(true);
            }
        }

    }
    //endregion
}