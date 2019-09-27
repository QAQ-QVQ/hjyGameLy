package com.renard.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import com.fzoplay.game.sdk.api.FZOApiFactory;
import com.fzoplay.game.sdk.bean.FZOExitResult;
import com.fzoplay.game.sdk.bean.FZOHttpResultUtil;
import com.fzoplay.game.sdk.bean.FZOLoginResult;
import com.fzoplay.game.sdk.bean.FZOOrderInfo;
import com.fzoplay.game.sdk.interfaces.FZOExitCallBack;
import com.fzoplay.game.sdk.interfaces.FZOLoginCallBack;
import com.renard.common.Interface.CallBackListener;
import com.renard.common.config.TypeConfig;
import com.renard.common.parse.channel.Channel;
import com.renard.common.utils.log.LogUtils;
import com.renard.paysdkss.ConfigUtils;
import com.renard.paysdkss.PayManager;
import com.renard.paysdkss.bean.CreateBackBean;
import com.renard.paysdkss.bean.CreateOrderBean;
import com.renard.paysdkss.bean.LoginBean;
import com.renard.paysdkss.bean.UploadBean;
import com.renard.paysdkss.listener.CreatOrderInterface;
import com.renard.paysdkss.listener.LoginInterface;
import com.renard.paysdkss.ui.PayActivity;


import java.util.HashMap;

public class Sy501SDK extends Channel {
    private final String TAG = getClass().getSimpleName();
    private CallBackListener loginCallBackListeners;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;
    private  static String uids;

    @Override
    protected void initChannel() {
        LogUtils.d(TAG, getClass().getSimpleName() + " has init");
    }

    @Override
    public String getChannelID() {
        return null;
    }

    @Override
    public boolean isSupport(int FuncType) {
        switch (FuncType) {

            case TypeConfig.FUNC_SWITCHACCOUNT:
                return true;

            case TypeConfig.FUNC_LOGOUT:
                return true;

            case TypeConfig.FUNC_SHOW_FLOATWINDOW:
                return false;

            case TypeConfig.FUNC_DISMISS_FLOATWINDOW:
                return false;

            default:
                return false;
        }
    }

    @Override
    public void init(final Context context, HashMap<String, Object> initMap, final CallBackListener initCallBackListener) {
        /**
         * 初始化，正式版改为false，也可以写在项目Application的OnCreate方法中，一定要在调用登陆或者支付之前初始化
         */
        FZOApiFactory.getFZOApi().init((Activity) context, "439",true);
        LogUtils.d(TAG, getClass().getSimpleName() + " init");
        PayManager.getInit("43","676","636e2ae74563351449cc8ac9450361ef");
        initOnSuccess(initCallBackListener);
    }

