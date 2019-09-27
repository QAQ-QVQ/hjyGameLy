package com.renard.sdk;

import android.app.Application;

import com.xxx.sdk.XApplication;
import com.xxx.sdk.XPlatform;


/**
 * Created by Riven_rabbit on 2019/1/18
 */
public class ChannelApplication extends XApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        XPlatform.getInstance().onApplicationCreate(this);
    }
    public void onTerminate() {
        super.onTerminate();
        XPlatform.getInstance().onApplicationTerminate();
    }
}

