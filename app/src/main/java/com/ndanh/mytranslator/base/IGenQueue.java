package com.ndanh.mytranslator.base;

/**
 * Created by ndanh on 4/19/2017.
 */

public interface IGenQueue<T> {
    void enQueue(T t);
    T deQueue();
}
