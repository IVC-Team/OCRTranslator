package com.ndanh.mytranslator.screen.history;

import com.ndanh.mytranslator.modulesimpl.HistoryDaoImp;
import com.ndanh.mytranslator.services.DAO.HistoryDao;

/**
 * Created by ndanh on 5/10/2017.
 */

public class HistoryPresenter implements HistoryContract.IHistoryPresenter {

    private HistoryDao historyDao;
    private HistoryContract.IHistoryView view;

    public HistoryPresenter(HistoryActivity view, HistoryDao historyDao) {
        this.view = view;
        view.setPresenter(this);
        this.historyDao = historyDao;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        this.view = null;
        this.historyDao = null;
    }

    @Override
    public void resume() {

    }

    @Override
    public void getHistoryRecords() {
        view.showHistory ( historyDao.getAllRecords () );
    }

    @Override
    public void deleteHistory(long[] timestamps) {
        for (int i = 0, length = timestamps.length; i < length; i++){
            historyDao.deleteRecord ( timestamps[i] );
        }
    }
}