    @Override
    public void login(final Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        LogUtils.i(TAG, getClass().getSimpleName() + " login");
        if (ConfigUtils.isIssueLogin()){
            LoginBean loginBean=new LoginBean();
            PayManager.getLogin((Activity)context,loginBean);
        }else {
            LogUtils.i("login", "logintrue");
            /**
             * 登录操作
             */
            FZOApiFactory.getFZOApi().startLogin((Activity) context, new FZOLoginCallBack() {
                @Override
                public void callBack(FZOLoginResult result) {
                    switch (result.getCode()) {
                        case FZOHttpResultUtil.FZO_HTTP_RESULT_SUCC:
                            String uid = result.getUid();//用户ID
                            String token = result.getToken();//用户token
                            /**
                             * 游戏里面自己处理请求后的操作
                             */
                            LoginBean loginBean=new LoginBean();
                                loginBean.setToken(token);
                                loginBean.setUid(uid);
                                PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                                    @Override
                                    public void setUID(String uid) {
                                        uids = uid;
                                        loginOnSuccess(uid, loginCallBackListeners);

                                        Toast.makeText(context,"登陆成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void Closed(String msg) {
                                        loginOnFail(msg, loginCallBackListeners);
                                    }
                                });
                            break;
                        case FZOHttpResultUtil.FZO_HTTP_RESULT_FAIL:
                            String errMsg = result.getMsg();//失败的错误信息
                            loginOnFail(errMsg, loginCallBackListeners);
                            Toast.makeText(context,errMsg, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
        this.loginCallBackListeners = loginCallBackListener;
    }

    @Override
    public void switchAccount(final Context context, CallBackListener changeAccountCallBackLister) {
        this.switchAccountCallBackLister=changeAccountCallBackLister;
    }

    @Override
    public void logout(final Context context, final CallBackListener logoutCallBackLister) {
        LogUtils.i(TAG, getClass().getSimpleName() + " logout");
        FZOApiFactory.getFZOApi().exit((Activity) context, new FZOExitCallBack() {
            @Override
            public void callBack(FZOExitResult fzoExitResult) {
                switch (fzoExitResult.getCode()){
                    case FZOHttpResultUtil.FZO_HTTP_RESULT_SUCC:
                        //注销成功
                        logoutOnSuccess(logoutCallBackLister);
                        Toast.makeText(context,"注销成功", Toast.LENGTH_SHORT).show();
                        break;
                        case FZOHttpResultUtil.FZO_HTTP_RESULT_FAIL:
                            logoutOnFail(fzoExitResult.getMsg(),logoutCallBackLister);
                            Toast.makeText(context,fzoExitResult.getMsg(), Toast.LENGTH_SHORT).show();
                            break;
                }
            }
        });
        this.loginCallBackListeners = logoutCallBackLister;
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, final CallBackListener payCallBackListener) {
        LogUtils.i(TAG, getClass().getSimpleName() + " pay");
        //传递到平台
        CreateOrderBean createOrderBean=new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId((String) payMap.get("productID"));
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(payMap.get("money")));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));
        //  PayManager.getCreateOrder((Activity)context,createOrderBean);
        if (com.renard.paysdkss.ConfigUtils.isIssuePay()) {
            Intent intent = new Intent(context, PayActivity.class);
            intent.putExtra("creatorder", createOrderBean);
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, 1001);
        } else {
            LogUtils.i(TAG, getClass().getSimpleName() + " paytrue");
            PayManager.getActivityCreateOrder(createOrderBean, new CreatOrderInterface() {
                @Override
                public void CreatOrder(CreateBackBean createBackBean) {

                }

                @Override
                public void PayOrder()  {
                    LogUtils.i(TAG, "paymap:" + payMap.toString());
                    /**
                     * 支付操作
                     */
                    FZOOrderInfo orderInfo = new FZOOrderInfo();
//                    orderInfo.setGameID("433");//游戏ID，平台分配的id
                    orderInfo.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));//时间戳，精确到秒
                    orderInfo.setPayMoney(String.valueOf(payMap.get("money")));//价格 单位元
                    orderInfo.setOrderID(String.valueOf(payMap.get("gorder")));//订单ID
                    orderInfo.setAccount(uids);//游戏中的账号
                    orderInfo.setSlogan(String.valueOf(payMap.get("extraInfo")));//弹出框中的一句话
                    FZOApiFactory.getFZOApi().startPay((Activity) context, orderInfo);
                }
            });
        }
        this.payCallBackListener=payCallBackListener;

    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
        LogUtils.i(TAG, getClass().getSimpleName() + " exit");
        channelNotExitDialog(exitCallBackLister);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        //传递到平台
        UploadBean uploadBean=new UploadBean();
        uploadBean.setIsCreateRole(String.valueOf(dataMap.get("create")));
        uploadBean.setGameId("585");
        uploadBean.setServerId((String) dataMap.get("serverId"));
        uploadBean.setServerName((String) dataMap.get("serverName"));
        uploadBean.setUserRoleId((String) dataMap.get("roleId"));
        uploadBean.setUserRoleName((String) dataMap.get("roleName"));
        uploadBean.setVipLevel((String) dataMap.get("roleVipLevel"));
        PayManager.getUpload(uploadBean);

    }
    /******************必接sdk生命周期 start***********************/
    @Override
    public void onResume(Context context) {
        super.onResume(context);

    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);

    }

    @Override
    public void onStart(Context context) {
        // TODO Auto-generated method stub
        super.onStart(context);

    }
    @Override
    public void onStop(Context context) {
        super.onStop(context);

    }

    @Override
    public void onDestroy(Context context) {
        super.onDestroy(context);

    }

    @Override
    public void onConfigurationChanged(Context context,Configuration newConfig) {
        super.onConfigurationChanged(context,newConfig);

    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(context,requestCode, resultCode, data);

        if (requestCode == 1000) {   //登录1000，300   支付1001，400
            if (resultCode==300){
                if (data.getStringExtra("uid")!=null){
                    String uid=data.getStringExtra("uid");
                    loginOnSuccess(uid,loginCallBackListeners);
                }else {
                    loginOnFail("登录失败",loginCallBackListeners);
                }
            }
        }
        if (requestCode == 1001) {   //登录1000，300   支付1001，400
            if (resultCode==400){
                if (data.getIntExtra("code",201)==200){
                    payOnSuccess(payCallBackListener);
                }else {
                    payOnFailure(payCallBackListener);
                }
            }
        }
    }


}
