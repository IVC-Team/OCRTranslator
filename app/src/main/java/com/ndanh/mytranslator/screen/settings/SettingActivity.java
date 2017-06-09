package com.ndanh.mytranslator.screen.settings;

import android.Manifest;
import android.os.Bundle;
import android.widget.ListView;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.adapter.SettingAdapter;
import com.ndanh.mytranslator.base.BaseActivity;
import com.ndanh.mytranslator.model.Setting;
import com.ndanh.mytranslator.util.PermissionHelper;
import com.ndanh.mytranslator.util.SimpleSQLiteOpenHelper;
import com.ndanh.mytranslator.util.DialogHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements SettingAdapter.OnItemClickListener {
    @BindView ( R.id.lst_settings )
    ListView lstSetting;
    private SettingAdapter adapter;


    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_setting );
        ButterKnife.bind ( this );
        this.adapter =  new SettingAdapter ( getApplicationContext (), getListSetting(), this );
        lstSetting.setAdapter ( adapter );
        PermissionHelper.requestPermission ( this, Manifest.permission.WRITE_EXTERNAL_STORAGE );
    }

    @OnClick( R.id.action_clear )
    public void deleteDataBase(){
        DialogHelper.confirm ( SettingActivity.this, getString( R.string.setting_message_confirm_delete_history), new DialogHelper.OnDialogListener () {
            @Override
            public void onAccept() {
                SettingActivity.this.deleteDatabase ( SimpleSQLiteOpenHelper.DATABASE_NAME );
            }
        } );

    }

    @OnClick( R.id.action_back )
    public void back(){
        finish ();
    }

    private List<Setting> getListSetting(){
        List<Setting> lstSetting = new ArrayList<Setting> (  );
        Setting setting = new Setting ( R.drawable.camera, R.string.setting_camera_action, R.drawable.checked );
        lstSetting.add ( setting );
        setting = new Setting ( R.drawable.keyboard, R.string.setting_keyboard_action, R.drawable.checked );
        lstSetting.add ( setting );
        setting = new Setting ( R.drawable.voice, R.string.setting_voice_action, R.drawable.checked );
        lstSetting.add ( setting );
        return lstSetting;
    }

    @Override
    public void onSelect(final Setting setting) {
        DialogHelper.confirm ( SettingActivity.this, getString( R.string.setting_message_confirm_change_start_mode), new DialogHelper.OnDialogListener () {
            @Override
            public void onAccept() {
                adapter.changeStartMode ( setting );
            }
        } );
    }
}
