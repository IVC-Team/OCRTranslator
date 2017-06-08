package com.ndanh.mytranslator.screen.camera;

import java.util.Observable;

/**
 * Created by dauda on 08/06/2017.
 */

public class PreviewMode  extends Observable {

    private boolean m_isPreviewMode;

    public PreviewMode() {
        this.m_isPreviewMode = true;
    }

    public void off(){
        m_isPreviewMode = false;
        this.setChanged ();
        this.notifyObservers ();
    }
    public void on(){
        m_isPreviewMode = true;
        this.setChanged ();
        this.notifyObservers ();
    }

    public boolean isPreviewMode(){
        return m_isPreviewMode;
    }
}