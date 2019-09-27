package com.renard.sdk;

import android.app.Application;


import com.renard.hjyGameSs.utils.KLog;
import com.renard.hjyGameSs.utils.SPDataUtils;
import com.renard.hjyGameSs.utils.Utils;
import com.renard.hjyGameSs.utils.WalleChannelReader;


/**
 * Created by Riven_rabbit on 2019/1/18
 */
public class ChannelApplication extends Application {
    private static ChannelApplication Application;

    @Override
    public void onCreate() {
        super.onCreate();
        Application = this;

        //初始化工具类
        Utils.init(Application);


        //V1分包获取渠道
//            String channel=ChannelUtil.getChannel(this, "default channel");//获取渠道名
        //V2分包获取渠道
        String channel = WalleChannelReader.getChannel(this);
        if (channel != null) {

        }
        //初始化SharedPreferences
        SPDataUtils.init(getApplicationContext());
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
    }
}

