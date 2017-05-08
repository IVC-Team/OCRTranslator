package com.ndanh.mytranslator.services;

/**
 * Created by ndanh on 4/18/2017.
 */

public abstract class ModuleManager implements IModuleManage {
    protected static IModuleManage manager;

    public static IModuleManage getInstance(){
        return manager;
    };
}
