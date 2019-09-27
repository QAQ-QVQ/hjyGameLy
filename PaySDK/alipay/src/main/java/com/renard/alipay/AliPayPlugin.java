package com.renard.alipay;

import android.content.Context;

import com.renard.alipay.pay.AliPayInterface;
import com.renard.alipay.pay.AlipayPay;
import com.renard.common.Interface.CallBackListener;
import com.renard.common.plugin.Plugin;
import com.renard.common.utils.log.LogUtils;

import java.util.Map;

/**
 * Created by Riven_rabbit on 2019/4/29
 *
 * @author Lemon酱
 */
public class AliPayPlugin extends Plugin {

    private String TAG = "AlipayPlugin";

    @Override
    protected synchronized void initPlugin() {
        super.initPlugin();
        LogUtils.d(TAG,"init " + getClass().getSimpleName());
    }

    /**
     * 调用支付宝支付app接口
     */
    public static void alipay(Context context, String orderString, AliPayInterface callBackListener){
        AlipayPay.getInstance().pay(context,orderString,callBackListener);
    }

}
