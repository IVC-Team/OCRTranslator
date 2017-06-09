package com.ndanh.mytranslator.screen.history;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ndanh.mytranslator.R;
import com.ndanh.mytranslator.adapter.HistoryRecyclerViewApdater;
import com.ndanh.mytranslator.base.BaseActivity;
import com.ndanh.mytranslator.model.History;
import com.ndanh.mytranslator.modulesimpl.HistoryDaoImp;
import com.ndanh.mytranslator.util.DialogHelper;
import com.ndanh.mytranslator.util.PermissionHelper;
import com.ndanh.mytranslator.util.SimpleSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryActivity extends BaseActivity implements HistoryContract.IHistoryView , Observer {
    @BindView(R.id.lst_history)
    RecyclerView recyclerView;
    @BindView(R.id.panel_delete)
    LinearLayout panel_delete;

    private HistoryRecyclerViewApdater adapter;
    private LinearLayoutManager layoutManager;
    private HistoryContract.IHistoryPresenter presenter;
    private DeleteProcessListener deleteProcessListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        DeleteMode.getInstance ().addObserver ( HistoryActivity.this );
        PermissionHelper.requestPermission ( this, Manifest.permission.WRITE_EXTERNAL_STORAGE );
    }

    public void initView(){
        adapter = new HistoryRecyclerViewApdater(getApplicationContext());
        adapter.addItems ( new ArrayList<HistoryRecyclerViewApdater.HistoryView> (  ) );
        deleteProcessListener = adapter;
        layoutManager = new LinearLayoutManager (this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart ();
        this.presenter.start ();
    }

    @Override
    protected void onResume() {
        super.onResume ();
        this.presenter.resume ();
        initView();
        this.presenter.getHistoryRecords ();
    }

    @Override
    protected void onPause() {
        super.onPause ();
        this.presenter.pause ();
        DeleteMode.getInstance ().off ();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        this.presenter.stop ();
        this.presenter = null;
        adapter = null;
    }

    @Override
    public void initPresenter() {
        new HistoryPresenter (this);
    }

    @Override
    public void setPresenter(HistoryContract.IHistoryPresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void showHistory(List<History> lstHistories) {
        List<HistoryRecyclerViewApdater.HistoryView> lstHis = new ArrayList<HistoryRecyclerViewApdater.HistoryView> (  );
        Iterator<History> it = lstHistories.iterator();
        while (it.hasNext()) {
            lstHis.add ( new HistoryRecyclerViewApdater.HistoryView ( it.next () ) );
        }
        this.adapter.addItems(lstHis);
    }

    @OnClick(R.id.garbage)
    public void doDelete(View v){
        DialogHelper.confirm ( HistoryActivity.this, getString( R.string.history_message_confirm_delete_history), new DialogHelper.OnDialogListener () {
            @Override
            public void onAccept() {
                deleteHistory();
            }
        } );
    }

    private void deleteHistory(){
        long[] lstDelete = deleteProcessListener.beforeDelete();
        this.presenter.deleteHistory ( lstDelete );
        DeleteMode.getInstance ().off ();
        this.presenter.getHistoryRecords ();
    }

    @OnClick(R.id.action_back)
    public void back(View v){
        finish ();
    }

    @OnClick(R.id.checkbox)
    public void selectAll(View v){
        deleteProcessListener.onSelectAll ();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(DeleteMode.getInstance ().isDeleteMode ()){
            panel_delete.setVisibility ( View.VISIBLE );
        } else {
            panel_delete.setVisibility ( View.GONE );
        }
    }

    public interface DeleteProcessListener{
        void onSelectAll();
        long[] beforeDelete();
    }

}
