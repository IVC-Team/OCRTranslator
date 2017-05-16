package com.ndanh.mytranslator.base;

import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.screen.about.AboutActivity;
import com.ndanh.mytranslator.screen.camera.CameraActivity;
import com.ndanh.mytranslator.screen.help.HelpActivity;
import com.ndanh.mytranslator.screen.history.HistoryActivity;
import com.ndanh.mytranslator.screen.settings.SettingActivity;
import com.ndanh.mytranslator.screen.text.TextTranslatorActivity;
import com.ndanh.mytranslator.screen.voice.VoiceTranslatorActivity;

import butterknife.OnClick;

/**
 * Created by ndanh on 5/15/2017.
 */

public abstract class NavigatorFooterActivity extends BaseActivity {
    @OnClick(R.id.nav_text_screen)
    protected void transferToTextScreen(View view) {
        Intent intent = new Intent ( NavigatorFooterActivity.this, TextTranslatorActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity ( intent );
    }

    @OnClick(R.id.nav_voice_screen)
    protected void transferToVoiceScreen(View view) {
        Intent intent = new Intent ( NavigatorFooterActivity.this, VoiceTranslatorActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity ( intent );
    }

    @OnClick(R.id.nav_camera_screen)
    protected void transferToCameraScreen(View view) {
        Intent intent = new Intent ( NavigatorFooterActivity.this, CameraActivity.class );
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity ( intent );
    }

    @OnClick(R.id.nav_other_pages)
    protected void doNavOtherPage(View view) {
        PopupMenu pum = new PopupMenu(this, findViewById(view.getId()));
        pum.inflate(R.menu.nav_menu);
        pum.setOnMenuItemClickListener(popupMenuNavigatorListener);
        pum.show();
    }

    private PopupMenu.OnMenuItemClickListener popupMenuNavigatorListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent;
            switch (item.getItemId ()){
                case  R.id.action_history:
                    intent = new Intent ( NavigatorFooterActivity.this, HistoryActivity.class );
                    break;
                case  R.id.action_about:
                    intent = new Intent ( NavigatorFooterActivity.this, AboutActivity.class );
                    break;
                case  R.id.action_helps:
                    intent = new Intent ( NavigatorFooterActivity.this, HelpActivity.class );
                    break;
                case  R.id.action_settings:
                    intent = new Intent ( NavigatorFooterActivity.this, SettingActivity.class );
                    break;
                default:
                    intent = null;
                    break;
            }
            startActivity ( intent );
            return true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume ();
        invisibleView();
    }

    public abstract void invisibleView();
}
