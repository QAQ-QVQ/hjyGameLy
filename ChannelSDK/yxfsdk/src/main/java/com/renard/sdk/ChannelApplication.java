package com.renard.sdk;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.yaoyue.release.YYReleaseSDK;
import com.yaoyue.release.YYSDKApplication;


/**
 * Created by Riven_rabbit on 2019/4/25
 *
 * @author Lemon酱
 */
public class ChannelApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        YYReleaseSDK.getInstance().onAppCreate(this);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        YYReleaseSDK.getInstance().onAppAttachBaseContext(base, this);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        YYReleaseSDK.getInstance().onAppConfigurationChanged(newConfig, this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        YYReleaseSDK.getInstance().onAppTerminate(this);
    }
}
