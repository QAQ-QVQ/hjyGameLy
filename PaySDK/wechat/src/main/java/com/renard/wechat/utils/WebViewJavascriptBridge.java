package com.renard.wechat.utils;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public interface WebViewJavascriptBridge {
    public void send(String data);
    public void send(String data, CallBackFunction responseCallback);
}
