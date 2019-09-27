package com.renard.common.parse.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.renard.common.Interface.CallBackListener;
import com.renard.common.Interface.LifeCycleInterface;
import com.renard.common.proguard.ProguardInterface;

import java.util.HashMap;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public abstract class Channel extends ChannelListenerImpl implements LifeCycleInterface, ProguardInterface {


    /*****************************   Channel 加载必须接口    **********************************/


    /**
     * 实例渠道插件对象，必须实现
     */
    protected abstract void initChannel();

    public ChannelList.ChannelBean channelBean;

    @Override
    public String toString() {
        return "Channel{" + "channelBean=" + channelBean + '}';
    }


    /****************************** 必须业务逻辑接口 ****************************/


    public static final String PARAMS_OAUTH_TYPE = "PARAMS_OAUTH_TYPE";
    public static final String PARAMS_OAUTH_URL = "PARAMS_OAUTH_URL";
    public static final String PARAMS_OAUTH_TOKEN = "PARAMS_OAUTH_TOKEN";


    /**
     * 返回渠道的ID(用于识别渠道)
     */
    public abstract String getChannelID();


    /**
     * 由于个别渠道只简单实现登录、支付接口，
     * 对外提供该接口给CP判断该接口是否已实现
     *
     * @param FuncType
     * @return
     */
    public abstract boolean isSupport(int FuncType);

    /**
     * 渠道SDK初始化
     */
    public abstract void init(Context context, HashMap<String, Object> initMap, CallBackListener initCallBackListener);

    /**
     * 渠道SDK登录
     */
    public abstract void login(Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener);

    /**
     * 渠道切换账号
     */
    public abstract void switchAccount(Context context, CallBackListener changeAccountCallBackLister);

    /**
     * 渠道SDK注销账号
     */
    public abstract void logout(Context context, CallBackListener logoutCallBackLister);

    /**
     * 渠道SDK支付
     */
    public abstract void pay(Context context, HashMap<String, Object> payMap, CallBackListener payCallBackListener);

    /**
     * 渠道SDK退出
     */
    public abstract void exit(Context context, CallBackListener exitCallBackLister);


    /****************************** 非必须业务逻辑接口 ****************************/


    /**
     * 返回渠道版本号
     */
    public String getChannelVersion() {
        return null;
    }

    /**
     * 渠道SDK个人中心
     */
    public void enterPlatform(Context context, CallBackListener enterPlatformCallBackLister) {
    }

    /**
     * 显示渠道SDK悬浮窗
     */
    public void showFloatView(Context context) {
    }

    /**
     * 关闭渠道SDK悬浮窗
     */
    public void dismissFloatView(Context context) {
    }

    /**
     * 渠道SDK上报数据
     */
    public void reportData(Context context, HashMap<String, Object> dataMap) {
    }

    /**
     * 横竖屏
     *
     * @return true为横屏，false为竖屏
     */
    public boolean getOrientation(Context context) {
        boolean isLandscape = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        return isLandscape;
    }

    /****************************** 非业务逻辑 生命周期接口 ****************************/

    @Override
    public void onCreate(Context context, Bundle savedInstanceState) {}

    @Override
    public void onStart(Context context) {}

    @Override
    public void onResume(Context context) {}

    @Override
    public void onPause(Context context) {}

    @Override
    public void onStop(Context context) {}

    @Override
    public void onRestart(Context context) {}

    @Override
    public void onDestroy(Context context) {}

    @Override
    public void onNewIntent(Context context, Intent intent) {}

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {}

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {}

    @Override
    public void onConfigurationChanged(Context context,Configuration configuration) {}

}