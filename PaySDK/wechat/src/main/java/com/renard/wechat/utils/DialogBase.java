package com.renard.wechat.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public class DialogBase extends Dialog {
    Context mContext;
    public DialogBase(Context context) {
        super(context);
        mContext=context;
        setCanceledOnTouchOutside(false);
    }
    public DialogBase(Context context,int style) {
        super(context, style);
        mContext=context;
        setCanceledOnTouchOutside(false);
    }

    public Activity getActivity(){
        return (Activity)mContext;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void dismiss(){
        Activity activity=(Activity)mContext;
        if (activity.isDestroyed() || activity.isFinishing()){

        }else{
            super.dismiss();
        }
    }

    /**
     * 设置窗口大小
     */
    public void setFullScreen(){
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity=Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        if(DensityUtils.isScreenOriatationPortrait(mContext)){//竖屏
            window.getDecorView().setPadding(DensityUtils.dip2px(mContext,30),DensityUtils.dip2px(mContext,30), DensityUtils.dip2px(mContext,30),DensityUtils.dip2px(mContext,30));
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            Log.w("dddddd", "竖屏");
        }else{//横屏
            window.getDecorView().setPadding(DensityUtils.dip2px(mContext,90),DensityUtils.dip2px(mContext,30), DensityUtils.dip2px(mContext,90),DensityUtils.dip2px(mContext,30));
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            Log.w("dddddd","横屏");
        }
        window.setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        window.setAttributes(lp);
    }
}