package com.renard.common.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.renard.common.Interface.LifeCycleInterface;
import com.renard.common.proguard.ProguardInterface;

/**
 * Created by Riven_rabbit on 2019/4/29
 *
 * @author Lemon酱
 */
public class Plugin implements LifeCycleInterface, ProguardInterface {

    private static final String TAG = "Plugin";

    public PluginBeanList.PluginBean pluginBean;

    private boolean hasInited;

    protected synchronized void initPlugin() {
        if (hasInited) {
            return;
        }
        hasInited = true;
    }

    @Override
    public String toString() {
        return "Plugin{" + "pluginMessage=" + pluginBean + ", hasInited=" + hasInited + '}';
    }

    /****************************************生命周期方法*********************************************/

    @Override
    public void onCreate(Context context, Bundle savedInstanceState) {

    }

    @Override
    public void onStart(Context context) {

    }

    @Override
    public void onResume(Context context) {

    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public void onStop(Context context) {

    }

    @Override
    public void onRestart(Context context) {

    }

    @Override
    public void onDestroy(Context context) {

    }

    @Override
    public void onNewIntent(Context context, Intent intent) {

    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onConfigurationChanged(Context context, Configuration configuration) {

    }
}