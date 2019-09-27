package com.renard.wechat.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import simcpux.Constants;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public class WeChatPayInterface extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    private WxPayEventListener wxPayEventListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
//		setContentView(R.layout.load_layout);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e("nonsense", "onPayFinish, errCode = ");
    }

    @Override
    public void onResp(BaseResp resp) {

        Log.e("nonsense", "onPayFinish, errCode = " + resp.errCode);
        Log.e("zgscwjm", "errcode:" + resp.errCode);
        switch (resp.errCode) {
            case 0:

//			CommonValue.commodityPayActivity.wxpayresult(0);

                break;
            case -1:
//			CommonValue.commodityPayActivity.wxpayresult(-1);

                break;
            case -2:
//			CommonValue.commodityPayActivity.wxpayresult(-2);
                break;
            default:
                break;
        }
        wxPayEventListener.PayOver(1);
        this.finish();
    }

    public interface WxPayEventListener{
        void PayOver(int code);

        void onFailure(int cancel, String action);
    }
}
