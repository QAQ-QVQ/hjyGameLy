package com.renard.wechat;

import android.content.Context;

import com.renard.common.Interface.CallBackListener;
import com.renard.common.plugin.Plugin;
import com.renard.common.utils.log.LogUtils;
import com.renard.wechat.pay.WeChatPayInterface;
import com.renard.wechat.pay.WechatPay;

import java.util.Map;

/**
 * Created by Riven_rabbit on 2019/4/29
 *
 * @author Lemon酱
 */
public class WechatPlugin extends Plugin {

    private String TAG = "WechatPlugin";

    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }

    /**
     * 调用微信支付接口
     */
    public static void wechatPay(Context context, String StringOrderId){
        WechatPay.getInstance().pay(context,StringOrderId);
    }


    /**
     * 调用微信登录接口
     */
    public void wechatLogin(Context context, Map<String,Object> LoginMap, CallBackListener callBackListener){

    }


    /**
     * 调用微信分享接口
     */
    public void wechatShare(Context context, Map<String,Object> ShareMap, CallBackListener callBackListener){

    }

    /**
     * 根据当前的生命周期
     * @param context
     */
    @Override
    public void onResume(Context context) {
        WechatPay.getInstance().onResume(context);
    }
}
