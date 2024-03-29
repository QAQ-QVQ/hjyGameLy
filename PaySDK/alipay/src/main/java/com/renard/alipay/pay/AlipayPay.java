package com.renard.alipay.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.renard.common.Interface.CallBackListener;
import com.renard.common.utils.ErrorCode;
import com.renard.common.utils.log.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by Riven_rabbit on 2019/4/29
 *
 * @author Lemon酱
 */
public class AlipayPay {
    public String TAG = "AlipayPay";

    private volatile static AlipayPay INSTANCE;

    private AlipayPay() {
    }

    public static AlipayPay getInstance() {
        if (INSTANCE == null) {
            synchronized (AlipayPay.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AlipayPay();
                }
            }
        }
        return INSTANCE;
    }

    private static final int SDK_PAY_FLAG = 1;

    private AliPayInterface paycallback;

    /**
     * 支付包app支付
     */
    public void pay(final Context context, String orderString, AliPayInterface callBackListener) {

        paycallback = callBackListener;

        //日志打印下,不传如支付订单里
        try {
            LogUtils.d(TAG, "alipayOrderInfo" + URLDecoder.decode(orderString, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String orderInfo = orderString;
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) context);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        paycallback.PayOver(1);

                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        paycallback.onFailure(ErrorCode.CANCEL, "pay cancel");

                    } else {

                        paycallback.onFailure(ErrorCode.FAILURE, "pay fail");
                    }
                    break;
                }
            }
        }
    };
}
