package com.renard.wechat.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.renard.common.utils.ResourceUtil;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public class DialogWeb extends DialogBase implements View.OnClickListener {
    private BridgeWebView webView;
    private ProgressBar progressBar;
    private String mUrl, method;
    public static final int TYPE_FULL = 1, TYPE_DIALOG = 2;//1:全屏 2：弹框
    private int mViewType;

    public DialogWeb(Context context, String url) {
        super(context, ResourceUtil.getStyleResIDByName(context, "loginsdk_style_dialog"));
        mContext = context;
        mUrl = url;
        method = null;
        mViewType = TYPE_DIALOG;
    }

    /**
     * @param context
     * @param url
     * @param method  get post
     */
    public DialogWeb(Context context, String url, String method) {
        super(context, ResourceUtil.getStyleResIDByName(context, "loginsdk_style_dialog"));
        mContext = context;
        mUrl = url;
        this.method = method;
        mViewType = TYPE_DIALOG;
    }

    public DialogWeb(Context context, String url, int viewType) {
        super(context, ResourceUtil.getStyleResIDByName(context, "loginsdk_style_dialog"));
        mContext = context;
        mUrl = url;
        mViewType = viewType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(ResourceUtil.getLayoutResIDByName(mContext, "web"));
        webView = (BridgeWebView) findViewById(ResourceUtil.getIdResIDByName(mContext, "webView"));
        progressBar = (ProgressBar) findViewById(ResourceUtil.getIdResIDByName(mContext, "progressBar"));
        int d = DensityUtils.dip2px(mContext, 1);
        LinearLayout layout_main = (LinearLayout) findViewById(ResourceUtil.getIdResIDByName(mContext, "layout_main"));
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        if (mViewType == TYPE_DIALOG) {//弹框模式
            window.getDecorView().setPadding(d * 90, d * 30, d * 90, d * 30);
            //layout_main.setPadding(d*5,0,d*5,d*5);
            layout_main.setBackgroundResource(ResourceUtil.getDrawableResIDByName(mContext, "web_bg_r4"));
            ((RelativeLayout.LayoutParams) webView.getLayoutParams()).setMargins(5 * d, 5 * d, 5 * d, 5 * d);
        } else {//全屏模式
            window.getDecorView().setPadding(0, 0, 0, 0);
            layout_main.setBackgroundColor(Color.WHITE);
            ((RelativeLayout.LayoutParams) webView.getLayoutParams()).setMargins(0, 0, 0, 0);
            //layout_main.setPadding(0,0,0,0);
        }

        setFullScreen();

        Log.e("查看数据：", " " + mUrl);

        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setAttributes(lp);
        //mUrl="file:///android_asset/demo.html";
        weiboView();

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CookieSyncManager.createInstance(mContext);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                CookieSyncManager.getInstance().sync();
                webView.setWebChromeClient(null);
                webView.setWebViewClient(null);
                webView.clearCache(true);
            }
        });

        // 1. 实例化BroadcastReceiver子类 &  IntentFilter
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("closeWeb")) {
                    dismiss();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        // 2. 设置接收广播的类型
        intentFilter.addAction("closeWeb");
        // 3. 动态注册：调用Context的registerReceiver（）方法
        mContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
//        if (v.getId() == R.id.btn_back) {
//            dismiss();
//        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        /*if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }*/
    }


    private void weiboView() {
        try {
            Log.w("url", mUrl);
            WebViewUtils.initWebView(webView, mUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            dismiss();
        }

        webView.setWebViewClient(new BaseWebViewClient(mContext, webView));
        webView.setWebChromeClient(myWebChromeClient);
        //webView.addJavascriptInterface(new JSInterface(),"js");
        initJSBridge((Activity) mContext);
    }


    /**
     * jsBridge交互设置，注册handler
     *
     * @param activity
     */
    private void initJSBridge(final Activity activity) {

        webView.registerHandler("JsHandlerApp", new BridgeHandler() {
            @Override
            public void handler(String data, final CallBackFunction function) {
                Log.w("urlHandler", data + "");
                if (!TextUtils.isEmpty(data)) {
                    try {
                        final JSONObject json = new JSONObject(data);
                        String handler = json.getString("handler");
                        JSONObject params;
                        try {
                            params = json.getJSONObject("params");
                        } catch (Exception e) {
                            params = new JSONObject();
                        }
                        switch (handler) {
                            case "J01"://仅仅关闭当前网页
                                dismiss();
                                break;
                            case "J02"://关闭当前网页，返回登录界面
                                dismiss();

                                break;
                            case "J03"://退出登录

                                break;
                            case "J04"://跳转到一个新窗体
                                String url_J04 = params.getString("action");
                                try {
                                    JSONObject data_J04 = params.getJSONObject("data");
                                    if (data_J04.length() > 0) {
                                        joinUrl(url_J04, data_J04);
                                    }
                                } catch (Exception e) {

                                }
                                DialogWeb dialogWeb = new DialogWeb(mContext, url_J04, params.getInt("screen"));
                                dialogWeb.show();
                                break;
                            case "J05"://请求一个接口

                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        /*webView.callHandler("AppHandlerJs","ddd", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Log.e("TTTAAA", "来到了这里");
                Toast.makeText(mContext,data, Toast.LENGTH_LONG).show();
            }
        });*/

    }

    private static String joinUrl(String url, JSONObject jsonObject) {
        if (!url.contains("?")) {
            url += "?";
        }
        try {
            Iterator iterator = jsonObject.keys();
            int i = 0;
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = jsonObject.getString(key);
                if (i == 0) {
                    url += key + "=" + value;
                } else {
                    url += "&" + key + "=" + value;
                }
                i++;
            }
        } catch (Exception e) {

        }
        return url;
    }

    /**
     * app调js
     */
    private void callHandler(String handlerName, String data) {
        webView.callHandler(handlerName, data, new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Log.w("callHandler_json", "callback" + data);

            }
        });

    }


    /**
     * web native交互处理
     */
 /*   private void initAction(String action, String handler) {
        if (!TextUtils.isEmpty(action)) {
            try {
                WebToNative webToNative = new WebToNative();
                webToNative.init(this, Des3.decode(action), handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

//js交互接口
    private final class JSInterface {
        /*@JavascriptInterface
        public void close(){//关闭页面
            dismiss();
        }
        @JavascriptInterface
        public void login(){//跳到登录页面
            QHManager.showLoginUI(mContext,QHManager.getCallBackListener());
        }*/
    }


    MyWebChromeClient myWebChromeClient = new MyWebChromeClient(mContext) {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);//加载完网页进度条消失
            } else {
                progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                progressBar.setProgress(newProgress);//设置进度值
            }
        }

    };
}