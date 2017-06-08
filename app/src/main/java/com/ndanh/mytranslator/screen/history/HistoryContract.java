package com.ndanh.mytranslator.screen.history;

import android.content.Context;

import com.ndanh.mytranslator.base.BasePresenter;
import com.ndanh.mytranslator.base.BaseView;
import com.ndanh.mytranslator.model.History;

import java.util.List;

/**
 * Created by ndanh on 4/18/2017.
 */

public interface HistoryContract {
    interface IHistoryView extends BaseView<IHistoryPresenter>{
        Context getApplicationContext();
        void showHistory(List<History> lstHistories);
    }
    interface IHistoryPresenter extends BasePresenter{
        void getHistoryRecords();
        void deleteHistory(long[] timestamps);
    }
}
