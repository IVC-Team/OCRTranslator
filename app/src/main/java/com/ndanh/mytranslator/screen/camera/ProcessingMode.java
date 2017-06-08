package com.ndanh.mytranslator.screen.camera;

import java.util.Observable;

/**
 * Created by dauda on 08/06/2017.
 */

public class ProcessingMode extends Observable {

    private boolean m_processMode;

    public ProcessingMode() {
        this.m_processMode = false;
    }

    public void off(){
        m_processMode = false;
        this.setChanged ();
        this.notifyObservers ();
    }
    public void on(){
        m_processMode = true;
        this.setChanged ();
        this.notifyObservers ();
    }

    public boolean isProcessingMode(){
        return m_processMode;
    }
}