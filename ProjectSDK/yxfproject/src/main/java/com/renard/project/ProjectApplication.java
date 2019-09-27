package com.renard.project;

import android.content.Context;
import android.content.res.Configuration;

import com.renard.initmanager.InitManager;
import com.renard.sdk.ChannelApplication;

/**
 * Created by Riven_rabbit on 2019/4/26
 *
 * @author Lemon酱
 */
public class ProjectApplication  extends ChannelApplication {
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
