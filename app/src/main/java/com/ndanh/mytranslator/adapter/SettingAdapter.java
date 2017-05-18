package com.ndanh.mytranslator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.model.Setting;
import com.ndanh.mytranslator.util.DialogHelper;

import java.util.List;

/**
 * Created by ndanh on 5/15/2017.
 */

public class SettingAdapter extends BaseAdapter {

    private final Context context;
    private List<Setting> lstSetting;
    private OnItemClickListener itemClickListener;
    public SettingAdapter( final Context context ,List<Setting> lstSetting, OnItemClickListener itemClickListener) {
        this.context = context;
        this.lstSetting = lstSetting;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getCount() {
        return lstSetting.size ();
    }

    @Override
    public Setting getItem(int position) {
        return lstSetting.get ( position ) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate( R.layout.setting_item, parent, false);
        ImageView imgIcon = (ImageView) rowView.findViewById(R.id.setting_icon);
        TextView tvTextSetting = (TextView) rowView.findViewById(R.id.setting_text);
        ImageView imgCheckbox = (ImageView) rowView.findViewById(R.id.setting_checkbox);
        tvTextSetting.setText(context.getString ( lstSetting.get ( position ).getTextSetting () ));
        imgIcon.setImageResource (lstSetting.get ( position ).getIconSetting ());
        imgCheckbox.setImageResource (lstSetting.get ( position ).getCheckBoxSetting ());
        if(lstSetting.get ( position ).isSet ()){
            imgCheckbox.setVisibility ( View.VISIBLE );
        } else{
            imgCheckbox.setVisibility ( View.GONE );
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onSelect ( lstSetting.get ( position ) );
                }
            });
        }
        return rowView;
    }

    public interface OnItemClickListener {
        void onSelect(Setting setting);
    }

    public void changeStartMode(Setting setting) {
        for(Setting item : lstSetting){
            item.setSet ( false );
        }
        setting.setSet ( true );
        Setting.saveScreenMode ( setting.getTextSetting () );
        notifyDataSetChanged ();
    }

}
