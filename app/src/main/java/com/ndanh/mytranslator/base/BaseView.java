package com.ndanh.mytranslator.base;

/**
 * Created by ndanh on 3/30/2017.
 */

public interface BaseView<T extends BasePresenter> {
    void initPresenter();
    void setPresenter(T presenter);
}
