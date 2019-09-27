package com.renard.project;

import android.content.Context;

import com.renard.initmanager.InitManager;
import com.renard.sdk.ChannelApplication;

public class ProjectApplication extends ChannelApplication {
    @Override
    protected void attachBaseContext(Context context) {
        //必须先加载项目配置文件，找到项目的入口。
        InitManager.getInstance().initApplication(this,context,true);
        super.attachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
