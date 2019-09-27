package com.renard.project;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.renard.account.AccountManager;
import com.renard.account.bean.AccountCallBackBean;
import com.renard.common.Interface.CallBackListener;
import com.renard.common.cache.BaseCache;
import com.renard.common.config.TypeConfig;
import com.renard.common.google.gson.JsonObject;
import com.renard.common.parse.channel.Channel;
import com.renard.common.parse.channel.ChannelManager;
import com.renard.common.parse.project.Project;
import com.renard.common.utils.ErrorCode;
import com.renard.common.utils.log.LogUtils;
import com.renard.initmanager.InitManager;
import com.renard.purchase.PurchaseManager;
import com.renard.purchase.PurchaseResult;

import java.util.HashMap;

public class SyProject extends Project {
    private final String TAG = getClass().getSimpleName();

    //渠道对象
    private Channel channel;

    private Activity mActivity;
    @Override
    protected void initProject() {
        LogUtils.d(TAG, getClass().getSimpleName() + " has init");
        super.initProject();
    }
    /******************************************  初始化   ****************************************/
    @Override
    public void init(final Activity activity, String gameid, String gamekey, final CallBackListener callBackListener) {
        LogUtils.d(TAG,"init");

        if (activity == null || callBackListener == null) {
            callBackListener.onFailure(ErrorCode.PARAMS_ERROR,"activity or callBackListener 为空");
            return;
        }

        //设置账号监听
        AccountManager.getInstance().setLoginCallBackLister(projectAccountCallBackListener);

        InitManager.getInstance().init(activity, gameid, gamekey, new CallBackListener() {

            @Override
            public void onSuccess(Object o) {

                channel = ChannelManager.getInstance().getChannel(); //注意渠道配置文件有没有加载

                channel.init(activity, null, new CallBackListener() {

                    @Override
                    public void onSuccess(Object o) {

                        //项目SDK初始化成功后，调用渠道SDK初始化。
                        InitManager.getInstance().setInitState(true);
                        callBackListener.onSuccess(o);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        InitManager.getInstance().setInitState(false);
                        callBackListener.onFailure(code,msg);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                callBackListener.onFailure(code,msg);
            }
        });
    }


    /******************************************      账号      ****************************************/


    /*** SDKApi层设置回调监听*/
    private CallBackListener ApiAccountCallback;

    @Override
    public void setAccountCallBackLister(CallBackListener callBackLister) {
        ApiAccountCallback = callBackLister;
    }

    /**
     * 监听AccountManager登录、切换账号、绑定、注销的回调信息
     */
    private CallBackListener projectAccountCallBackListener = new CallBackListener<AccountCallBackBean>() {

        @Override
        public void onSuccess(AccountCallBackBean callBackBean) {
            ApiAccountCallback.onSuccess(callBackBean);
        }

        @Override
        public void onFailure(int code, String msg) {
            //不会走到这里来
        }
    };

    private void AccountOnFailCallBack(int event, int code, String msg){

        AccountCallBackBean callBackBean = new AccountCallBackBean();
        callBackBean.setEvent(event);
        callBackBean.setErrorCode(code);
        callBackBean.setMsg(msg);
        ApiAccountCallback.onSuccess(callBackBean);
    }


    /**
     * 监听渠道Channel的登录、切换账号、绑定、注销的回调信息
     */
    private CallBackListener authCallBackListener = new CallBackListener<HashMap<String,Object>>() {


        @Override
        public void onSuccess(HashMap<String,Object> loginAuthData) {
            LogUtils.debug_d(TAG,channel.getClass().getSimpleName() + " = " + loginAuthData.toString());

            int type = (int) loginAuthData.get(Channel.PARAMS_OAUTH_TYPE);
            //类型为登录、切换账号就走服务器登录逻辑
            if (type == TypeConfig.LOGIN ){
                AccountManager.getInstance().authLogin(mActivity,loginAuthData);

            }else if (type == TypeConfig.SWITCHACCOUNT){
                AccountManager.getInstance().switchAccount(mActivity);
            } else if (type == TypeConfig.LOGOUT){ //类型为登出
                AccountManager.getInstance().logout(mActivity);
            }
        }

        @Override
        public void onFailure(int code, String msg) {
            LogUtils.debug_d(TAG,channel.getClass().getSimpleName() + msg);
            //做失败处理
            switch (code){
                case ErrorCode.CHANNEL_LOGIN_FAIL: //登录失败
                    AccountOnFailCallBack(TypeConfig.LOGIN, ErrorCode.FAILURE,msg);
                    break;

                case ErrorCode.CHANNEL_LOGIN_CANCEL: //登录取消
                    AccountOnFailCallBack(TypeConfig.LOGIN, ErrorCode.CANCEL,msg);
                    break;

                case ErrorCode.CHANNEL_SWITCH_ACCOUNT_FAIL: //切换账号失败
                    AccountOnFailCallBack(TypeConfig.SWITCHACCOUNT, ErrorCode.FAILURE,msg);
                    break;

                case ErrorCode.CHANNEL_SWITCH_ACCOUNT_CANCEL: //切换账号取消
                    AccountOnFailCallBack(TypeConfig.SWITCHACCOUNT, ErrorCode.CANCEL,msg);
                    break;

                case ErrorCode.CHANNEL_LOGOUT_FAIL:// 注销失败
                    AccountOnFailCallBack(TypeConfig.LOGOUT, ErrorCode.FAILURE,msg);
                    break;

                case ErrorCode.CHANNEL_LOGOUT_CANCEL: //注销取消
                    AccountOnFailCallBack(TypeConfig.LOGOUT, ErrorCode.CANCEL,msg);
                    break;
            }
        }
    };

    @Override
    public void login(Activity activity, HashMap<String, Object> loginParams) {
        LogUtils.d(TAG,"login");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(activity,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        if (activity == null ) {
            AccountOnFailCallBack(TypeConfig.LOGIN,ErrorCode.PARAMS_ERROR,"activity 为空");
            return;
        }

        mActivity = activity;
        channel.login(activity, loginParams, authCallBackListener);
        channel.switchAccount(activity,authCallBackListener);
    }



    @Override
    public void switchAccount(Activity activity) {
        LogUtils.d(TAG,"switchAccount");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(activity,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!AccountManager.getInstance().getLoginState()){
            AccountOnFailCallBack(TypeConfig.SWITCHACCOUNT,ErrorCode.NO_LOGIN,"account has not login");
            return;
        }

        if (activity == null ) {
            AccountOnFailCallBack(TypeConfig.SWITCHACCOUNT,ErrorCode.PARAMS_ERROR,"activity 为空");
            return;
        }

        mActivity = activity;

        if (isSupport(TypeConfig.FUNC_SWITCHACCOUNT)){
            channel.switchAccount(activity,authCallBackListener);

        }else {
            Toast.makeText(activity,"没有实现该方法",Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void logout(Activity activity) {
        LogUtils.d(TAG,"logout");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(activity,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!AccountManager.getInstance().getLoginState()){
            AccountOnFailCallBack(TypeConfig.LOGOUT,ErrorCode.NO_LOGIN,"account has not login");
            return;
        }

        if (activity == null ) {
            AccountOnFailCallBack(TypeConfig.LOGIN,ErrorCode.PARAMS_ERROR,"activity 为空");
            return;
        }

        if (isSupport(TypeConfig.FUNC_LOGOUT)){
            channel.logout(activity, authCallBackListener);

        }else {
            Toast.makeText(activity,"没有实现该方法",Toast.LENGTH_SHORT).show();
        }
    }


    /******************************************      购买      ****************************************/

    @Override
    public void pay(final Activity activity, final HashMap<String, Object> payParams, final CallBackListener callBackListener) {
        LogUtils.d(TAG,"pay");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(activity,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!AccountManager.getInstance().getLoginState()){
            callBackListener.onFailure(ErrorCode.NO_LOGIN,"account has not login");
            return;
        }

        if (activity == null || callBackListener == null) {
            callBackListener.onFailure(ErrorCode.PARAMS_ERROR,"activity or callBackListener is null");
            return;
        }


        PurchaseManager.getInstance().createOrderId(activity, payParams, new CallBackListener() {

            @Override
            public void onSuccess(Object object) {

                //返回订单号
                String orderID = (String) object;

                //创建订单成功，回调订单信息和CP的透传信息
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderId",orderID);
                PurchaseResult purchaseResult = new PurchaseResult(PurchaseResult.OrderState,jsonObject);
                callBackListener.onSuccess(purchaseResult);

                payParams.put("orderId",orderID);

                channel.pay(activity, payParams, new CallBackListener() {

                    @Override
                    public void onSuccess(Object object) {

                        //支付结果回调到这里来
                        PurchaseResult purchaseResult = new PurchaseResult(PurchaseResult.PurchaseState,null);
                        callBackListener.onSuccess(purchaseResult);

                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        callBackListener.onFailure(code,msg);
                    }
                });

            }

            @Override
            public void onFailure(int code, String msg) {
                callBackListener.onFailure(code,"create orderId fail");
            }
        });

    }


    @Override
    public void showFloatView(Activity activity) {
        LogUtils.d(TAG,"showFloatView");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(activity,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        mActivity = activity;

        if (isSupport(TypeConfig.FUNC_SHOW_FLOATWINDOW)){
            channel.showFloatView(activity);

        }else {
            Toast.makeText(activity,"没有实现该方法",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void dismissFloatView(Activity activity) {
        LogUtils.d(TAG,"dismissFloatView");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(activity,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        mActivity = activity;

        if (isSupport(TypeConfig.FUNC_DISMISS_FLOATWINDOW)){
            channel.dismissFloatView(activity);

        }else {
            Toast.makeText(activity,"没有实现该方法",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void exit(Activity activity, CallBackListener callBackListener) {
        LogUtils.d(TAG,"exit");

        if (activity == null || callBackListener == null) {
            callBackListener.onFailure(ErrorCode.PARAMS_ERROR,"activity or callBackListener is null");
            return;
        }

        mActivity = activity;

        channel.exit(activity,callBackListener);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        LogUtils.d(TAG,"reportData");

        if (!InitManager.getInstance().getInitState()){
            Toast.makeText(context,"请先初始化",Toast.LENGTH_SHORT).show();
            return;
        }

        channel.reportData(context,dataMap);
    }


    @Override
    public void extendFunction(Activity activity, int functionType, Object object, CallBackListener callBackListener) {
        super.extendFunction(activity, functionType, object, callBackListener);
    }


    /**
     * 是否支持该接口,由于个别渠道只简单实现登录、支付接口
     * @param FuncType
     * @return
     */
    public boolean isSupport(int FuncType) {
        return channel.isSupport(FuncType);
    }


    @Override
    public String getChannelID() {
        return BaseCache.getInstance().getChannelId();
    }

    /*************************************  生命周期接口 ****************************************/

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        LogUtils.d(TAG,"onCreate");

        if (InitManager.getInstance().getInitState()){
            super.onCreate(activity, savedInstanceState);
            channel.onCreate(activity,savedInstanceState);
        }
    }

    @Override
    public void onStart(Activity activity) {
        LogUtils.d(TAG,"onStart");
        if (InitManager.getInstance().getInitState()){
            super.onStart(activity);
            channel.onStart(activity);
        }
    }

    @Override
    public void onResume(Activity activity) {
        LogUtils.d(TAG,"onResume");

        if (InitManager.getInstance().getInitState()){
            super.onResume(activity);
            channel.onResume(activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        LogUtils.d(TAG,"onPause");
        if (InitManager.getInstance().getInitState()){
            super.onPause(activity);
            channel.onPause(activity);
        }
    }

    @Override
    public void onStop(Activity activity) {
        LogUtils.d(TAG,"onStop");
        if (InitManager.getInstance().getInitState()){
            super.onStop(activity);
            channel.onStop(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        LogUtils.d(TAG,"onDestroy");
        if (InitManager.getInstance().getInitState()){
            super.onDestroy(activity);
            channel.onDestroy(activity);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        LogUtils.d(TAG,"onActivityResult");
        if (InitManager.getInstance().getInitState()){
            super.onActivityResult(activity, requestCode, resultCode, data);
            channel.onActivityResult(activity,requestCode,resultCode,data);
        }
    }

    @Override
    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,int[] grantResults) {
        LogUtils.d(TAG,"onRequestPermissionsResult");
        if (InitManager.getInstance().getInitState()){
            super.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
            channel.onRequestPermissionsResult(activity,requestCode,permissions,grantResults);
        }
    }
}
