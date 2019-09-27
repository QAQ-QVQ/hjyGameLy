package com.renard.wechat.pay;

import android.content.Context;

import com.renard.common.Interface.CallBackListener;
import com.renard.common.utils.log.LogUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

/**
 * Created by Riven_rabbit on 2019/4/29
 *
 * @author Lemon酱
 */
public class WechatPay {
    public static String TAG = "WechatPay";

    private volatile static WechatPay INSTANCE;

    private WechatPay() {
    }

    public static WechatPay getInstance() {
        if (INSTANCE == null) {
            synchronized (WechatPay.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WechatPay();
                }
            }
        }
        return INSTANCE;
    }

    private IWXAPI msgApi;
    private WXPresenter wxPresenter;
    /**
     * 微信app支付
     */
    public void pay(Context context, String StringOrderId){
        msgApi = WXAPIFactory.createWXAPI(context.getApplicationContext(), null);
        if (wxPresenter == null) {
            this.wxPresenter = new WXPresenter();
        }
        wxPresenter.payWeixin(msgApi, context, StringOrderId);
    }


    /**
     * 处理微信没有回调的问题
     * @param context
     */
    public void onResume(Context context) {
        LogUtils.debug_d(TAG,"onResume");

    }
}
