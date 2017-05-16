package com.ndanh.mytranslator.screen.camera;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.base.NavigatorFooterActivity;
import com.ndanh.mytranslator.modulesimpl.ModuleManageImpl;
import com.ndanh.mytranslator.util.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class CameraActivity extends NavigatorFooterActivity implements CameraContract.ICameraView {

    @BindView(R.id.tranlate_result)
    ImageView imageViewResult;
    @BindView((R.id.preview))
    LinearLayout cameraView;
    @BindView ( R.id.navigation_footer_camera )
    RelativeLayout hiddenPanel;


    private static final String TAG = "OcrCaptureActivity";
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraContract.ICameraPresenter presenter;



    //    -------------------------- Activity methods override implement ----------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        PermissionHelper.requestPermission ( this, Manifest.permission.CAMERA );
        PermissionHelper.requestPermission ( this, Manifest.permission.WRITE_EXTERNAL_STORAGE );
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

//    -------------------------- CameraContract CameraView implement ----------------------------

    @Override
    public void initPresenter() {
        new CameraPresenter(this, ModuleManageImpl.getInstance().getTextDetect(), ModuleManageImpl.getInstance().getTranslateModule());
    }

    @Override
    public void setPresenter( CameraContract.ICameraPresenter presenter) {
        this.presenter = presenter;
    }

    //    -------------------------- Private methods ----------------------------




    //    -------------------------- Inner Class ----------------------------

    @Override
    public void invisibleView() {
        hiddenPanel.setVisibility ( View.GONE );
    }
}
