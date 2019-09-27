package com.renard.sdk;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.tygrm.sdk.core.TYRSDK;

/**
 * Created by Riven_rabbit on 2019/4/25
 *
 * @author Lemon酱
 */
public class ChannelApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TYRSDK.getInstance().onAppCreate(this);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        TYRSDK.getInstance().onAppAttachBaseContext(this, base);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        TYRSDK.getInstance().onAppConfigurationChanged(this, newConfig);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        TYRSDK.getInstance().onTerminate(this);
    }
}
