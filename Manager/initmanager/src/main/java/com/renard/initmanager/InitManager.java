package com.renard.initmanager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.renard.common.Interface.CallBackListener;
import com.renard.common.cache.ApplicationCache;
import com.renard.common.cache.BaseCache;
import com.renard.common.cache.SDKInfoCache;
import com.renard.common.cache.SharePreferencesCache;
import com.renard.common.config.KeyConfig;
import com.renard.common.config.UrlConfig;
import com.renard.common.parse.channel.ChannelManager;
import com.renard.common.parse.plugin.PluginManager;
import com.renard.common.parse.project.ProjectManager;
import com.renard.common.utils.KLog;
import com.renard.common.utils.log.LogUtils;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public class InitManager {
    private final String TAG = getClass().getSimpleName();

    private volatile static InitManager INSTANCE;

    private InitManager() {
    }

    public static InitManager getInstance() {
        if (INSTANCE == null) {
            synchronized (InitManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new InitManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 加载SDK项目配置入口插件(这是项目最开始加载的)
     * @param context 上下文
     * @param isdebug 日志调试开关
     */
    public void initApplication(Application cxt, Context context, boolean isdebug){

        ApplicationCache.init(cxt);
        LogUtils.setDebugLogModel(isdebug);
        KLog.init(isdebug);
        ProjectManager.init(context).loadAllProjects();
        //聚合SDK加载渠道插件
        ChannelManager.init(context).loadChannel();
    }


    private static Handler sApiHandler;
    private static boolean initState = false;

    /**
     * SDK初始化逻辑
     * @param activity
     * @param callBackListener
     */
    public void init(final Activity activity, final String gameid, final String gamekey, final CallBackListener callBackListener) {

        if (sApiHandler == null) {
            HandlerThread ht = new HandlerThread("project_sdk_thread",
                    Process.THREAD_PRIORITY_BACKGROUND);
            ht.start();
            sApiHandler = new Handler(ht.getLooper());
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {

                //1、初始化全局缓存变量
                BaseCache.init(activity.getApplication());
                BaseCache.getInstance().put(KeyConfig.GAME_ID,gameid);
                BaseCache.getInstance().put(KeyConfig.GAME_KEY,gamekey);

                //2、初始化SDK参数
                SDKInfoCache.getDefault(activity.getApplication());

                //3、初始化持久化数据
                SharePreferencesCache spCache = new SharePreferencesCache(activity);
                spCache.init();
                //4、加载功能插件
                PluginManager.init(activity).loadAllPlugins();

                //5、初始化域名配置
                UrlConfig.initUrl();

                //4、开始初始化逻辑
                startInitLogic(activity,callBackListener);

            }
        };
        sApiHandler.post(r);
    }


    /**
     * 真正的初始化逻辑
     */
    private void startInitLogic(final Activity activity, final CallBackListener callBackListener){

        //-----------------------------已初始化完成--------------------------------
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setInitState(true);
                callBackListener.onSuccess(null);
            }
        });
    }

    /**
     * 初始化功能插件()
     */
    private void initFunctionPlugin(Activity activity){

        //腾讯bugly日志收集

    }


    public void setInitState(boolean state) {
        initState = state;
        //将当前状态存储到全局变量供其他模块插件使用
        BaseCache.getInstance().put(KeyConfig.IS_INIT,initState);
    }

    public boolean getInitState() {
        return initState;
    }
}
