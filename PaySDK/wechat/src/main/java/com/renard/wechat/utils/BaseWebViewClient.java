package com.renard.wechat.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public class BaseWebViewClient extends BridgeWebViewClient {
    private Context mContext;
    private Activity mAtivity;
    private BridgeWebView mwebView;

    public BaseWebViewClient(Context context, BridgeWebView webView) {
        super(webView);
        mContext = context;
        mwebView = webView;
        mAtivity = (Activity) context;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.i("debug", "onPageStarted,url=" + url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {
        Log.d("OverrideUrlLoading", url);

        if (url.startsWith("tel:")) {//电话
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        }

        if (url.startsWith("weixin://wap/pay?")) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                sendBroad();
                return true;
            } catch (ActivityNotFoundException e) {
                Log.w("sh", "没有安装微信");
            }
        }
        if (url.startsWith("alipays:") || url.startsWith("alipay")) {
            try {
                mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
            } catch (Exception e) {
                new AlertDialog.Builder(mContext)
                        .setMessage("未检测到支付宝客户端，请安装后重试。")
                        .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                mContext.startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                            }
                        }).setNegativeButton("取消", null).show();
            }
            return true;
        }

        /*if (!(url.startsWith("http") || url.startsWith("https"))) {
            return true;
        }*/

        /**
         * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
         */
        /*final PayTask task = new PayTask((Activity) mContext);
        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
            @Override
            public void onPayResult(final H5PayResultModel result) {
                final String url=result.getReturnUrl();
                if(!TextUtils.isEmpty(url)){
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.loadUrl(url);
                        }
                    });
                }
            }
        });

        if(!isIntercepted) {
            return super.shouldOverrideUrlLoading(view, url);
        }*/
        if (url.contains("alipays://platformapi")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                Log.w("sh", "没有安装支付宝");
            }
        }

        return super.shouldOverrideUrlLoading(view, url);

    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        Log.d("debug", "WebView,errorCode=" + errorCode + "failingUrl=" + failingUrl);
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        new DialogSSlError(mContext, handler);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.d("onPageFinished", url);
    }

    private void sendBroad() {
        Intent mIntent = new Intent("closeWeb");
        mContext.sendBroadcast(mIntent);
    }
}
