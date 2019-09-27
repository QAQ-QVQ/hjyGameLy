package com.renard.common.parse.channel;

import android.content.Context;
import android.text.TextUtils;

import com.renard.common.google.gson.Gson;
import com.renard.common.utils.FileUtils;
import com.renard.common.utils.log.LogUtils;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public class ChannelManager {
    private static final String TAG = "ChannelManager";
    private static String CHANNEL_CONFIG = "Channel_config.txt";

    private Channel channel;
    private HashMap<String, ChannelList.ChannelBean> channelBeans = new HashMap<>();

    /********************* 同步锁双重检测机制实现单例模式（懒加载）********************/
    private volatile static ChannelManager channelManager;
    public static ChannelManager init(Context context) {
        if (channelManager == null) {
            synchronized (ChannelManager.class) {
                if (channelManager == null) {
                    channelManager = new ChannelManager(context);
                }
            }
        }
        return channelManager;
    }

    public static ChannelManager getInstance() {
        return channelManager;
    }
    /********************* 同步锁双重检测机制实现单例模式 ********************/

    private ChannelManager(Context context) {
        parse(context, CHANNEL_CONFIG);
    }

    private void parse(Context context, String pluginFilePath) {
        //从配置文件中，读取插件配置
        StringBuilder channelContent = FileUtils.readAssetsFile(context, pluginFilePath);
        String strChannelContent = String.valueOf(channelContent);

        //进行解析
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(strChannelContent)) {
            try {

                ChannelList channelList = gson.fromJson(strChannelContent, ChannelList.class);

                if (channelList.getChannel() != null && channelList.getChannel().size() != 0) {

                    //如果解析结果无误，载入到listPluginBean中去
                    for (ChannelList.ChannelBean channelBean : channelList.getChannel()) {
                        channelBeans.put(channelBean.getChannel_name(), channelBean);
                    }
                    //打印解析结果
                    LogUtils.e(TAG, CHANNEL_CONFIG +" parse: \n" + channelBeans.toString());
                } else {
                    //解析结果出错
                    LogUtils.e(TAG, CHANNEL_CONFIG + " parse error.");
                }

            } catch (Exception e) {
                //解析结果出错
                LogUtils.e(TAG, CHANNEL_CONFIG + " parse exception.");
                e.printStackTrace();
            }
        }else {
            LogUtils.e(TAG, CHANNEL_CONFIG + " parse is blank.");
        }

    }


    private boolean hasLoaded;
    private static HashMap<String, Channel> ChannelLists = new HashMap<String, Channel>();


    /**
     * 加载所有的Channel,可能存在多个渠道
     */
    public synchronized void loadAllChannels() {
        if (hasLoaded) {
            return;
        }
        HashMap<String, ChannelList.ChannelBean> entries = channelBeans;
        Set<String> set = entries.keySet();
        for (String key : set) {
            loadChannel(key);
        }
        LogUtils.e(TAG, "loadAllPlugins:" + ChannelLists.toString());
        hasLoaded = true;
    }


    /**
     * 加载一个渠道，返回的channel可能为空
     *
     * @param channelName
     * @return
     * @throws RuntimeException
     */
    private Channel loadChannel(String channelName) throws RuntimeException {

        // 1.查看从配置文件中读取的插件列表，是否存在此插件
        HashMap<String, ChannelList.ChannelBean> entries = channelBeans;
        ChannelList.ChannelBean channelBean = entries.get(channelName);
        if (channelBean == null) {
            LogUtils.e(TAG, "The channel [" +  channelName + "] does not exists in " + CHANNEL_CONFIG);
            return null;
        }
        Channel channel = null;
        // 2.调用其单例模式方法
        channel = channelBean.invokeGetInstance();
        if (channel != null) {
            // 3.反射初始化插件
            channel.initChannel();
            // 4.将已加载好的插件，添加到插件列表中去
            ChannelLists.put(channelName, channel);
        }
        return channel;
    }


    /**
     * 获取特定渠道
     * 可能为空
     *
     * @param channelName
     * @return
     */
    public Channel getChannel(String channelName) {
        if (!hasLoaded) {
            LogUtils.e(TAG, "getChannel: " + channelName + "Channel not loaded yet");
            return null;
        }
        Channel channel = null;
        HashMap<String, Channel> entries = ChannelLists;
        channel = entries.get(channelName);
        return channel;
    }



    /**
     * 当前的加载渠道SDK(只有一个渠道配置时，且渠道名为channel)
     */
    public void loadChannel(){

        ChannelList.ChannelBean channelBean = channelBeans.get("channel");
        if (channelBean == null){
            LogUtils.e(TAG, "The channel does not exists in " + CHANNEL_CONFIG);
            return;
        }

        Channel channel = null;
        channel = channelBean.invokeGetInstance();
        if (channel != null){
            channel.initChannel();
            this.channel = channel;
        }
    }

    /**
     * 返回当前渠道名为channel的渠道对象
     * @return
     */
    public Channel getChannel(){
        return channel;
    }
}
