package com.renard.wechat.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.webkit.SslErrorHandler;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public class DialogSSlError {
    public DialogSSlError(Context context, final SslErrorHandler handler){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("是否通过证书验证?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (handler != null) {
                    handler.proceed();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (handler != null) {
                    handler.cancel();
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
