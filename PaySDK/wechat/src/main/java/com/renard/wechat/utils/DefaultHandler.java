package com.renard.wechat.utils;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public class DefaultHandler implements BridgeHandler{
    String TAG = "DefaultHandler";

    @Override
    public void handler(String data, CallBackFunction function) {
        if(function != null){
            function.onCallBack("DefaultHandler response data");
        }
    }
}
