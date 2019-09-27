package com.renard.common.parse.plugin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import com.renard.common.google.gson.Gson;
import com.renard.common.utils.FileUtils;
import com.renard.common.utils.KLog;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public class PluginManager extends PluginReflectApi{

    private static final String TAG = "PluginManager";
    public static String PLUGIN_CONFIG = "Plugin_config.txt";


    private static HashMap<String, PluginList.PluginBean> PluginBeans = new HashMap<>();

    /********************* 同步锁双重检测机制实现单例模式（懒加载）********************/
    private volatile static PluginManager pluginManager;
    public static PluginManager init(Context context) {
        if (pluginManager == null) {
            synchronized (PluginManager.class) {
                if (pluginManager == null) {
                    pluginManager = new PluginManager(context);
                }
            }
        }
        return pluginManager;
    }

    public static PluginManager getInstance(){
        return pluginManager;
    }
    /********************* 同步锁双重检测机制实现单例模式（懒加载）********************/


    private PluginManager(Context context) {
        parse(context, PLUGIN_CONFIG);
    }

    /**
     * 读取配置文件
     * @param context
     * @param pluginFilePath
     */
    private void parse(Context context, String pluginFilePath) {
        //从配置文件中，读取插件配置
        StringBuilder pluginContent = FileUtils.readAssetsFile(context, pluginFilePath);
        String strPluginContent = String.valueOf(pluginContent);

        //进行解析
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(strPluginContent)) {
            try {

                PluginList pluginList = gson.fromJson(strPluginContent, PluginList.class);
                if (pluginList.getPlugin() != null && pluginList.getPlugin().size() != 0) {
                    //如果解析结果无误，载入到listPluginBean中去
                    for (PluginList.PluginBean projectBean : pluginList.getPlugin()) {
                        PluginBeans.put(projectBean.getPlugin_name(), projectBean);
                    }
                    //打印解析结果
                    KLog.i(TAG, PLUGIN_CONFIG +" parse: \n" + PluginBeans.toString());
                } else {
                    //解析结果出错
                    KLog.e(TAG, PLUGIN_CONFIG + " parse error.");
                }

            } catch (Exception e) {
                //解析结果出错
                KLog.e(TAG, PLUGIN_CONFIG + " parse exception.");
                e.printStackTrace();
            }
        }else {

            KLog.e(TAG, PLUGIN_CONFIG + " parse is blank");
        }
    }

    private boolean hasLoaded;
    private static HashMap<String, Plugin> PluginLists = new HashMap<String, Plugin>();

    /**
     * 加载所有的插件
     */
    public synchronized void loadAllPlugins() {
        if (hasLoaded) {
            return;
        }
        HashMap<String, PluginList.PluginBean> entries = PluginBeans;
        Set<String> set = entries.keySet();
        for (String key : set) {
            loadPlugin(key);
        }
        KLog.i(TAG, "loadAllPlugins:" + PluginLists.toString());
        hasLoaded = true;
    }

    /**
     * 加载一个插件，返回的插件可能为空
     *
     * @param pluginName
     * @return
     * @throws RuntimeException
     */
    private Plugin loadPlugin(String pluginName) throws RuntimeException {

        // 1.查看从配置文件中读取的插件列表，是否存在此插件
        HashMap<String, PluginList.PluginBean> entries = PluginBeans;
        PluginList.PluginBean pluginBean = entries.get(pluginName);
        if (pluginBean == null) {
            KLog.i(TAG, "The plugin [" +  pluginName + "] does not exists in " + PLUGIN_CONFIG);
            return null;
        }
        Plugin Plugin = null;
        // 2.调用其单例模式方法
        Plugin = pluginBean.invokeGetInstance();
        if (Plugin != null) {
            // 3.反射初始化插件
            Plugin.initPlugin();
            // 4.将已加载好的插件，添加到插件列表中去
            PluginLists.put(pluginName, Plugin);
        }
        return Plugin;
    }

    /**
     * 获取特定插件
     * 可能为空
     *
     * @param pluginName
     * @return
     */
    public Plugin getPlugin(String pluginName) {
        if (!hasLoaded) {
            KLog.i(TAG, "getPlugin: " + pluginName + "Plugin not loaded yet");
            return null;
        }
        Plugin p = null;
        HashMap<String, Plugin> entries = PluginLists;
        p = entries.get(pluginName);
        return p;
    }

    /******************************* 生命周期接口 *********************************/

    public void onCreate(Context context, Bundle savedInstanceState) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onCreate(context,savedInstanceState);
        }
    }

    public void onStart(Context context) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onStart(context);
        }
    }

    public void onResume(Context context) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onResume(context);
        }
    }

    public void onPause(Context context) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onPause(context);
        }
    }

    public void onStop(Context context) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onStop(context);
        }
    }

    public void onRestart(Context context) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onRestart(context);
        }
    }

    public void onDestroy(Context context) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onDestroy(context);
        }
    }

    public void onNewIntent(Context context, Intent intent) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onNewIntent(context,intent);
        }
    }

    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onActivityResult(context, requestCode, resultCode, data);
        }
    }

    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onRequestPermissionsResult(context, requestCode, permissions, grantResults);
        }
    }
    public void onConfigurationChanged(Context context, Configuration config) {
        Set<String> set = PluginLists.keySet();
        for (String key : set) {
            Plugin plugin = PluginLists.get(key);
            plugin.onConfigurationChanged(context, config);
        }
    }
}